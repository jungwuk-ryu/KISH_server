package org.kish.entity;

import com.google.firebase.messaging.AndroidConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Noti {
    private String topic, title, content;
    private HashMap<String, String> data = new HashMap<>();

    private String color = "#344aba";
    private AndroidConfig.Priority priority = AndroidConfig.Priority.NORMAL;

    public Noti(String topic, String title, String content){
        this.setTopic(topic);
        this.setTitle(title);
        this.setContent(content);
    }
}
