package org.kish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.web.context.support.RequestHandledEvent;

import java.util.Properties;

@SpringBootApplication
public class KishServer {
    public static FirebaseManager firebaseManager = null;
    public static Config CONFIG = null;

    public static void main(String[] args) {
        CONFIG = new Config("kish_config.json");
        firebaseManager = new FirebaseManager();

        SpringApplication.run(KishServer.class, args);
    }

    public Config getConfig(){
        return CONFIG;
    }

    public FirebaseManager getFirebaseManager(){
        return firebaseManager;
    }

    @EventListener
    public void requestEventListener(RequestHandledEvent e){
        MainLogger.info(e.getShortDescription() + "(" + e.getProcessingTimeMillis() + "ms)");
    }

    // TODO : TEST
    @EventListener
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Properties props = new Properties();

        if(CONFIG.get("mysql_db").equals("db name")) {
            System.out.println("A option mysql_pw is invalid");
            System.exit(0);
        }

        String host = "jdbc:mysql://%hsot%:%port%/%db%?useUnicode=true&characterEncoding=utf8&useSSL=false";
        host = host.replace("%host%", (String) CONFIG.get("mysql_host"));
        host = host.replace("%port%", (String) CONFIG.get("mysql.port"));
        host = host.replace("%db%", (String) CONFIG.get("mysql_db"));

        props.put("spring.datasource.url", host);
        props.put("spring.datasource.username", CONFIG.get("mysql_user"));
        props.put("spring.datasource.password", "mysql_pw");

        environment.getPropertySources().addFirst(new PropertiesPropertySource("myProps", props));
    }
}
