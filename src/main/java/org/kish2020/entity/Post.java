package org.kish2020.entity;

import org.json.simple.JSONObject;
import org.kish2020.DataBase.DataBase;

import java.util.HashSet;
import java.util.LinkedHashMap;

public class Post extends DataBase<Object> {
    public Post(String menuID, String postID){
        super("post/posts/" + menuID + "/" + postID + ".json");
        this.setAttachmentUrlMap(new LinkedHashMap<>());
        this.setMenuId(menuID);
        this.setPostId(postID);
    }

    public Post(JSONObject jsonObject){
        super("post/posts/" + jsonObject.get("menuId") + "," + jsonObject.get("postID") + ".json");
        this.put("title", jsonObject.get("title"));
        this.put("content", jsonObject.get("content"));
        this.put("author", jsonObject.get("author"));
        this.put("postDate", jsonObject.get("postDate"));
        this.put("hasAttachment", jsonObject.get("hasAttachment"));
        this.put("attachmentUrlMap", jsonObject.get("attachmentUrlMap"));
        this.put("postID", jsonObject.get("postID"));
        this.put("menuID", jsonObject.get("menuID"));
        this.put("registeredKeyword", jsonObject.get("registeredKeyword"));
        this.put("fullHtml", jsonObject.get("fullHtml"));
    }

    public String getTitle() {
        return (String) this.get("title");
    }

    public void setTitle(String title) {
        this.put("title", title);
    }

    public String getContent() {
        return (String) this.get("content");
    }

    public void setContent(String content) {
        this.put("content", content);
    }

    public String getAuthor() {
        return (String) this.get("author");
    }

    public void setAuthor(String author) {
        this.put("author", author);
    }

    public String getPostDate() {
        return (String) this.get("postDate");
    }

    public void setPostDate(String postDate) {
        this.put("postDate", postDate);
    }

    public LinkedHashMap<String, String> getAttachmentUrlMap() {
        return (LinkedHashMap<String, String>) this.getOrDefault("attachmentUrlMap", new LinkedHashMap<>());
    }

    public void setAttachmentUrlMap(LinkedHashMap<String, String> attachmentUrlMap) {
        this.put("attachmentUrlMap", attachmentUrlMap);
    }
    
    public void addAttachmentUrl(String name, String url){
        LinkedHashMap<String, String> map = this.getAttachmentUrlMap();
        map.put(name, url);
        this.setAttachmentUrlMap(map);
    }

    public String getPostId() {
        return (String) this.get("postID");
    }

    public void setPostId(String postID) {
        this.put("postID", postID);
    }

    public String getMenuId() {
        return (String) this.get("menuId");
    }

    public void setMenuId(String menuId) {
        this.put("menuId", menuId);
    }

    public boolean HasAttachment() {
        return (boolean) this.get("hasAttachment");
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.put("hasAttachment", hasAttachment);
    }

    public HashSet<String> getRegisteredKeyword() {
        return (HashSet<String>) this.get("registeredKeyword");
    }

    public void setRegisteredKeyword(HashSet<String> registeredKeyword) {
        this.put("registeredKeyword", registeredKeyword);
    }

    public String getFullHtml(){
        return (String) this.get("fullHtml");
    }

    public void getFullHtml(String html){
        this.put("fullHtml", html);
    }

    public String getPostKey(){
        return this.getMenuId() + "," + this.getPostId();
    }
}
