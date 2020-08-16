package org.kish2020.entity;

import org.json.simple.JSONObject;

import java.util.HashSet;

public class Post extends JSONObject {
    public Post(JSONObject jsonObject){
        this.put("title", jsonObject.get("title"));
        this.put("content", jsonObject.get("content"));
        this.put("author", jsonObject.get("author"));
        this.put("postDate", jsonObject.get("postDate"));
        this.put("hasAttachment", jsonObject.get("hasAttachment"));
        this.put("attachmentUrl", jsonObject.get("attachmentUrl"));
        this.put("postId", jsonObject.get("postId"));
        this.put("menuID", jsonObject.get("menuId"));
        this.put("registeredKeyword", jsonObject.get("registeredKeyword"));
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

    public String getAttachmentUrl() {
        return (String) this.get("attachmentUrl");
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.put("attachmentUrl", attachmentUrl);
    }

    public String getPostId() {
        return (String) this.get("postId");
    }

    public void setPostId(String postId) {
        this.put("postId", postId);
    }

    public String getMenuId() {
        return (String) this.get("menuId");
    }

    public void setMenuId(String menuId) {
        this.put("menuId", menuId);
    }

    public boolean isHasAttachment() {
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
}
