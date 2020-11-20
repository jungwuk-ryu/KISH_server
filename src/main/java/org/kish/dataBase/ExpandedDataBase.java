package org.kish.dataBase;

import org.json.simple.JSONObject;

import java.util.LinkedHashMap;
import java.util.Set;

@SuppressWarnings("unchecked")
public class ExpandedDataBase extends DataBase<Object> {
    public ExpandedDataBase(String fileName) {
        super(fileName);
    }

    public ExpandedDataBase(String fileName, boolean isLoggingEnabled, boolean doSaveOnShutdown) {
        super(fileName, isLoggingEnabled, doSaveOnShutdown);
    }

    @Override
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

    public long getLong(String k){
        return (long) this.get(k);
    }
}
