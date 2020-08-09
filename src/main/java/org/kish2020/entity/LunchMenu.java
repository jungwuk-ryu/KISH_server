package org.kish2020.entity;

import org.json.simple.JSONObject;

public class LunchMenu extends JSONObject {
    public LunchMenu(String menu, String detail, String imageUrl){
        this.setMenu(menu);
        this.setDetail(detail);
        this.setImageUrl(imageUrl);
    }

    public String getMenu() {
        return (String) this.get("menu");
    }

    public void setMenu(String menu) {
        this.put("menu", menu);
    }

    public String getDetail() {
        return (String) this.get("detail");
    }

    public void setDetail(String detail) {
        this.put("detail", detail);
    }

    public String getImageUrl() {
        return (String) this.get("imageUrl");
    }

    public void setImageUrl(String imageUrl) {
        this.put("imageUrl", imageUrl);
    }
}
