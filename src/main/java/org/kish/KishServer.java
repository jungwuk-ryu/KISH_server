package org.kish;

import org.kish.dataBase.DBCPInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.web.context.support.RequestHandledEvent;

import javax.servlet.ServletException;
import java.util.Date;
import java.util.HashMap;

@SpringBootApplication
public class KishServer {
    public static Config CONFIG = null;
    public static FirebaseManager firebaseManager = null;

    public static void main(String[] args) {
        CONFIG = new Config("config.json");
        firebaseManager = new FirebaseManager();

        DBCPInitializer dbcp = new DBCPInitializer("host", "userName", "pw", "db", 0);
        try {
            dbcp.init();
        } catch (ServletException e) {
            MainLogger.error("DBCP 초기화 실패", e);
            Runtime.getRuntime().exit(0);
        }

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
