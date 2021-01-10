package org.kish.entity;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class Exam {
    static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private int id;
    private long timestamp;
    private String label;
    private String date;

    public Exam(int id, long timestamp, String label){
        Date date = new Date(timestamp * 1000);
        setId(id);
        setTimestamp(timestamp);
        setLabel(label);
        setDate(SDF.format(date));
    }
}
