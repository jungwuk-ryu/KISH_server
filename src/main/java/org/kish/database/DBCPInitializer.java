package org.kish.database;

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.kish.MainLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.sql.DriverManager;

public class DBCPInitializer extends HttpServlet {
    private String host, user, pw, db;
    private int port;

    public DBCPInitializer() {};

    public DBCPInitializer(String host, String user, String pw, String db){
        this();
        this.host = host;
        this.user = user;
        this.pw = pw;
        this.db = db;
        this.port = 3306;
    }

    public DBCPInitializer(String host, String user, String pw, String db, int port){
        this(host, user, pw, db);
        this.port = port;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        initPool();
    }

    private void initPool(){
        // Thanks for https://hsp1116.tistory.com/8

        if(host == null || user == null || pw == null || db == null){
            MainLogger.error(
                    new IllegalArgumentException("DB host 또는 username, pw, db가 설정되어있지 않습니다."));
            return;
        }

        try{
          Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            MainLogger.error("", e);
        }

        try{
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?characterEncoding=utf8&useUnicode=true";

            ConnectionFactory conFactory = new DriverManagerConnectionFactory(url, user, pw);
            PoolableConnectionFactory poolableConFactory = new PoolableConnectionFactory(conFactory, null);
            poolableConFactory.setValidationQuery("select 1");

            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setTimeBetweenEvictionRunsMillis(1000L * 60L); // 커넥션 검사 주기
            poolConfig.setTestWhileIdle(true); // 유효 유무 검사 여부
            poolConfig.setMinIdle(7);
            poolConfig.setMaxTotal(30);

            GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConFactory, poolConfig);
            poolableConFactory.setPool(connectionPool);

            Class.forName("org.apache.commons.dbcp2.PoolingDriver");

            PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp");
            driver.registerPool("genericPool", connectionPool);
        } catch (Exception e) {
            MainLogger.error("", e);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}
