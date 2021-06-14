package org.kish;

import org.kish.web.KishMagazineApiController;
import org.kish.web.MainApiController;
import org.kish.web.PostApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    @Autowired
    public MainApiController mainApiController;
    @Autowired
    public PostApiController postApiController;
    @Autowired
    public KishMagazineApiController kishMagazineApiController;

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
}
