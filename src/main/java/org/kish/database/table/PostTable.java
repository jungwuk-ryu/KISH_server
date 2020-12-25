package org.kish.database.table;

public class PostTable extends Table {
    public PostTable(){
        super("kish_posts",
                "CREATE TABLE `kish_posts` " +
                        "( " +
                        "`menu` SMALLINT NOT NULL ," +
                        "`id` MEDIUMINT NOT NULL ," +
                        "`title` VARCHAR(64) NOT NULL ," +
                        "`author` VARCHAR(64) NOT NULL ," +
                        "`content` MEDIUMTEXT NOT NULL ," +
                        "`post_date` VARCHAR(25) NOT NULL ," +
                        "`last_updated` TIMESTAMP NOT NULL ," +
                        "`has_attachments` BOOLEAN NOT NULL ," +
                        "PRIMARY KEY( `menu`, `id`)) " +
                        "ENGINE = InnoDB;");
    }
}
