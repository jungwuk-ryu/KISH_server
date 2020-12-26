package org.kish.database;

import org.kish.MainLogger;
import org.kish.database.mapper.PostMapper;
import org.kish.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Repository
public class PostDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insertPost(Post post){
        String query = "INSERT INTO kish_posts(menu, id, title, author, content, post_date, has_attachments) VALUES(?,?,?,?,?,?,?);";
        return jdbcTemplate.update(query, post.getMenu(),
                post.getId(),
                post.getTitle(),
                post.getAuthor(),
                post.getContent(),
                post.getPost_date(),
                post.hasAttachments());
    }

    public Post selectPost(Post post){
        return this.selectPost(post.getMenu(), post.getId());
    }

    public Post selectPost(int menu, int id){
        String query = "SELECT * FROM `kish_posts` WHERE `menu` = ? AND `id` = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{menu, id}, new PostMapper());
    }

    public List<Post> searchPost(String content, int index){
        if(content.trim().isEmpty()){
            return new ArrayList<>();
        }

        String titleSrc, contentSrc;
        StringTokenizer st = new StringTokenizer(content, " ");

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while(st.hasMoreTokens()){
            String token = st.nextToken();

            if(i != 0) sb.append(" AND ");
            sb.append("title LIKE '%").append(token).append("%'");
            i++;
        }

        titleSrc = sb.toString();
        contentSrc = titleSrc.replace("title", "content");

        String query = "SELECT title, content, (?) + (?) AS score " +
                "FROM `kish_posts` WHERE (?) OR (?) " +
                "ORDER BY score DESC, `last_updated` DESC LIMIT " + (index * 10) + ", 10";
        return jdbcTemplate.query(query, new Object[]{titleSrc, contentSrc, titleSrc, contentSrc}, new PostMapper());
    }

    public int updatePost(Post post){
        String query = "UPDATE `kish_posts` SET `title` = ?" +
                ", `author` = ?" +
                ", `content` = ?" +
                ", `post_date`" +
                ", `has_attachments` = ? WHERE `kish_posts`.`menu` = ? AND `kish_posts`.`id` = ?";
        return jdbcTemplate.update(query,
                post.getTitle(), post.getAuthor(),
                post.getContent(), post.getPost_date(),
                (post.hasAttachments() ? 1 : 0), post.getMenu(), post.getId());
    }

    public boolean isExistPost(Post post){
        return this.isExistPost(post.getMenu(), post.getId());
    }

    public boolean isExistPost(int menu, int id){
        String query = "SELECT COUNT(*) FROM `kish_posts` WHERE `menu` = ? AND `id` = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{menu, id}, Integer.class) != 0;
    }

    public int removePost(Post post){
        return this.removePost(post.getMenu(), post.getId());
    }

    public int removePost(int menu, int id){
        MainLogger.info("게시물 제거하는 중 : " + menu + "," + id);

        String query = "DELETE FROM `kish_posts` WHERE `kish_posts`.`menu` = ? AND `kish_posts`.`id` = ?";
        return jdbcTemplate.update(query, menu, id);
    }
}
