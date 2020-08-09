package org.kish2020.entity;

import org.json.simple.JSONObject;

public class Post extends JSONObject {

    public Post(String postUrl, String postId, String title, String author, String postDate, String attachmentUrl){
        this.setPostUrl(postUrl);
        this.setPostId(postId);
        this.setTitle(title);
        this.setAuthor(author);
        this.setPostDate(postDate);

        this.setAttachmentUrl(attachmentUrl);
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

    public String getAttachmentUrl() {
        return (String) this.get("attachmentUrl");
    }

    public void setAttachmentUrl(String attachmentUrl) {
        // AttachmentUrl은 첨부파일의 direct 다운로드 주소가 아닌 첨부파일 유형 이미지의 url입니다.
        this.put("hasAttachment", !(attachmentUrl == null || attachmentUrl.isEmpty() || !attachmentUrl.contains("http")));
        this.put("attachmentUrl", attachmentUrl);
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
