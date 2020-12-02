package org.kish.dataBase;

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
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

@SuppressWarnings("unchecked")
public class Config extends LinkedHashMap<String, Object>{
    public static final Gson GSON = new Gson();
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
    public boolean doSave = true;
    public boolean isLoggingEnabled = true;
    public boolean saveWithPrettyGson = false;
    public final String fileName;

    private boolean isLoaded = false;

    public Config(String fileName) {
        this.fileName = fileName;
        this.reload();
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(() -> {
            save();
        }));
    }

    public Config(String fileName, boolean isLoggingEnabled, boolean doSaveOnShutdown) {
        this.fileName = fileName;
        this.setIsLoggingEnabled(isLoggingEnabled);
        this.reload();
        Runtime rt = Runtime.getRuntime();
        if(doSaveOnShutdown) {
            rt.addShutdownHook(new Thread(() -> {
                save();
            }));
        }
    }

    public void reload(){
        File jsonFile = new File(fileName);
        if(this.isLoggingEnabled) MainLogger.warn("DB 불러오는 중 : " + jsonFile.getAbsolutePath());
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
            MainLogger.error("DB 준비 중 오류가 발생하였습니다.", e);
            return;
        }
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

    /**
     * key에 해당하는 값을 n만큼 증가시킵니다.
     */
    public int increase(String k, int n){
        Object num = this.getOrDefault(k, 0);
        if(num instanceof Long){
            Long longNum = (Long) num;
            int increasedNum = (int) longNum.intValue() + n;
            this.put(k, increasedNum);
            return increasedNum;
        } else {
            int increasedNum = ((int) num) + n;
            this.put(k, increasedNum);
            return increasedNum;
        }
    }

    /**
     * key에 해당하는 값을 n만큼 감소시킵니다.
     */
    public int decrease(String k, int n){
        Object num = this.getOrDefault(k, 0);
        if(num instanceof Long){
            Long longNum = (Long) num;
            int decreasedNum = (int) longNum.intValue() - n;
            this.put(k, decreasedNum);
            return decreasedNum;
        } else {
            int decreasedNum = ((int) num) - n;
            this.put(k, decreasedNum);
            return decreasedNum;
        }
    }

    public void deleteFile(){
        File file = new File(fileName);
        file.delete();
        if(isLoggingEnabled) MainLogger.warn("제거됨 : " + fileName);
    }

    public void save(){
        if(!this.doSave) return;
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

    public boolean getDoSave() {
        return doSave;
    }

    public void setDoSave(boolean doSave) {
        this.doSave = doSave;
    }

    public boolean isLoggingEnabled(){
        return isLoggingEnabled;
    }

    public void setIsLoggingEnabled(boolean v){
        this.isLoggingEnabled = v;
    }


    public void setSaveWithPrettyGson(boolean b){
        this.saveWithPrettyGson = b;
    }

    /*private void runListener(){
        if(this.dataChangeListener == null) return;
        this.dataChangeListener.run();
    }*/
}
