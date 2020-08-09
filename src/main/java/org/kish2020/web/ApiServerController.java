package org.kish2020.web;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kish2020.DataBase;
import org.kish2020.Kish2020Server;
import org.kish2020.MainLogger;
import org.kish2020.entity.LunchMenu;
import org.kish2020.entity.Post;
import org.kish2020.utils.parser.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
@RequestMapping("/api")
public class ApiServerController {
    private Kish2020Server main;
    private DataBase db;
    Post post;

    public ApiServerController(Kish2020Server kish2020Server){
        MainLogger.info("Api Server Controller 초기화중");
        this.main = kish2020Server;
        this.db = this.main.getDataBase();
    }

    @RequestMapping("/getCount")
    public @ResponseBody String getCount(){
        int count = this.db.increase("count", 1);
        MainLogger.info("now count : " + count);
        return "{\"num\":" + count + "}";
    }

    @RequestMapping("/getLunch")
    public @ResponseBody String getLunch(){ // TODO : 캐싱
        ArrayList<LunchMenu> list = KishWebParser.parseLunch("2020-07-1");
        JSONArray jsonArray = new JSONArray();
        for(LunchMenu menu : list){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("menu", menu.getMenu());
            jsonObject.put("detail", menu.getDetail());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();
    }

    @RequestMapping("/getPosts")
    public @ResponseBody String getPosts(){  // TODO : 캐싱
        if(this.post == null) {
            MainLogger.info("불러오는 중");
             post = KishWebParser.parseMenu("25").get(0);
        }
        return post.getTitle() + "\n글쓴이 : " + post.getAuthor() + "\n첨부파일 url" + post.getAttachmentUrl();
    }


}
