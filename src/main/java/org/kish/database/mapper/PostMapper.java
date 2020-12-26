package org.kish.database.mapper;

import org.kish.entity.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int no) throws SQLException {
        Post post = new Post();
        post.setMenu(rs.getInt("menu"));
        post.setId(rs.getInt("id"));
        post.setTitle(rs.getString("title"));
        post.setAuthor(rs.getString("author"));
        post.setContent(rs.getString("content"));
        post.setPost_date(rs.getString("post_date"));
        post.setHasAttachments(rs.getBoolean("has_attachments"));
        post.setLast_updated(rs.getTimestamp("last_updated").getTime());

        return post;
    }
}
