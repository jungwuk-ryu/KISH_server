package org.kish.database.table;

public class ExamTable extends Table {
    public ExamTable(){
        super("kish_exam", "CREATE TABLE `kish_exam`(" +
                "    `id` INT NOT NULL AUTO_INCREMENT," +
                "    `date` DATE NOT NULL," +
                "    `label` TINYTEXT NOT NULL," +
                "    PRIMARY KEY(`id`)" +
                ") ENGINE = InnoDB;");
    }
}
