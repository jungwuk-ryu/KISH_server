package org.kish.entity;

import lombok.Getter;
import lombok.Setter;

/**
 *  게시글의 본문과 같은 내용을 포함하고 있는 클래스입니다.
 *  DataBase를 상속받아 save() 메소드를 사용할경우 post/posts/{메뉴ID}/{postID}.json 에 저장됩니다.
 *  또한 postKey는 {메뉴ID},{postID}의 형태로서 95,28 같은 String입니다.
 */
@SuppressWarnings("unchecked")
@Getter
@Setter
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

    public boolean hasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean v){
        this.hasAttachments = v;
    }

    public void setHasAttachments(int i){
        this.hasAttachments = i > 0;
    }
}
