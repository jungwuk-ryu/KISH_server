package org.kish2020;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Set;

public class DataBase extends LinkedHashMap<String, Object>{
    public boolean doSave = true;
    public final String jsonName;

    public DataBase(String jsonName) {
        this.jsonName = jsonName;
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
            for(String key : (Set<String>) jsonObject.keySet()){
                Object data = jsonObject.get(key);
                if(data instanceof Long) this.put(key, ((Long) data).intValue());  //사실 그냥 Long으로 냅두면 어떨까 싶습니다.
                else this.put(key, data);
            }
        } catch (ParseException | IOException e) {
            MainLogger.error("DB 준비 중 오류가 발생하였습니다.", e);
        }
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(() -> {
            save();
        }));
    }

    public void save(){
        if(!this.doSave) return;
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

    /**
     * key에 해당하는 값을 n만큼 증가시킵니다.
     */
    public int increase(String k, int n){
        int increasedNum = (int) this.getOrDefault(k, 0) + n;
        this.put(k, increasedNum);
        return increasedNum;
    }

    /**
     * key에 해당하는 값을 n만큼 감소시킵니다.
     */
    public int decrease(String k, int n){
        int decreasedNum = (int) this.getOrDefault(k, 0) - n;
        this.put(k, decreasedNum);
        return decreasedNum;
    }

    public boolean getDoSave() {
        return doSave;
    }

    public void setDoSave(boolean doSave) {
        this.doSave = doSave;
    }
}
