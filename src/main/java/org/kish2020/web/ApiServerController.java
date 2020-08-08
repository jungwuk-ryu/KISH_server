package org.kish2020.web;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kish2020.DataBase;
import org.kish2020.Kish2020Server;
import org.kish2020.MainLogger;
import org.kish2020.entity.LunchMenu;
import org.kish2020.utils.parsing.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
@RequestMapping("/api")
public class ApiServerController {
    private Kish2020Server main;
    private DataBase db;

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
    public @ResponseBody String getLunch(){
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

}
