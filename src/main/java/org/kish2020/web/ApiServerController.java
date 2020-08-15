package org.kish2020.web;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kish2020.DataBase;
import org.kish2020.Kish2020Server;
import org.kish2020.MainLogger;
import org.kish2020.entity.LunchMenu;
import org.kish2020.entity.SimplePost;
import org.kish2020.utils.WebUtils;
import org.kish2020.utils.parser.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Controller
@RequestMapping("/api")
public class ApiServerController {
    public JSONArray testDates = new JSONArray();
    public String testDatesJson;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private Kish2020Server main;
    private DataBase db;
    SimplePost simplePost;

    public ApiServerController(Kish2020Server kish2020Server){
        MainLogger.info("Api Server Controller 초기화중");
        this.main = kish2020Server;
        this.db = this.main.getMainDataBase();

        try {
            testDates.add(sdf.parse("2020-09-07").getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.testDatesJson = testDates.toJSONString();
    }

    /**
     * <p>날씨 정보를 얻습니다</p>
     * <p>추후 API 내용이 변경될 가능성이 있기때문에 클라이언트가 직접 api를 조회하지 않고
     * 서버측에 요청합니다.</p>
     *
     * @param lat 위도(Latitude)
     * @param lon 경도(Longitude)
     */

    // TODO : quota 구현, Null check
    @RequestMapping("/getWeather")
    public @ResponseBody String getWeather(@RequestParam String lat, @RequestParam String lon){
        /*
        아래 API는 Meteorogisk institutt의 api입니다.
        관련 문서는 https://api.met.no/doc/ 을 참고하세요
        SAMPLE : https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=21.043611&lon=105.773763
        */

        String url = "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=" + lat + "&lon=" + lon;
        JSONObject result = WebUtils.getRequest(url);
        return result.toJSONString();
    }

    @RequestMapping("/getCount")
    public @ResponseBody String getCount(){
        int count = this.db.increase("count", 1);
        MainLogger.info("now count : " + count);
        return "{\"num\":" + count + "}";
    }

    @RequestMapping("/getLunch")
    public @ResponseBody String getLunch(@RequestParam(required = false, defaultValue = "") String date){ // TODO : 캐싱
        ArrayList<LunchMenu> list = KishWebParser.parseLunch(date);
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
