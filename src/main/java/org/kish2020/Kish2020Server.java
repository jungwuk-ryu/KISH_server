package org.kish2020;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Kish2020Server {
    public static DataBase mainDataBase = null;
    public int d = 1;

    public static void main(String[] args) {
        mainDataBase = new DataBase("kish_main_db.json");
        SpringApplication.run(Kish2020Server.class, args);

        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(){
            @Override
            public void run(){
                MainLogger.warn("저장하는 중 입니다.");
                mainDataBase.save();
            }
        });
    }

    public DataBase getMainDataBase(){
        return mainDataBase;
    }
}
