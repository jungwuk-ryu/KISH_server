package org.kish.entity;

import com.google.firebase.messaging.AndroidConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class Noti {
    private String topic, title, content;
    private LinkedHashMap<String, String> data;

    private String color = "#344aba";
    private AndroidConfig.Priority priority = AndroidConfig.Priority.NORMAL;

}
