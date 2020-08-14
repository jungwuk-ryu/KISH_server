package org.kish2020.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kish2020.MainLogger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebUtils {
    private final static String USER_AGENT = "Mozilla/5.0";

    // TODO : 테스트
    public static JSONObject postRequest(String fullUrl, ContentType contentType, String parameters){
        try {
            URL url = new URL(fullUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Content-Type", contentType.toString());
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String il;
            StringBuilder resultJson = new StringBuilder();
            while ((il = bufferedReader.readLine()) != null) {
                resultJson.append(il);
            }
            bufferedReader.close();
            con.disconnect();

            String resultJsonStr = resultJson.toString();
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(resultJsonStr);
        } catch (IOException | ParseException e) {
            MainLogger.error("post 요청 중 오류가 발생하였습니다.", e);
            return null;
        }
    }

    public enum ContentType {
        JSON("application/json"),
        FORM("application/x-www-form-urlencoded; charset=UTF-8");

        String type;
        ContentType(String type){
            this.type = type;
        }

        @Override
        public String toString(){
            return this.type;
        }
    }
}
