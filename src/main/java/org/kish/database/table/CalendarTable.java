package org.kish.database.table;

public class CalendarTable extends Table {
    public CalendarTable(){
        super("kish_calendar",
                "CREATE TABLE `TEST`.`kish_calendar`(" +
                        "    `id` INT NOT NULL AUTO_INCREMENT," +
                        "    `date` DATE NOT NULL," +
                        "    `plan` VARCHAR(255) NOT NULL," +
                        "    PRIMARY KEY(`id`)" +
                        ") ENGINE = InnoDB");
    }
}
