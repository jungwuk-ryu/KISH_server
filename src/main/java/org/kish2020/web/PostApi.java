package org.kish2020.web;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kish2020.DataBase.DataBase;
import org.kish2020.DataBase.ExpandedDataBase;
import org.kish2020.MainLogger;
import org.kish2020.MenuID;
import org.kish2020.entity.Post;
import org.kish2020.entity.SimplePost;
import org.kish2020.utils.Utils;
import org.kish2020.utils.parser.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.rmi.CORBA.Util;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/api/post")
public class PostApi {
    public ExpandedDataBase db;
    public LinkedHashMap<String, Post> loadedPosts = new LinkedHashMap<>();
    /* 검색 관련 */
    public DataBase<HashMap<String, Long>> postInKeyword;
    public LinkedHashMap<String, HashSet<String>> tempSrcTemp = new LinkedHashMap<>();   //검색어, 결과

    public PostApi(){
        this.postInKeyword = new DataBase<HashMap<String, Long>>("post/keywordDB.json");
        if(!this.postInKeyword.isLoaded()){
            this.postInKeyword.reload();
            if(!this.postInKeyword.isLoaded()) {
                MainLogger.error("postDB를 불러올 수 없습니다.");
                Runtime.getRuntime().exit(0);
            }
        }

        //this.savedPost = (HashSet<String>) db.getOrDefault("savedPost", new HashSet<>());
        this.postInKeyword.setDataChangeListener(() -> {
            if(!tempSrcTemp.isEmpty()) tempSrcTemp.clear();
        });
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

    public Post getPostFromServer(String menuId, String postID){
        MainLogger.info("서버에서 받아오는 중 : " + menuId + "," + postID);
        boolean isNew = false;
        if(!Utils.isSavedPost(menuId, postID)) isNew = true;
        Post post = KishWebParser.parsePost(menuId, postID);
        if(post == null){
            if(Utils.isSavedPost(menuId, postID)){
                this.removePost(getPost(menuId + "," + postID));
            }
            return null;
        }
        if(isNew) registerPostKeywords(post);
        this.loadedPosts.put(post.getPostKey(), post);
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
        /*LinkedHashMap<String, Integer> m = Utils.getContentTokenMap(KishWebParser.getPostRawContent(
                "http://hanoischool.net/default.asp?board_mode=view&menu_no=" + menu + "&bno=" + bno
        ));*/
        Post post = KishWebParser.parsePost(menu, bno);
        Utils.addPostToKeyword(post.getPostKey(), post.getTitle(), post.getAttachmentUrlMap(), this.postInKeyword, Utils.getContentTokenMap(post.getContent()));
        MainLogger.warn(post.getPostKey());
        return new JSONObject(post).toJSONString();
    }

    @RequestMapping("/searchPost")
    public @ResponseBody String searchPostApi(@RequestParam String keyword){
        MainLogger.info("검색요청 : " + keyword);
        JSONArray array = new JSONArray();
        if(!this.tempSrcTemp.containsKey(keyword)) {
            Utils.search(this.postInKeyword, keyword).forEach(((key, value) -> {
                for(String postKey : value){
                    Post post = getPost(postKey);
                    if(post == null) continue;
                    array.add(post);
                }
            }));
        }
        return array.toJSONString();
    }
}
