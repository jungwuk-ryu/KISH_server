package org.kish2020.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kish2020.MainLogger;
import org.kish2020.entity.RequestResult;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

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
    public static JSONObject postRequestWithJsonResult(String fullUrl, ContentType contentType, String parameters){
        return postRequestWithJsonResult(fullUrl, contentType, parameters, null);
    }

    public static JSONObject postRequestWithJsonResult(String fullUrl, ContentType contentType, String parameters, String cookie){
        JSONParser parser = new JSONParser();
        JSONObject resultJson = null;
        try {
            String jsonResult = postRequest(fullUrl, contentType, parameters).getResponse();
            if(jsonResult == null) return null;
            resultJson = (JSONObject) parser.parse(jsonResult);
        } catch (ParseException e) {
            MainLogger.error("", e);
        }
        return resultJson;
    }

    public static RequestResult postRequest(String fullUrl, ContentType contentType, String parameters){
        return postRequest(fullUrl, contentType, parameters, null);
    }

    public static RequestResult postRequest(String fullUrl, ContentType contentType, String parameters, String cookie){
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
            if(cookie != null){
                con.setRequestProperty("Cookie", cookie);
            }else{
                con.setRequestProperty("Cookie", "");
            }

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

            String resultStr = resultJson.toString();
            return new RequestResult(resultStr, parseCookie(con.getHeaderFields()));
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
        return getRequestWithJsonResult(fullUrl, null);
    }

    public static JSONObject getRequestWithJsonResult(String fullUrl, String cookie){
        JSONParser parser = new JSONParser();
        JSONObject resultJson = null;
        try {
            resultJson = (JSONObject) parser.parse(getRequest(fullUrl, cookie).getResponse());
        } catch (ParseException e) {
            MainLogger.error("", e);
        }
        return resultJson;
    }

    public static RequestResult getRequest(String fullUrl) {
        return getRequest(fullUrl, null);
    }

    public static RequestResult getRequest(String fullUrl, String cookie) {
        URL url = null;
        JSONObject resultJsonObj = null;
        try {
            url = new URL(fullUrl);

            HttpURLConnection con = null;
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Accept", "application/json");
            if(cookie != null){
                con.setRequestProperty("Cookie", cookie);
            }else{
                con.setRequestProperty("Cookie", "");
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String il;
            StringBuilder resultJson = new StringBuilder();
            while((il = bufferedReader.readLine()) != null){
                resultJson.append(il);
            }
            bufferedReader.close();
            con.disconnect();

            String resultStr = resultJson.toString();
            return new RequestResult(resultStr, parseCookie(con.getHeaderFields()));
        } catch (IOException e) {
            MainLogger.error( fullUrl + "에 대한 get요청 중 오류 발생", e);
            return null;
        }
    }

    public static String getNewCookie (String fullUrl){
        //web.getHeaderField("Set-Cookie");
        //web.setRequestProperty("Cookie", cookie);
        URL url = null;
        try {
            url = new URL(fullUrl);

            HttpURLConnection con = null;
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Cookie", "");
            con.disconnect();
            Map<String, List<String>> map = con.getHeaderFields();
            return parseCookie(map);
        } catch (IOException e) {
            MainLogger.error( fullUrl + "에 대한 get요청 중 오류 발생", e);
            return null;
        }
    }

    public static String parseCookie(Map<String, List<String>> map){
        String cookie = "";
        for(String field : map.keySet()) {
            if("Set-Cookie".equalsIgnoreCase(field)) {
                List<String> list = map.get(field);
                int i = 0;
                for(String cookieV : list){
                    if(i > 0) cookie += "; ";
                    cookie += cookieV.split(";\\s*")[0];
                    i++;
                }
                break;
            }
        }
        return cookie;
    }
}
