package org.kish;

import org.kish.dataBase.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.web.context.support.RequestHandledEvent;

import java.util.Date;
import java.util.HashMap;

@SpringBootApplication
public class KishServer {
    public static Config CONFIG = null;
    public static FirebaseManager firebaseManager = null;

    public static void main(String[] args) {
        // TODO : DB ID, DB PW, DB HOST, DB PORT, FB messaging_tokens, FB db url, FB service account
        CONFIG = new Config("db/config.json");
        CONFIG.setSaveWithPrettyGson(true);
        firebaseManager = new FirebaseManager();
        SpringApplication.run(KishServer.class, args);

        firebaseManager.sendFCMToAdmin("Server started", "서버가 시작되었습니다.\n" + new Date().toString(), new HashMap<>());
    }

    public Config getMainSettings(){
        return CONFIG;
    }

    public FirebaseManager getFirebaseManager(){
        return firebaseManager;
    }

    @EventListener
    public void requestEventListener(RequestHandledEvent e){
        MainLogger.info(e.getShortDescription() + "(" + e.getProcessingTimeMillis() + "ms)");
    }
}
