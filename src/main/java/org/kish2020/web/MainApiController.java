package org.kish2020.web;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kish2020.dataBase.ExpandedDataBase;
import org.kish2020.Kish2020Server;
import org.kish2020.MainLogger;
import org.kish2020.entity.LunchMenu;
import org.kish2020.entity.SchoolCalendar;
import org.kish2020.utils.WebUtils;
import org.kish2020.utils.parser.KishWebParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/api")
public class MainApiController {
    public JSONArray testDates = new JSONArray();
    public String testDatesJson;
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private final Kish2020Server main;
    private final ExpandedDataBase db;
    private final ExpandedDataBase lunchLikesDB;
    private final SchoolCalendar calendar;

    public MainApiController(Kish2020Server kish2020Server){
        MainLogger.info("Api Server Controller 초기화중");
        this.main = kish2020Server;
        this.db = this.main.getMainDataBase();
        this.lunchLikesDB = new ExpandedDataBase("db/lunchLikesDB.json");
        try {
            testDates.add(sdf.parse("2020-09-07").getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.testDatesJson = testDates.toJSONString();
        this.calendar = new SchoolCalendar();
        this.makeCalendar();
    }

    /**
     * <p>날씨 정보를 얻습니다</p>
     * <p>추후 API 내용이 변경될 가능성이 있기때문에 클라이언트가 직접 api를 조회하지 않고
     * 서버측에 요청합니다.</p>
     *
     * @param lat 위도(Latitude)
     * @param lon 경도(Longitude)
     */

    @RequestMapping("/getWeather")
    public @ResponseBody String getWeather(@RequestParam String lat, @RequestParam String lon){
        /*
        아래 API는 Meteorogisk institutt의 api입니다.
        관련 문서는 https://api.met.no/doc/ 을 참고하세요
        SAMPLE : https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=21.043611&lon=105.773763
        */

        String url = "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=" + lat + "&lon=" + lon;
        JSONObject result = WebUtils.getRequestWithJsonResult(url);
        if(result == null){
            result = new JSONObject();
            result.put("result", "1");
            return result.toJSONString();
        }
        result.put("result", "0");
        return result.toJSONString();
    }

    @RequestMapping("/getCount")
    public @ResponseBody String getCount(){
        int count = this.db.increase("count", 1);
        MainLogger.info("now count : " + count);
        return "{\"num\":" + count + "}";
    }

    @RequestMapping("/getLunch")
    public @ResponseBody String getLunch(@RequestParam(required = false, defaultValue = "") String date){
        ArrayList<LunchMenu> list = KishWebParser.parseLunch(date);
        JSONArray jsonArray = new JSONArray();
        for(LunchMenu menu : list){
            HashSet<String> likes = (HashSet<String>) this.lunchLikesDB.getOrDefault(menu.getDate(), new HashSet<String>());
            menu.put("likes", likes.size());
            jsonArray.add(menu);
        }
        return jsonArray.toJSONString();
    }

    @RequestMapping(value = "/toggleLunchLikes", method = RequestMethod.POST)
    public @ResponseBody String toggleLunchLikes(@RequestParam String uid, @RequestParam String lunchDate, @RequestParam String method){
        JSONObject resultJson = new JSONObject();
        if(this.main.getFirebaseManager().isExistUser(uid)) {
            HashSet<String> likes = (HashSet<String>) this.lunchLikesDB.getOrDefault(lunchDate, new HashSet<String>());
            if(method.equals("add")) {
                resultJson.put("result", "0");
                likes.add(uid);
            }else{
                resultJson.put("result", "1");
                likes.remove(uid);
            }
            this.lunchLikesDB.put(lunchDate, likes);
            resultJson.put("num", likes.size());
        }else{
            resultJson.put("result", "500");
            resultJson.put("msg", "로그인 상태를 확인할 수 없습니다.");
        }
        return resultJson.toJSONString();
    }

    @RequestMapping("/subscribeNotification")
    public @ResponseBody String subscribeNoti(@RequestParam String topic, @RequestParam String token){
        this.main.getFirebaseManager().addNotificationUser(topic, token);
        return "";
    }

    @RequestMapping("/unsubscribeNotification")
    public @ResponseBody String unsubscribeNoti(@RequestParam String topic, @RequestParam String token){
        this.main.getFirebaseManager().removeNotificationUser(topic, token);
        return "";
    }

    @RequestMapping("/checkSubscription")
    public @ResponseBody String checkSubscription(@RequestParam String topic, @RequestParam String token){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", this.main.getFirebaseManager().isNotificationUser(topic, token) ? 0 : 1);
        return jsonObject.toJSONString();
    }

    @RequestMapping("/getExamDates")
    public @ResponseBody String getExamDates(){
        MainLogger.info("getExamDates 호출");
        return this.testDatesJson;
    }

    /**
     * <p>학사일정 API입니다.</p>
     * <p>http://www.hanoischool.net/?menu_no=41에 학사일정이 없는 관계로
     * 직접 입력한 데이터를 사용해야합니다...</p>*/
    @RequestMapping("/getCalendar")
    public @ResponseBody String getCalendar(){
        return this.calendar.getJson();
    }

    private void makeCalendar(){
        /*학사일정이 홈페이지에 등록되어있지 않기에 직접 입력해줍니다...*/
        if(calendar.isEmpty()) {
            calendar.add("2020-08-31", "1학기 종료")
                    .add("2020-09-01", "2학기 시작")
                    .add("2020-09-02", "외국어의 날")
                    .add("2020-09-04", "인문학에세이쓰기공모전")
                    .add("2020-09-11", "2학기 간부수련회")
                    .add("2020-09-12", "과학캠프")
                    .add("2020-09-14", "2학기 방과후 수업 시작")
                    .add("2020-09-21", "스포츠리그(스포츠교류)", 1)
                    .add("2020-10-01", "추석")
                    .add("2020-10-02","추석연휴")
                    .add("2020-10-06", "10-11학년 학부모 대상\n교육과정 및 진학설명회")
                    .add("2020-10-09", "한글날 행사")
                    .add("2020-10-20", "1차 지필평가", 3)
                    .add("2020-10-26", "스포츠클럽주간", 5)
                    .add("2020-10-30", "과학탐구포트폴리오")
                    .add("2020-10-30", "프리젠테이션발표대회")
                    .add("2020-11-04", "10학년 수학여행", 3)
                    .add("2020-11-05", "8학년 수학여행", 2)
                    .add("2020-11-09", "화재예방 및 재난대피 훈련주간", 5)
                    .add("2020-11-6", "현장체험학습")
                    .add("2020-11-18", "2차 학생 건강검진")
                    .add("2020-11-20", "KISH 문화제")
                    .add("2020-11-21", "KISH 음악회")
                    .add("2020-11-26", "총학생회 유세")
                    .add("2020-11-27", "총학생회 선거")
                    .add("2020-11-30", "교육과정 만족도조사")
                    .add("2020-12-01", "예비중∙고학생 OT")
                    .add("2020-12-04", "독서퀴즈대회")
                    .add("2020-12-15", "2차 지필평가", 4)
                    .add("2020-12-19", "전편입생 지필면접")
                    .add("2020-12-21", "스포츠클럽주간", 8)
                    .add("2020-12-24", "인문학에세이쓰기공모전")
                    .add("2020-12-25", "크리스마스")
                    .add("2020-12-31", "졸업진급사정회(4교시)")
                    .add("2021-01-01", "신정" )
                    .add("2021-01-07", "(오전) 종업식(2교시)")
                    .add("2021-01-07", "(오후) 졸업식")
                    .add("2021-01-08", "중등교사출근일")
                    .add("2021-01-08", "겨울방학(예정)", 24 + 28)
                    .add("2021-01-11", "방학 중 방과후 수업", 12)
                    .add("2021-02-06", "설날연휴(예정)",9)
                    .add("2021-02-22", "전교직원 출근일", 4)
                    .add("2021-02-23", "전교직원 워크숍", 2)
                    .commit();
        }
    }
}
