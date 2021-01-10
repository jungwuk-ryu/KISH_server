package org.kish.database.table;

public class LunchTable extends Table {
    public LunchTable(){
        super("kish_lunch",
                "CREATE TABLE `kish_lunch`(" +
                        "    `lunch_date` DATE NOT NULL," +
                        "    `menu` TEXT NOT NULL," +
                        "    `detail` VARCHAR(128) NOT NULL," +
                        "    `image_url` TEXT NOT NULL," +
                        "    PRIMARY KEY(`lunch_date`)" +
                        ") ENGINE = InnoDB;");
    }
}
