package org.kish;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kish.MainLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Config extends LinkedHashMap<String, Object>{
    public static final Gson GSON = new Gson();
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    public final String fileName;

    private final boolean doSave = false;
    private final boolean isLoggingEnabled = true;
    private final boolean saveWithPrettyGson = true;
    private boolean isLoaded = false;

    public Config(String fileName) {
        this.fileName = fileName;
        this.reload();
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(() -> {
            save();
        }));
    }

    /*public Config(String fileName, boolean isLoggingEnabled, boolean doSaveOnShutdown) {
        this.fileName = fileName;
        this.setIsLoggingEnabled(isLoggingEnabled);
        this.reload();
        Runtime rt = Runtime.getRuntime();
        if(doSaveOnShutdown) {
            rt.addShutdownHook(new Thread(() -> {
                save();
            }));
        }
    }*/

    public void reload(){
        File jsonFile = new File(fileName);
        if(this.isLoggingEnabled) MainLogger.warn("Config 불러오는 중 : " + jsonFile.getAbsolutePath());

        try {
            String json;
            if (!jsonFile.exists()) {
                json = "{}";
            } else {
                json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
            }
            JSONObject jsonObject;
            jsonObject = (JSONObject) (new JSONParser().parse(json));
            this.putAll(jsonToMap(jsonObject));
        } catch (ParseException | IOException e) {
            MainLogger.error("Config 준비 중 오류가 발생하였습니다.", e);
            return;
        }

        for (ConfigItem item : ConfigItem.values()) {
            if(!this.containsKey(item.key)) this.put(item.key, item.defaultValue);
        }

        save(true);
        this.isLoaded = true;
    }

    public LinkedHashMap<String, Object> jsonToMap(JSONObject jsonObject){
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for(String key : (Set<String>) jsonObject.keySet()){
            Object data = jsonObject.get(key);
            if(data instanceof Long) this.put(key, ((Long) data).intValue());
             map.put(key, data);
        }
        return map;
    }

    public void deleteFile(){
        File file = new File(fileName);
        file.delete();
        if(isLoggingEnabled) MainLogger.warn("제거됨 : " + fileName);
    }

    public void save(){
        this.save(false);
    }

    public void save(boolean forceSave){
        if(!forceSave && !this.doSave) return;
        if(!this.isLoaded){
            MainLogger.error("DB가 정상적으로 로드되지 않아 데이터 손실 방지를 위해 저장되지 않습니다.");
            return;
        }
        if(isLoggingEnabled) MainLogger.warn("저장하는 중 : " + fileName);

        /*JSONObject jsonObject = new JSONObject();
        for(String key : this.keySet()){
            jsonObject.put(key, this.get(key));
        }*/
        try {
            if(saveWithPrettyGson){
                FileUtils.write(new File(fileName), PRETTY_GSON.toJson(this), StandardCharsets.UTF_8);
            }else {
                FileUtils.write(new File(fileName), GSON.toJson(this), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            MainLogger.error("DB 저장중 오류가 발생하였습니다.", e);
        }
    }

    public long getLong(String k){
        return (long) this.get(k);
    }

    public boolean isLoaded() { return isLoaded; }

    /*private void runListener(){
        if(this.dataChangeListener == null) return;
        this.dataChangeListener.run();
    }*/

    public enum ConfigItem {
        MYSQL_HOST("mysql_host", "localhost"),
        MYSQL_PORT("mysql_port", 3306),
        MYSQL_USER("mysql_user", "userName"),
        MYSQL_PW("mysql_pw", "password"),
        MYSQL_DB("mysql_db", "db name"),

        FB_DB_URL("firebase_DatabaseUrl", "Firebase DB주소. ex) https://DB이름.firebaseio.com"),
        FB_ACCOUNT_KEY("firebase_path_serviceAccountKey", "serviceAccountKey.json 파일 경로");

        public String key;
        public Object defaultValue;

        ConfigItem(String key, Object defaultValue){
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return key;
        }
    }
}
