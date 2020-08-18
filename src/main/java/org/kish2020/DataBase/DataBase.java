package org.kish2020.DataBase;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kish2020.MainLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class DataBase<V> extends LinkedHashMap<String, V>{
    public boolean doSave = true;
    public boolean isLoggingEnabled = true;
    public final String fileName;

    private boolean isLoaded = false;
    private Runnable dataChangeListener;

    public DataBase(String fileName) {
        this.fileName = fileName;
        this.reload();
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(() -> {
            save();
        }));
    }

    public DataBase(String fileName, boolean doSaveOnShutdown) {
        this.fileName = fileName;
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

    public void remove(){
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
        Gson gson = new Gson();
        try {
            FileUtils.write(new File(fileName), gson.toJson(this) , StandardCharsets.UTF_8);
        } catch (IOException e) {
            MainLogger.error("DB 저장중 오류가 발생하였습니다.", e);
        }
    }

    @Override
    public V put(String key, V value) {
        V returnValue = super.put(key, value);
        this.runListener();
        return returnValue;
    }

    public boolean put(String key, V value, boolean overwrite){
        if(this.containsKey(key)){
            if(!overwrite) return false;
        }
        this.put(key, value);
        return true;
    }

    @Override
    public V putIfAbsent(String key, V value) {
        V returnValue = super.putIfAbsent(key, value);
        this.runListener();
        return returnValue;
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        super.putAll(m);
        this.runListener();
    }

    @Override
    public V remove(Object key) {
        V returnValue = super.remove(key);
        this.runListener();
        return returnValue;
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean returnValue = super.remove(key, value);
        this.runListener();
        return returnValue;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        boolean returnValue =  super.removeEldestEntry(eldest);
        this.runListener();
        return returnValue;
    }

    @Override
    public V replace(String key, V value) {
        V returnValue = super.replace(key, value);
        this.runListener();
        return returnValue;
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super V, ? extends V> function) {
        super.replaceAll(function);
        this.runListener();
    }

    @Override
    public void clear() {
        super.clear();
        this.runListener();
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

    public void setDataChangeListener(Runnable r){
        this.dataChangeListener = r;
    }

    private void runListener(){
        if(this.dataChangeListener == null) return;
        this.dataChangeListener.run();
    }
}
