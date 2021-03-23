package org.kish.database.table;

public class KishMenuInfoTable extends Table {
    public KishMenuInfoTable() {
        super("kish_menu_info", "CREATE TABLE `kish_menu_info`(" +
                "    `id` INT NOT NULL," +
                "    `lastupdate` TIMESTAMP NOT NULL," +
                "     PRIMARY KEY(`id`)" +
                ") ENGINE = InnoDB;");
    }
}
