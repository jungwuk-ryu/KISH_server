package org.kish.entity;

/**
 *  게시글의 본문과 같은 내용을 포함하고 있는 클래스입니다.
 *  DataBase를 상속받아 save() 메소드를 사용할경우 post/posts/{메뉴ID}/{postID}.json 에 저장됩니다.
 *  또한 postKey는 {메뉴ID},{postID}의 형태로서 95,28 같은 String입니다.
 */
@SuppressWarnings("unchecked")
public class Post{
    private String title, author, content, post_date;
    private int menu, id;
    private long last_updated;
    private boolean hasAttachments;

    public Post(){}

    public Post(int menu, int id){
        this.menu = menu;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public int getMenu() {
        return menu;
    }

    public void setMenu(int menu) {
        this.menu = menu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    public boolean hasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public void setHasAttachments(int i){
        this.hasAttachments = i > 0;
    }
}
