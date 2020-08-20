package org.kish2020.entity;

import org.json.simple.JSONObject;

import java.util.HashMap;

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
