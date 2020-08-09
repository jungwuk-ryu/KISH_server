package org.kish2020.entity;

import org.json.simple.JSONObject;

public class Post extends JSONObject {

    public Post(String postUrl, String postId, String title, String author, String postDate, String attachmentIconUrl){
        this.setPostUrl(postUrl);
        this.setPostId(postId);
        this.setTitle(title);
        this.setAuthor(author);
        this.setPostDate(postDate);

        this.setAttachmentIconUrl(attachmentIconUrl);
    }

    public String getPostId() {
        return (String) this.get("postId");
    }

    public void setPostId(String postId) {
        this.put("postId", postId);
    }

    public String getTitle() {
        return (String) this.get("title");
    }

    public void setTitle(String title) {
        this.put("title", title);
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

    public String getAttachmentIconUrl() {
        return (String) this.get("attachmentIconUrl");
    }

    public void setAttachmentIconUrl(String attachmentIconUrl) {
        this.put("hasAttachment", !(attachmentIconUrl == null || attachmentIconUrl.isEmpty() || !attachmentIconUrl.contains("http")));
        this.put("attachmentIconUrl", attachmentIconUrl);
    }

    public String getPostUrl() {
        return (String) this.get("postUrl");
    }

    public void setPostUrl(String postUrl) {
        this.put("postUrl", postUrl);
    }

    public boolean hasAttachment() {
        return (boolean) this.get("hasAttachment");
    }

    /*public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }*/
}
