package org.kish2020.entity;

import com.google.gson.Gson;
import org.kish2020.DataBase.DataBase;
import org.kish2020.MainLogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;

public class SchoolCalendar extends DataBase<HashSet<String>> {
    private static Gson gson = new Gson();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String json;

    public SchoolCalendar() {
        super("db/schoolCalendarDB.json", true, true);
        this.commit();
    }

    public SchoolCalendar add(String date, String event){
        return this.add(date, event,1);
    }

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

    public void commit(){
        this.json = gson.toJson(this);
    }

    public String getJson(){
        return this.json;
    }
}
