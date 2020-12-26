package org.kish.database.table;

public class NotificationTable extends Table {
    public NotificationTable() {
        super("kish_notification",
                "CREATE TABLE `kish_notification` (" +
                        "  `id` INT NOT NULL AUTO_INCREMENT, " +
                        "  `topic` TEXT NOT NULL, " +
                        "  `device_id` VARCHAR(152) NOT NULL, " +
                        "  `last_request` TIMESTAMP NOT NULL, " +
                        "  PRIMARY KEY (`id`)" +
                        ") ENGINE = InnoDB;");
    }
}
