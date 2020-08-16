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
import org.kish2020.utils.Utils;
import org.kish2020.utils.parser.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/api/post")
public class PostApi {
    public ExpandedDataBase db;
    public HashSet<String> savedPost;
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

    public Post getPost(String postKey){
        if(!this.savedPost.contains(postKey)){
            return null;
        }
        JSONObject jsonObject;
        try {
             jsonObject = (JSONObject) new JSONParser().parse(FileUtils.readFileToString(new File("post/posts/" + postKey), StandardCharsets.UTF_8));
        } catch (IOException | ParseException e) {
            MainLogger.error("", e);
            return null;
        }
        return new Post(jsonObject);
    }

    @RequestMapping("/getMenuIds")
    public @ResponseBody String getMenuIdsApi(){
        JSONObject jsonObject = new JSONObject();
        for(MenuID menu : MenuID.values()){
            jsonObject.put(menu.name(), menu.id);
        }
        return jsonObject.toJSONString();
    }

    @RequestMapping("/getPosts")
    public @ResponseBody String getPostsApi(@RequestParam String menuId, @RequestParam(required = false, defaultValue = "1") String page){
        JSONArray jsonArray = new JSONArray();
        MainLogger.info("메뉴 게시글 불러오는 중 : " + menuId);
        jsonArray.addAll(KishWebParser.parseMenu(menuId, page));
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
        JSONArray array = new JSONArray();
        array.addAll(Utils.search(this.postInKeyword, keyword));
        return array.toJSONString();
    }
}
