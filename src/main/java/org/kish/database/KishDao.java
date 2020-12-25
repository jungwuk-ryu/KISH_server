package org.kish.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class KishDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int addAdmin(String deviceID){
        String query
                = "INSERT INTO `kish_admin` (`device_id`) VALUES ('?');";
        return jdbcTemplate.update(query, deviceID);
    }

    public int removeAdmin(String deviceID){
        String query = "DELETE FROM `kish_admin` WHERE `device_id` = '?'";
        return jdbcTemplate.update(query, deviceID);
    }

    public boolean isAdmin(String deviceID){
        String query
                = "SELECT * FROM `kish_admin` " +
                "WHERE `device_id` = '" + deviceID + "'";
        return jdbcTemplate.queryForList(query).size() > 0;
    }
}
