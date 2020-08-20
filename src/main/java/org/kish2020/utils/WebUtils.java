package org.kish2020.utils;

import com.google.gson.Gson;
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

    /**
     * Post요청을 보냅니다.
     *
     * @param fullUrl post요청을 할 url
     * @param contentType RequestProperty의 Content-Type
     * @param parameters post요청에 사용할 파라미터
     * @return 요청에 성공할경우 JSONObject, 실패할경우 null
     */
    // TODO : 테스트

    public static JSONObject postRequestWithJsonResult(String fullUrl, ContentType contentType, String parameters){
        JSONParser parser = new JSONParser();
        JSONObject resultJson = null;
        try {
            resultJson = (JSONObject) parser.parse(postRequest(fullUrl, contentType, parameters));
        } catch (ParseException e) {
            MainLogger.error("", e);
        }
        return resultJson;
    }

    public static String postRequest(String fullUrl, ContentType contentType, String parameters){
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
            return resultJsonStr;
        } catch (IOException e) {
            MainLogger.error(fullUrl + "에 대한 post 요청 중 오류가 발생하였습니다.", e);
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

    /**
     * <p>Get 요청을 보냅니다</p>
     *
     * @param fullUrl 파라미터를 포함한 url
     * @return 요청에 성공할경우 JSONObject, 실패할경우 null
     */

    public static JSONObject getRequestWithJsonResult(String fullUrl){
        JSONParser parser = new JSONParser();
        JSONObject resultJson = null;
        try {
            resultJson = (JSONObject) parser.parse(getRequest(fullUrl));
        } catch (ParseException e) {
            MainLogger.error("", e);
        }
        return resultJson;
    }

    public static String getRequest(String fullUrl) {
        URL url = null;
        JSONObject resultJsonObj = null;
        try {
            url = new URL(fullUrl);

            HttpURLConnection con = null;
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Accept", "application/json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String il;
            StringBuilder resultJson = new StringBuilder();
            while((il = bufferedReader.readLine()) != null){
                resultJson.append(il);
            }
            bufferedReader.close();
            con.disconnect();

            String resultJsonStr = resultJson.toString();
            return resultJsonStr;
        } catch (IOException e) {
            MainLogger.error( fullUrl + "에 대한 get요청 중 오류 발생", e);
            return null;
        }
    }
}
