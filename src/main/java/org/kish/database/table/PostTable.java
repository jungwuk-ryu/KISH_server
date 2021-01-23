package org.kish.database.table;

public class PostTable extends Table {
    public PostTable(){
        super("kish_posts",
                "CREATE TABLE `kish_posts` " +
                        "( " +
                        "`menu` SMALLINT NOT NULL ," +
                        "`id` MEDIUMINT NOT NULL ," +
                        "`title` VARCHAR(512) NOT NULL ," +
                        "`author` VARCHAR(128) NOT NULL ," +
                        "`content` MEDIUMTEXT NOT NULL ," +
                        "`post_date` DATE NOT NULL ," +
                        "`last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "`has_attachments` BOOLEAN NOT NULL ," +
                        "PRIMARY KEY( `menu`, `id`)) " +
                        "ENGINE = InnoDB CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;");
    }
}
