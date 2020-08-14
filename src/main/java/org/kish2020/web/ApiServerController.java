package org.kish2020.web;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kish2020.DataBase;
import org.kish2020.Kish2020Server;
import org.kish2020.MainLogger;
import org.kish2020.entity.LunchMenu;
import org.kish2020.entity.SimplePost;
import org.kish2020.utils.parser.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Controller
@RequestMapping("/api")
public class ApiServerController {
    public JSONArray testDates = new JSONArray();
    public String testDatesJson;
    public SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");

    private Kish2020Server main;
    private DataBase db;
    SimplePost simplePost;

    public ApiServerController(Kish2020Server kish2020Server){
        MainLogger.info("Api Server Controller 초기화중");
        this.main = kish2020Server;
        this.db = this.main.getDataBase();

        try {
            testDates.add(sdf.parse("2020-08-11").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.testDatesJson = testDates.toJSONString();
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
        if(this.simplePost == null) {
            MainLogger.info("불러오는 중");
             simplePost = KishWebParser.parseMenu("25").get(0);
        }
        return simplePost.getTitle() + "\n글쓴이 : " + simplePost.getAuthor() + "\n첨부파일 url" + simplePost.getAttachmentIconUrl();
    }

    @RequestMapping("/getExamDates")
    public @ResponseBody String getExamDates(){
        MainLogger.info("getExamDates 호출");
        return this.testDatesJson;
    }
}
