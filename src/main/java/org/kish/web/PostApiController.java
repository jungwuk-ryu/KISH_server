package org.kish.web;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.kish.KishServer;
import org.kish.database.DataBase;
import org.kish.MainLogger;
import org.kish.MenuID;
import org.kish.entity.Post;
import org.kish.entity.PostInfo;
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
    public DataBase<PostInfo> postInfo;
    public LinkedHashMap<String, Post> loadedPosts = new LinkedHashMap<>();
    /* 검색 관련 */
    public DataBase<HashMap<String, Long>> postInKeyword;

    public PostApiController(KishServer main){
        this.main = main;
        this.postInfo = new DataBase<>("post/postInfoDB.json");
        this.postInKeyword = new DataBase<>("post/keywordDB.json");
        if(!this.postInKeyword.isLoaded()){
            this.postInKeyword.reload();
            if(!this.postInKeyword.isLoaded()) {
                MainLogger.error("postInKeywordDB를 불러올 수 없습니다.");
                Runtime.getRuntime().exit(0);
            }
        }
        if(!this.postInfo.isLoaded()){
            this.postInfo.reload();
            if(!this.postInfo.isLoaded()) {
                MainLogger.error("postInfoDB를 불러올 수 없습니다.");
                Runtime.getRuntime().exit(0);
            }
        }

        for(String key : this.postInfo.keySet()){
            Object postInfo = this.postInfo.get(key);
            if(postInfo instanceof JSONObject){     // 인텔리제이가 제거하라는데 안 해주면 안 됩니다 ㅠㅠㅠㅠ
                this.postInfo.put(key, new PostInfo((JSONObject) postInfo));
            }
        }

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
                if(Utils.isSavedPost(sp.getMenuId(), sp.getPostId())) break;
                if(loadedPosts.containsKey(sp.getMenuId() + "," + sp.getPostId())) continue;
                Post post = getPostFromServer(sp.getMenuId(), sp.getPostId());
                HashMap<String, String> data= new HashMap<>();
                data.put("type", "newPost");
                data.put("menuID", post.getMenuId());
                data.put("postID", post.getPostId());

                String menuName;
                try {
                    MenuID menu = Utils.getMenuFromID(post.getMenuId());
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

    /**
     * 저장된 게시물을 불러옵니다.
     */
    public Post getPost(String postKey){
        return this.getPost(postKey, true);
    }

    public Post getPost(String postKey, boolean saveOnShutdown){
        if(this.loadedPosts.containsKey(postKey)){
            return this.loadedPosts.get(postKey);
        }
        String[] tokens = postKey.split(",");
        if(!Utils.isSavedPost(tokens[0], tokens[1])) return null;
        Post post = new Post(tokens[0], tokens[1], saveOnShutdown);
        this.loadedPosts.put(postKey, post);
        return post;
    }

    /**
     * 간략한 게시물 정보를 불러옵니다
     */

    public PostInfo getPostInfo(String postKey){
        return this.postInfo.get(postKey);
    }

    public Post getPostFromServer(String menuId, String postID){
        MainLogger.info("서버에서 받아오는 중 : " + menuId + "," + postID);
        boolean isNew = !Utils.isSavedPost(menuId, postID);
        Post post = KishWebParser.parsePost(menuId, postID, false);
        if(post == null){
            if(Utils.isSavedPost(menuId, postID)){
                this.removePost(getPost(menuId + "," + postID));
            }
            return null;
        }
        if(isNew){
            registerPostKeywords(post);
            this.postInfo.put(post.getPostKey(), new PostInfo(post));
        }
        this.loadedPosts.put(post.getPostKey(), post);
        post.save();
        return post;
    }

    public void registerPostKeywords(Post post){
        Utils.addPostToKeyword(post.getPostKey(), post.getTitle(), post.getAttachmentUrlMap(), this.postInKeyword, Utils.getContentTokenMap(post.getContent()));
    }

    public void unRegisterPostKeyWords(Post post){
        Utils.removePostFromKeyword(post.getPostKey(), post.getTitle(), post.getAttachmentUrlMap(), this.postInKeyword, Utils.getContentTokenMap(post.getContent()));
    }

    public void removePost(Post post){
        post.remove();
        post.setDoSave(false);
        this.loadedPosts.remove(post.getPostKey());
        unRegisterPostKeyWords(post);
        this.postInfo.remove(post.getPostKey());
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
        JSONArray jsonArray = new JSONArray();
        MainLogger.info("메뉴 게시글 불러오는 중 : " + menuId);
        ArrayList<SimplePost> result = KishWebParser.parseMenu(menuId, page);
        jsonArray.addAll(result);
        Thread thread = new Thread(() -> {
            result.forEach(sp -> {
                String postKey = sp.getMenuId() + sp.getPostId();
                if(this.loadedPosts.containsKey(postKey)) return;
                if(!Utils.isSavedPost(sp.getMenuId(), sp.getPostId())){
                    getPostFromServer(sp.getMenuId(), sp.getPostId());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    MainLogger.error("", e);
                }
            });
        });
        thread.start();
        return jsonArray.toJSONString();
    }

    @RequestMapping("/searchPost")
    public @ResponseBody String searchPostApi(@RequestParam String keyword, @RequestParam int index){
        MainLogger.info("검색요청 : " + keyword);
        ArrayList<PostInfo> result = new ArrayList<>();
        Utils.search(this.postInKeyword, this.postInfo, keyword, index).forEach(postKey -> {
            PostInfo postInfo = this.getPostInfo(postKey);
            if(postInfo == null) return;
            result.add(postInfo);
        });
        return gson.toJson(result);
    }

    @RequestMapping(value = "/getPost")
    public @ResponseBody String getPostApi(@RequestParam String menuID, @RequestParam String postID){
        Post post = this.getPostFromServer(menuID, postID);
        Document doc = Utils.postToDocument(post);
        post.put("bodyHtml", KishWebParser.generatePostToNormal(doc));
        post.remove("registeredKeyword");
        post.remove("fullHtml");
        post.remove("content");
        return (new JSONObject(post).toJSONString());
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
                        if(Utils.isSavedPost(sp.getMenuId(), sp.getPostId())){
                            MainLogger.warn(sp.getMenuId() + "," + sp.getPostId() + "가 이미 저장되어있습니다. skip...");
                            continue;
                        }
                        if(this.loadedPosts.containsKey(sp.getMenuId() + "," + sp.getPostId())){
                            MainLogger.error(sp.getMenuId() + "," + sp.getPostId() + "가 이미 로드되어있습니다. skip...");
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
