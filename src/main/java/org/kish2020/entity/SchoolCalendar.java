package org.kish2020.entity;

import org.json.simple.JSONObject;
import org.kish2020.MainLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 *  학사일정을 관리하는 클래스입니다.
 *  add, remove와 같은 메소드를 사용하고 commit을 꼭 해주셔야 getJson() 하실 때 변경된 부분이 적용됩니다.
 */
public class SchoolCalendar extends LinkedHashMap<String, HashSet<String>> {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String json;

    public SchoolCalendar() {
        this.commit();
    }

    /**
     * 학사 일정을 추가합니다.
     *
     * @param date YYYY-mm-dd와 같은 형식
     * @param event 학사일정 이름
     */
    public SchoolCalendar add(String date, String event){
        return this.add(date, event,1);
    }

    /**
     * 학사 일정을 추가합니다.
     * 학사일정이 하루만 진행되는게 아닌 장기적으로 진행되는 경우 사용합니다.
     * 방학, 스포츠클럽, 지필고사 등이 장기적으로 진행되는 학사일정입니다.
     *
     * @param sourceDate 학사일정 시작일이며, YYYY-mm-dd와 같은 형식
     * @param event 학사일정 이름
     * @param duration 학사일정 지속일 (04-26 ~ 04-28 일동안 지속될 경우 3)
     */
    public SchoolCalendar add(String sourceDate, String event, int duration){
        if(duration < 1){
            MainLogger.error("", new IllegalArgumentException("duration은 1 이상의 수만 허용됩니다."));
        }

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(sourceDate));
        } catch (ParseException e) {
            MainLogger.error("전달된 date : " + sourceDate, e);
            return null;
        }
        for(int i = 0; i < duration; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String newDate = simpleDateFormat.format(calendar.getTime());
            HashSet<String> schedules = this.getOrDefault(newDate, new HashSet<>());
            schedules.add(event);
            this.put(newDate, schedules);
        }
        return this;
    }

    /**
     * 변경된 정보들을 적용합니다.
     */
    public void commit(){
        this.json = new JSONObject(this).toString();
    }

    public String getJson(){
        return this.json;
    }
}
