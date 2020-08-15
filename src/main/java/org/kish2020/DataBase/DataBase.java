package org.kish2020.DataBase;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kish2020.MainLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Set;

public class DataBase<V> extends LinkedHashMap<String, V>{
    public boolean doSave = true;
    public final String jsonName;

    private boolean isLoaded = false;

    public DataBase(String jsonName) {
        this.jsonName = jsonName;
        this.reload();
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(() -> {
            save();
        }));
    }

    public void reload(){
        File jsonFile = new File(jsonName);
        MainLogger.warn("DB 불러오는 중 : " + jsonFile.getAbsolutePath());
        try {
            String json;
            if (!jsonFile.exists()) {
                json = "{}";
            } else {
                json = FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
            }
            JSONObject jsonObject = null;
            jsonObject = (JSONObject) (new JSONParser().parse(json));
            this.putAll(jsonToMap(jsonObject));
        } catch (ParseException | IOException e) {
            MainLogger.error("DB 준비 중 오류가 발생하였습니다.", e);
            return;
        }
        this.isLoaded = true;
    }

    public LinkedHashMap<String, V> jsonToMap(JSONObject jsonObject){
        LinkedHashMap<String, V> map = new LinkedHashMap<>();
        for(String key : (Set<String>) jsonObject.keySet()){
            Object data = jsonObject.get(key);
             map.put(key, (V) data);
        }
        return map;
    }

    public void save(){
        if(!this.doSave) return;
        if(!this.isLoaded){
            MainLogger.error("DB가 정상적으로 로드되지 않아 데이터 손실 방지를 위해 저장되지 않습니다.");
            return;
        }
        MainLogger.warn("저장하는 중 : " + jsonName);

        JSONObject jsonObject = new JSONObject();
        for(String key : this.keySet()){
            jsonObject.put(key, this.get(key));
        }
        try {
            FileUtils.write(new File(jsonName), jsonObject.toJSONString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            MainLogger.error("DB 저장중 오류가 발생하였습니다.", e);
        }
    }

    public boolean put(String key, V value, boolean overwrite){
        if(this.containsKey(key)){
            if(!overwrite) return false;
        }
        this.put(key, value);
        return true;
    }

    public boolean isLoaded() { return isLoaded; }

    public boolean getDoSave() {
        return doSave;
    }

    public void setDoSave(boolean doSave) {
        this.doSave = doSave;
    }
}
