package org.kish.web;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kish.KishServer;
import org.kish.MainLogger;
import org.kish.database.KishDAO;
import org.kish.entity.LunchMenu;
import org.kish.utils.WebUtils;
import org.kish.utils.parser.KishWebParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.kish.KishServer.GSON;

@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/api")
public class MainApiController {
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private final KishServer main;
    private @Autowired KishDAO kishDao;

    public MainApiController(KishServer kishServer){
        MainLogger.info("Api Server Controller 초기화중");
        this.main = kishServer;

        MainLogger.info("학사일정 준비중");
        //this.makeCalendar();
        Timer scheduler = new Timer();
        scheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                makeCalendar();
            }
        }, 1000 * 60, 1000 * 60 * 50);
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

/*    @RequestMapping("/getCount")
    public @ResponseBody String getCount(){
        int count = this.db.increase("count", 1);
        MainLogger.info("now count : " + count);
        return "{\"num\":" + count + "}";
    }*/

    @RequestMapping("/getLunch")
    public @ResponseBody String getLunch(@RequestParam(required = false, defaultValue = "") String date){
        ArrayList<LunchMenu> list = KishWebParser.parseLunch(date);
        JSONArray jsonArray = new JSONArray();
        for(LunchMenu menu : list){
            //HashSet<String> likes = (HashSet<String>) this.lunchLikesDB.getOrDefault(menu.getDate(), new HashSet<String>());
            //menu.put("likes", likes.size());
            jsonArray.add(menu);
        }
        return jsonArray.toJSONString();
    }

/*    @RequestMapping(value = "/toggleLunchLikes", method = RequestMethod.POST)
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
    }*/

    @RequestMapping("/subscribeNotification")
    public @ResponseBody String subscribeNoti(@RequestParam String topic, @RequestParam String token){
        int rs = this.main.getFirebaseManager().addNotificationUser(topic, token);
        return "{rs: " + rs + "}";
    }

    @RequestMapping("/unsubscribeNotification")
    public @ResponseBody String unsubscribeNoti(@RequestParam String topic, @RequestParam String token){
        int rs = this.main.getFirebaseManager().removeNotificationUser(topic, token);
        return "{rs: " + rs + "}";
    }

    @RequestMapping("/checkSubscription")
    public @ResponseBody String checkSubscription(@RequestParam String topic, @RequestParam String token){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rs", this.main.getFirebaseManager().isUserInTopic(topic, token) ? 1 : 0);
        return jsonObject.toJSONString();
    }

    @RequestMapping("/getExamDates")
    public @ResponseBody String getExamDates(){
        return GSON.toJson(kishDao.getExamDates());
    }

    /**
     * <p>학사일정을 반환하는 API입니다.</p>
     */
/*    @RequestMapping("/getCalendar")
    public @ResponseBody String getCalendar(){
        return new JSONObject(this.calendarMap).toJSONString();
        //return this.calendar.getJson();
    }*/

    @RequestMapping("/getCalendarFromDate")
    public @ResponseBody String getCalendarFromDate(@RequestParam int year, @RequestParam int month){
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month - 1);

        List<Map<String, Object>> rs = kishDao.getPlansByYM(date);
        JSONArray rsArray = new JSONArray();
        rsArray.addAll(rs);

        return rsArray.toJSONString();
        //return new JSONObject(KishWebParser.getSchoolCalendar()).toJSONString();
        //return this.calendar.getJson();
    }

    private void makeCalendar(){
        int added = 0;
        int removed = 0;
        Calendar date = Calendar.getInstance();
        for(int m = 1; m < 13; m++){
            date.set(Calendar.MONTH, (m - 1));
            List<Map<String, Object>> registered = kishDao.getPlansByYM(date);
            HashSet<String> set = new HashSet<>();

            for (Map<String, Object> planMap : registered) {
                set.add(sdf.format(planMap.get("date")) + "=" + planMap.get("plan"));
            }

            LinkedHashMap<Calendar, ArrayList<String>> map = KishWebParser.getPlansFromServer(date);
            for (Calendar planDate : map.keySet()) {
                ArrayList<String> plans = map.get(planDate);

                for (String plan : plans) {
                    String key = sdf.format(planDate.getTime()) + "=" + plan;
                    if(!set.remove(key)){
                        kishDao.addPlanToCalendar(planDate, plan);
                        added ++;
                    }
                }
            }
            Calendar calendar = Calendar.getInstance();
            for (String key : set) {
                String[] tmp = key.split("=");

                try {
                    calendar.setTime(sdf.parse(tmp[0]));
                } catch (ParseException e) {
                    MainLogger.error(e);
                }

                kishDao.removePlanFromCalendar(calendar, tmp[1]);
                removed ++;
            }
        }

        if(added > 0 || removed > 0) {
            MainLogger.info(added + "개의 추가된 학사일정과 " + removed + "개의 제거된 학사일정이 있습니다.");
        }
    }
}
