package org.kish2020.web;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kish2020.DataBase.ExpandedDataBase;
import org.kish2020.MainLogger;
import org.kish2020.MenuID;
import org.kish2020.entity.Post;
import org.kish2020.utils.parser.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;

@Controller
@RequestMapping("/api/post")
public class PostApi {
    public ExpandedDataBase db;
    public HashSet<String> savedPost;
    /* 검색 관련 */
    public LinkedHashMap<String, HashSet<String>> postInKeyword;

    public PostApi(){
        this.db = new ExpandedDataBase("post/postDB.json");
        if(!this.db.isLoaded()){
            this.db.reload();
            if(!this.db.isLoaded()) {
                MainLogger.error("postDB를 불러올 수 없습니다.");
                Runtime.getRuntime().exit(0);
            }
        }

        this.postInKeyword = (LinkedHashMap<String, HashSet<String>>) db.get("postInKeyword");
        this.savedPost = (HashSet<String>) db.get("savedPost");
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
}
