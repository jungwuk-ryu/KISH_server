package org.kish2020;

import org.kish2020.DataBase.ExpandedDataBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Kish2020Server {
    public static ExpandedDataBase mainDataBase = null;
    public static ExpandedDataBase mainSettings = null;
    public static FirebaseManager firebaseManager = null;
    public int d = 1;

    public static void main(String[] args) {
        mainDataBase = new ExpandedDataBase("kish_main_db.json");
        mainSettings = new ExpandedDataBase("kish2020.json");
        firebaseManager = new FirebaseManager();
        SpringApplication.run(Kish2020Server.class, args);
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
}
