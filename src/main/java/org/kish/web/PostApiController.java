package org.kish.web;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kish.KishServer;
import org.kish.MainLogger;
import org.kish.MenuID;
import org.kish.database.PostDao;
import org.kish.entity.Post;
import org.kish.entity.SimplePost;
import org.kish.utils.Utils;
import org.kish.utils.parser.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/api/post")
public class PostApiController {
    public static final Gson gson = new Gson();

    public final KishServer main;
    public final PostDao postDao;

    public PostApiController(KishServer main){
        this.main = main;
        this.postDao = KishServer.postDao;

        Timer scheduler = new Timer();
        scheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                checkNewPost();
            }
        }, 1000 * 60 * 5, 1000 * 60 * 30);     // 30분 마다 반복
    }

    public void checkNewPost(){
        int postCount = 0;

        for(MenuID menuId : MenuID.values()) {
            String id = menuId.id;
            ArrayList<SimplePost> list = KishWebParser.parseMenu(id, "1");

            if (list.size() < 1) break;
            for (SimplePost sp : list) {
                if(this.postDao.isExistPost(sp.getMenuId(), sp.getPostId())) break;
                Post post = getPostFromServer(sp.getMenuId(), sp.getPostId());
                HashMap<String, String> data = new HashMap<>();

                data.put("type", "newPost");
                data.put("menu", Integer.toString(post.getMenu()));
                data.put("id", Integer.toString(post.getId()));
                data.put("url", Utils.postUrlGenerator(post.getMenu(), post.getId()));

                String menuName;
                try {
                    MenuID menu = Utils.getMenuFromID(Integer.toString(post.getMenu()));
                    menuName = menu.name;
                } catch(IllegalArgumentException e){
                    menuName = "알 수 없음";
                    MainLogger.error("", e);
                }

                this.main.getFirebaseManager().sendFCM("newPost", post.getTitle(),
                        "새 글이 올라왔습니다.\n메뉴 : " + menuName + "\n작성자 : " + post.getAuthor(), data);
                postCount++;

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    MainLogger.error("", e);
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                MainLogger.error("", e);
            }
        }

        MainLogger.info("새로운 " + postCount + "개의 게시물이 추가되었습니다.");
    }

    public Post getPostFromServer(int menu, int id){
        MainLogger.info("서버에서 받아오는 중 : " + menu + "," + id);

        boolean isNew = !this.postDao.isExistPost(menu, id);
        Post post = KishWebParser.parsePost(menu, id, false);

        if(post == null){
            if(!isNew) this.postDao.removePost(menu, id);
            return null;
        }

        if(isNew){
            this.postDao.insertPost(post);
        }else{
            this.postDao.updatePost(post);
        }

        return post;
    }

    @RequestMapping("/getMenuIds")
    public @ResponseBody String getMenuIdsApi(){
        JSONObject jsonObject = new JSONObject();
        for(MenuID menu : MenuID.values()){
            jsonObject.put(menu.name(), menu.id);
        }
        return jsonObject.toJSONString();
    }

    @RequestMapping("/getPostsFromMenu")
    public @ResponseBody String getPostsFromMenuApi(@RequestParam String menuId, @RequestParam(required = false, defaultValue = "1") String page){
        MainLogger.info("메뉴 게시글 불러오는 중 : " + menuId);
        JSONArray jsonArray = new JSONArray();

        ArrayList<SimplePost> result = KishWebParser.parseMenu(menuId, page);
        jsonArray.addAll(result);

        return jsonArray.toJSONString();
    }

    @RequestMapping("/searchPost")
    public @ResponseBody String searchPostApi(@RequestParam String keyword, @RequestParam int index){
        MainLogger.info("검색요청 : " + keyword);
        /*ArrayList<PostInfo> result = new ArrayList<>();
        Utils.search(this.postInKeyword, this.postInfo, keyword, index).forEach(postKey -> {
            PostInfo postInfo = this.getPostInfo(postKey);
            if(postInfo == null) return;
            result.add(postInfo);
        });
        return gson.toJson(result);*/
        return gson.toJson(this.postDao.searchPost(keyword, index));
    }

    @RequestMapping(value = "/getPost")
    public @ResponseBody String getPostApi(@RequestParam int menuID, @RequestParam int postID){
        Post post = this.postDao.selectPost(menuID, postID);
        if(post == null) post = this.getPostFromServer(menuID, postID);
        if(post == null) return "{ rpc: 404 }";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", post.getTitle());
        jsonObject.put("author", post.getAuthor());
        jsonObject.put("post_date", post.getPost_date());
        jsonObject.put("content", post.getContent());
        jsonObject.put("menu", post.getMenu());
        jsonObject.put("id", post.getId());
        jsonObject.put("rpc", 200);
        jsonObject.put("url", Utils.postUrlGenerator(menuID, postID));

        return (jsonObject.toJSONString());
    }

    /**
     * <p>검색기능을 위해 학교 홈페이지 내 모든 게시글을 불러옵니다</p>
     * <p>(최초 실행시 필요할 수 있음)</p>
     */
    public void parseAllPosts(){
        MainLogger.warn("이 작업 후 프로그램 재시작을 추천드립니다.");
        Thread thread = new Thread( () -> {
            int cnt = 0;
            int postCount = 0;
            int totalMenuCount =  MenuID.values().length;

            for(MenuID menuId : MenuID.values()){
                MainLogger.info((cnt++) + " / " + totalMenuCount);
                String id = menuId.id;
                int i = 1;

                while(true) {
                    ArrayList<SimplePost> list = KishWebParser.parseMenu(id, Integer.toString(i));
                    if(list.size() < 1) break;
                    for (SimplePost sp : list){
                        if(this.postDao.isExistPost(sp.getMenuId(), sp.getPostId())){
                            MainLogger.warn(sp.getMenuId() + "," + sp.getPostId() + "가 이미 저장되어있습니다. skip...");
                            continue;
                        }

                        this.getPostFromServer(sp.getMenuId(), sp.getPostId());
                        postCount++;

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            MainLogger.error("", e);
                        }
                    }
                    i++;
                }
            }
            MainLogger.info("총 " + postCount + "개의 게시물이 저장되었습니다.");
        });
        thread.start();
    }
}
