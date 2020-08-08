package org.kish2020.web;

import org.kish2020.DataBase;
import org.kish2020.Kish2020Server;
import org.kish2020.MainLogger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
