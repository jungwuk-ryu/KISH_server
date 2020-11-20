package org.kish;

import org.kish.dataBase.ExpandedDataBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.web.context.support.RequestHandledEvent;

import java.util.Date;
import java.util.HashMap;

@SpringBootApplication
public class KishServer {
    public static ExpandedDataBase mainDataBase = null;
    public static ExpandedDataBase mainSettings = null;
    public static FirebaseManager firebaseManager = null;

    public static void main(String[] args) {
        mainDataBase = new ExpandedDataBase("db/kish_main_db.json");
        mainSettings = new ExpandedDataBase("db/kish2020.json");
        mainSettings.setSaveWithPrettyGson(true);
        firebaseManager = new FirebaseManager();
        SpringApplication.run(KishServer.class, args);

        firebaseManager.sendFCMToAdmin("Server started", "서버가 시작되었습니다.\n" + new Date().toString(), new HashMap<>());
    }

    /**
     * @return 기본 데이터베이스
     */
    public ExpandedDataBase getMainDataBase(){
        return mainDataBase;
    }

    public ExpandedDataBase getMainSettings(){
        return mainSettings;
    }

    public FirebaseManager getFirebaseManager(){
        return firebaseManager;
    }

    @EventListener
    public void requestEventListener(RequestHandledEvent e){
        MainLogger.info(e.getShortDescription() + "(" + e.getProcessingTimeMillis() + "ms)");
    }
}
