package org.kish.database;

import org.kish.database.mapper.PostMapper;
import org.kish.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// TODO : search
@Repository
public class PostDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void init(){
        for (Tables table : Tables.values()) {
            String tableExitQuery = "SHOW TABLES LIKE '" + table.toString() + "'";
            if(jdbcTemplate.queryForList(tableExitQuery).size() < 1){
                jdbcTemplate.execute(table.getCreateQuery());
                jdbcTemplate.execute(table.getPrimaryQuery().replace("?", table.toString()));
            }
        }
    }

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

    public int deletePost(Post post){
        String query = "DELETE FROM `kish_posts` WHERE `kish_posts`.`menu` = ? AND `kish_posts`.`id` = ?";
        return jdbcTemplate.update(query, post.getMenu(), post.getId());
    }

    private enum Tables{
        POSTS("kish_posts",
                "CREATE TABLE `kish_posts` " +
                        "( " +
                        "`menu` SMALLINT NOT NULL ," +
                        "`id` MEDIUMINT NOT NULL ," +
                        "`title` VARCHAR(64) NOT NULL ," +
                        "`author` VARCHAR(64) NOT NULL ," +
                        "`content` MEDIUMTEXT NOT NULL ," +
                        "`post_date` VARCHAR(25) NOT NULL ," +
                        "`last_updated` TIMESTAMP NOT NULL ," +
                        "`has_attachments` BOOLEAN NOT NULL " +
                        ") " +
                        "ENGINE = InnoDB;",
                "ALTER TABLE `?` ADD PRIMARY KEY( `menu`, `id`);");

        private final String name;
        private final String createQuery, primaryQuery;

        Tables(String name, String createQuery, String primaryQuery){
            this.name = name;
            this.createQuery = createQuery;
            this.primaryQuery = primaryQuery;
        }

        @Override
        public String toString(){
            return name;
        }

        public String getName() {
            return name;
        }

        public String getCreateQuery() {
            return createQuery;
        }

        public String getPrimaryQuery() {
            return primaryQuery;
        }
    }
}
