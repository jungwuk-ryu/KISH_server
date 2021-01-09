package org.kish.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Exam {
    private int id;
    private long timestamp;
    private String label;

    public Exam(int id, long timestamp, String label){
        setId(id);
        setTimestamp(timestamp);
        setLabel(label);
    }
}
