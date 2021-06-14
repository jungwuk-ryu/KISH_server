package org.kish;

import org.kish.web.KishMagazineApiController;
import org.kish.web.MainApiController;
import org.kish.web.PostApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Scheduler {
    @Autowired
    public MainApiController mainApiController;
    @Autowired
    public PostApiController postApiController;
    @Autowired
    public KishMagazineApiController kishMagazineApiController;
    @Autowired
    public Interceptor interceptor;

    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 1000 * 60)
    public void updateLunchMenu() {
        mainApiController.updateLunchMenu();
    }

    @Scheduled(fixedDelay = 1000 * 60 * 50, initialDelay = 1000 * 60)
    public void updateCalendar() {
        mainApiController.makeCalendar();
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 60 * 5)
    public void updateKishPosts() {
        postApiController.checkNewPost();
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 60 * 30)
    public void updateKishMagazine() {
        kishMagazineApiController.updateArticles();
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void clientMonitor() {
        int in10min = 0;
        int in1Hour = 0;

        ArrayList<String> list = new ArrayList<>();
        long current = System.currentTimeMillis();
        for (String ip : interceptor.clients.keySet()) {
            long time = interceptor.clients.get(ip);
            long diff = current - time;

            if (diff > 1000 * 60 * 60) {
                list.add(ip);
            } else {
                if (diff <= 1000 * 60 * 10) {
                    in10min++;
                }
                in1Hour++;
            }
        }

        MainLogger.info("Clients: " + in1Hour + " (1h), " + in10min + " (10m)");
    }
}
