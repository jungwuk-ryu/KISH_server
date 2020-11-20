package org.kish.entity;

public class RequestResult {
    public String response, cookie;
    public RequestResult(String response, String cookie){
        this.response = response;
        this.cookie = cookie;
    }

    public String getResponse() {
        return response;
    }

    public String getCookie() {
        return cookie;
    }
}
