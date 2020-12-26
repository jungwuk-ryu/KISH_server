package org.kish;

import org.kish.database.table.TableManager;
import org.kish.web.PostApiController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.support.RequestHandledEvent;

import java.util.Arrays;
import java.util.Properties;


@SpringBootApplication(scanBasePackages = {"org.kish", "org.kish.web", "org.kish.database"})
public class KishServer {
    public static Config CONFIG = null;
    public static ConfigurableApplicationContext CAC = null;

    public static TableManager tableManager = null;
    public static FirebaseManager firebaseManager = null;

    public static JdbcTemplate jdbcTemplate = null;

    public static void main(String[] args) {
        CONFIG = new Config("kish_config.json");

        if(CONFIG.get("mysql_db").equals("db name")){
            System.out.println("mysql_db is not configured");
            System.exit(0);
        }

        SpringApplication application = new SpringApplication(KishServer.class);
        Properties props = new Properties();

        String host = "jdbc:mysql://%host%:%port%/%db%?useUnicode=true&characterEncoding=utf8&useSSL=false";
        host = host.replace("%host%", (String) CONFIG.get("mysql_host"));
        host = host.replace("%port%", Long.toString(CONFIG.getLong("mysql_port")));
        host = host.replace("%db%", (String) CONFIG.get("mysql_db"));

        props.put("spring.datasource.url", host);
        props.put("spring.datasource.username", CONFIG.get("mysql_user"));
        props.put("spring.datasource.password", CONFIG.get("mysql_pw"));
        application.setDefaultProperties(props);
        CAC = application.run(args);

        tableManager.checkAllTable();
        firebaseManager = new FirebaseManager();
        MainLogger.info("실행 준비됨");

        PostApiController pac = CAC.getBean(PostApiController.class);
        if(KishServer.CONFIG.get(Config.ConfigOption.GET_ALL_POSTS_ON_BOOT.key).equals("on")){
            pac.checkingNewPostLock = true;
            pac.parseAllPosts();
        }
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
}
