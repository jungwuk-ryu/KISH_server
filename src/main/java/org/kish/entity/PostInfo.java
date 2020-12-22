package org.kish.entity;

import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 *  게시물의 저자, 제목과 같은 비교적 간단한 정보들을 가지는 클래스입니다.
 *  Post객체와는 다르게 HashMap을 상속받아 PostApiController의 PostInfo DataBase를 통해 저장됩니다.
 *
 * @see org.kish.web.PostApiController
 */
@SuppressWarnings("unchecked")
@Deprecated
public class PostInfo extends HashMap<String, Object> {
    public PostInfo(JSONObject jsonObject){
        super(jsonObject);
    }

    public PostInfo(Post post){
        this.setPostID(post.getPostId());
        this.setMenuID(post.getMenuId());
        this.setTitle(post.getTitle());
        this.setAuthor(post.getAuthor());
        this.setPostDate(post.getPostDate());
        this.setHasAttachment(post.HasAttachment());
    }

    public void setTitle(String title){
        this.put("title", title);
    }

    public void setAuthor(String author){
        this.put("author", author);
    }

    public void setPostDate(String postDate){
        this.put("postDate", postDate);
    }

    public void setHasAttachment(boolean b){
        this.put("hasAttachment", b);
    }

    public void setPostID(String postID){
        this.put("postID", postID);
        this.put("url", "http://www.hanoischool.net/default.asp?board_mode=view&menu_no=" + getMenuID() + "&bno=" + postID);
    }

    public void setMenuID(String menuID){
        this.put("menuID", menuID);
        this.put("url", "http://www.hanoischool.net/default.asp?board_mode=view&menu_no=" + menuID + "&bno=" + this.getPostID());
    }

    public String getTitle(){
        return (String) this.get("title");
    }

    public String getAuthor(){
        return (String) this.get("author");
    }

    public String getPostDate(){
        return (String) this.get("postDate");
    }

    public boolean hasAttachments(){
        return (boolean) this.get("hasAttachment");
    }

    public String getPostID(){
        return (String) this.get("postID");
    }

    public String getMenuID(){
        return (String) this.get("menuID");
    }

    public String getUrl(){
        return (String) this.get("url");
    }

}
