package org.kish2020.web;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kish2020.DataBase.DataBase;
import org.kish2020.MainLogger;
import org.kish2020.MenuID;
import org.kish2020.entity.Post;
import org.kish2020.entity.PostInfo;
import org.kish2020.entity.SimplePost;
import org.kish2020.utils.Utils;
import org.kish2020.utils.parser.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/api/post")
public class PostApi {
    public DataBase<PostInfo> postInfo;
    public LinkedHashMap<String, Post> loadedPosts = new LinkedHashMap<>();
    /* 검색 관련 */
    public DataBase<HashMap<String, Long>> postInKeyword;
    public LinkedHashMap<String, HashSet<String>> tempSrcResult = new LinkedHashMap<>();   //검색어, 결과

    public PostApi(){
        this.postInfo = new DataBase<>("post/postInfoDB.json");
        this.postInKeyword = new DataBase<HashMap<String, Long>>("post/keywordDB.json");
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

        // TODO : 이 부분 해결하기,,
        for(String key : this.postInfo.keySet()){
            Object postInfo = this.postInfo.get(key);
            if(postInfo instanceof JSONObject){     // 인텔리제이가 제거하라는데 안 해주면 안됩니다 ㅠㅠㅠㅠ
                this.postInfo.put(key, new PostInfo((JSONObject) postInfo));
            }
        }
        //this.savedPost = (HashSet<String>) db.getOrDefault("savedPost", new HashSet<>());
        this.postInKeyword.setDataChangeListener(() -> {
            if(!tempSrcResult.isEmpty()) tempSrcResult.clear();
        });

        Timer scheduler = new Timer();
        scheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                checkNewPost();
            }
        }, 1000 * 60 * 60, 1000 * 60 * 30);     // 30분 마다 반복
    }

    public void checkNewPost(){
        int postCount = 0;
        MainLogger.info("task 실행");
        for(MenuID menuId : MenuID.values()) {
            String id = menuId.id;
            ArrayList<SimplePost> list = KishWebParser.parseMenu(id, "1");
            if (list.size() < 1) break;
            for (SimplePost sp : list) {
                if(Utils.isSavedPost(sp.getMenuId(), sp.getPostId())){
                    break;
                }
                if(loadedPosts.containsKey(sp.getMenuId() + "," + sp.getPostId())){
                    continue;
                }
                getPostFromServer(sp.getMenuId(), sp.getPostId());
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
        if(this.loadedPosts.containsKey(postKey)){
            return this.loadedPosts.get(postKey);
        }
        String[] tokens = postKey.split(",");
        if(!Utils.isSavedPost(tokens[0], tokens[1])) return null;
        Post post = new Post(tokens[0], tokens[1]);
        this.loadedPosts.put(postKey, post);
        return post;
    }

    /**
     * 간략한 게시물 정보를 불러옵니다
     */

    public PostInfo getPostInfo(String postKey){
        PostInfo postInfo = this.postInfo.get(postKey);
        return postInfo;
    }

    public Post getPostFromServer(String menuId, String postID){
        MainLogger.info("서버에서 받아오는 중 : " + menuId + "," + postID);
        boolean isNew = false;
        if(!Utils.isSavedPost(menuId, postID)) isNew = true;
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

    /* 테스트용 코드입니다 */
    @RequestMapping("/d")
    public @ResponseBody String dTest(@RequestParam String bno, @RequestParam String menu){
        //this.parseAllPosts();
        return "ok";
    }

    @RequestMapping("/searchPost")
    public @ResponseBody String searchPostApi(@RequestParam String keyword){
        MainLogger.info("검색요청 : " + keyword);
        JSONArray array = new JSONArray();
        if(!this.tempSrcResult.containsKey(keyword)) {
            Utils.search(this.postInKeyword, keyword).forEach(((key, value) -> {
                for(String postKey : value){
                    PostInfo postInfo = this.getPostInfo(postKey);
                    if(postInfo == null) continue;
                    array.add(postInfo);
                }
            }));
        }
        return array.toJSONString();
    }

    /**
     * 검색기능을 위해 학교 홈페이지 내 모든 게시글을 불러옵니다
     */
    private void parseAllPosts(){
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
