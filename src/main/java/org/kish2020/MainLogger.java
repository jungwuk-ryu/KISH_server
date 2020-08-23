package org.kish2020;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class MainLogger {
    public static final Logger LOGGER = LogManager.getLogger(MainLogger.class);

    public static void debug(Object content){
        LOGGER.debug(content);
    }

    public static void info(Object content){
        LOGGER.info(content);
    }

    public static void warn(Object content){
        LOGGER.warn(content);
    }

    public static void error(Object content){
        if(Kish2020Server.firebaseManager != null){
            Kish2020Server.firebaseManager.sendFCMToAdmin("오류 발생", content.toString(), new HashMap<>());
        }
        LOGGER.error(content);
    }

    public static void error(Object content, Throwable t){
        if(Kish2020Server.firebaseManager != null){
            Kish2020Server.firebaseManager.sendFCMToAdmin("오류 발생", t.toString(), new HashMap<>());
        }
        LOGGER.error(content, t);
    }

}
