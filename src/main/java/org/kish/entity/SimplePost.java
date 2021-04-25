package org.kish.entity;

import lombok.Getter;
import lombok.Setter;

/**
 *  메뉴에서 얻을 수 있는 게시물의 정보입니다.
 *  PostInfo와는 다르게 첨부파일 다운로드 url은 존재하지 않으며 첨부파일 icon url이 존재합니다.
 */
@SuppressWarnings("unchecked")
@Getter
@Setter
public class SimplePost {
    private String url;
    private String title;
    private String author;
    private String postDate;
    private String attachmentIconUrl;
    private boolean hasAttachment;
    private int menu;
    private int id;

    public SimplePost(String postUrl, int menu, int id, String title, String author, String postDate, String attachmentIconUrl){
        this.setUrl(postUrl);
        this.setMenu(menu);
        this.setTitle(title);
        this.setAuthor(author);
        this.setPostDate(postDate);
        this.setId(id);

        this.setAttachmentIconUrl(attachmentIconUrl);
    }

    public SimplePost(Post post) {
        this(post.getUrl(), post.getMenu(), post.getId(), post.getTitle(), post.getAuthor(), post.getPost_date(), "");
    }

    public void setAttachmentIconUrl(String attachmentIconUrl) {
        this.hasAttachment = !(attachmentIconUrl == null || attachmentIconUrl.isEmpty() || !attachmentIconUrl.contains("http"));
        this.attachmentIconUrl = attachmentIconUrl;
    }
}
