package org.kish.entity;

import org.json.simple.JSONObject;

/**
 *  메뉴에서 얻을 수 있는 게시물의 정보입니다.
 *  PostInfo와는 다르게 첨부파일 다운로드 url은 존재하지 않으며 첨부파일 icon url이 존재합니다.
 */
@SuppressWarnings("unchecked")
public class SimplePost extends JSONObject {

    public SimplePost(String postUrl, int menuID, int postId, String title, String author, String postDate, String attachmentIconUrl){
        this.setPostUrl(postUrl);
        this.setPostId(postId);
        this.setTitle(title);
        this.setAuthor(author);
        this.setPostDate(postDate);
        this.setMenuId(menuID);

        this.setAttachmentIconUrl(attachmentIconUrl);
    }

    public SimplePost(Post post) {
        this(post.getUrl(), post.getMenu(), post.getId(), post.getTitle(), post.getAuthor(), post.getPost_date(), "");
    }

    public int getPostId() {
        return (int) this.get("postId");
    }

    public void setPostId(int postId) {
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

    public int getMenuId(){
        return (int) this.get("menuID");
    }

    public void setMenuId(int menuId){
        this.put("menuID", menuId);
    }
}
