package org.kish.entity;

import org.json.simple.JSONObject;

/**
 *  급식 정보를 담고있는 클래스입니다.
 *  급식 정보는 http://www.hanoischool.net/?menu_no=47 에서 볼 수 있습니다.
 */
@SuppressWarnings("unchecked")
public class LunchMenu extends JSONObject {
    public LunchMenu(String date, String menu, String detail, String imageUrl){
        this.setMenu(menu);
        this.setDetail(detail);
        this.setImageUrl(imageUrl);
    }

    /**
     * @return 급식 메뉴의 날짜
     */
    public String getDate(){
        return (String) this.get("date");
    }

    /**
     * 급식 메뉴의 날짜를 설정합니다.
     *
     * @param date
     */
    public void setDate(String date){
        this.put("date", date);
    }

    /**
     * @return 급식 메뉴
     */
    public String getMenu() {
        return (String) this.get("menu");
    }

    /**
     *  급식 메뉴를 설정합니다.
     *
     * @param menu
     */
    public void setMenu(String menu) {
        this.put("menu", menu);
    }

    /**
     *  @return 급식메뉴의 염도, 칼로리 정보
     */
    public String getDetail() {
        return (String) this.get("detail");
    }

    /**
     *  급식메뉴의 염도, 칼로리 정보를 설정합니다.
     *
     * @param detail
     */
    public void setDetail(String detail) {
        this.put("detail", detail);
    }

    /**
     * @return 급식 사진 url
     */
    public String getImageUrl() {
        return (String) this.get("imageUrl");
    }

    /**
     *  급식 사진 url을 설정합니다
     */
    public void setImageUrl(String imageUrl) {
        this.put("imageUrl", imageUrl);
    }
}
