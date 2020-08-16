package org.kish2020.utils;

import io.github.bangjunyoung.KoreanChar;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Utils {
    public static String makeChoseongSentence(String sentence){
        StringBuilder choseongSb = new StringBuilder();
        for(char c : sentence.toCharArray()){
            if(KoreanChar.isSyllable(c)) c = KoreanChar.getCompatChoseong(c);
            choseongSb.append(c);
        }
        return choseongSb.toString();
    }

    public static ArrayList<String> getContentTokens(String postContent){
        if(postContent == null || postContent.isEmpty()) return null;
        ArrayList<String> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(postContent);
        while (st.hasMoreTokens()){
            list.add(st.nextToken());
        }
        return list;
    }

    public static LinkedHashMap<String, Integer> getContentTokenMap(String postContent){
        if(postContent == null || postContent.isEmpty()) return null;
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        StringTokenizer st = new StringTokenizer(postContent);
        while (st.hasMoreTokens()){
            String token = st.nextToken();
            map.put(token, map.getOrDefault(token, 0) + 1);
        }
        return map;
    }

    public static void addPostToKeyword(String postID, LinkedHashMap<String, HashMap<String, Integer>> targetKeywordMap, LinkedHashMap<String, Integer> tokenMap){
        for(String key : tokenMap.keySet()){
            int count = tokenMap.get(key);
            HashMap<String, Integer> keywordData = targetKeywordMap.getOrDefault(key, new HashMap<>());
            keywordData.put(postID, count);
            String choseongKeywordData = makeChoseongSentence(key);
            HashMap<String, Integer> choseongMap = targetKeywordMap.getOrDefault(choseongKeywordData, new HashMap<>());
            choseongMap.put(postID, count);

            targetKeywordMap.put(key, keywordData);
            targetKeywordMap.put(choseongKeywordData, choseongMap);
        }
    }

    public static ArrayList<HashMap<String, Integer>> search(LinkedHashMap<String, HashMap<String, Integer>> sourceMap, String keyword){
        String choseongKeyword = Utils.makeChoseongSentence(keyword);
        String noSpaceKeyword = StringUtils.replace(keyword, " ", "");
        ArrayList<HashMap<String, Integer>> arrayList = new ArrayList<>();
        for(String key : sourceMap.keySet()){
            if(keyword.contains(key) || choseongKeyword.equals(key) || noSpaceKeyword.contains(key) || key.contains(noSpaceKeyword)){
                arrayList.add(sourceMap.get(key));
            }
        }
        return arrayList;
    }
}
