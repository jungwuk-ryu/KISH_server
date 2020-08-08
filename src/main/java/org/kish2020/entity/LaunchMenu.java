package org.kish2020.entity;

public class LaunchMenu {
    public String menu, detail, imageUrl;

    public LaunchMenu(String menu, String detail, String imageUrl){
        this.menu = menu;
        this.detail = detail;
        this.imageUrl = imageUrl;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
