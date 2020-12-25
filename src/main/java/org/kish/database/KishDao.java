package org.kish.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public List<String> getDeviceIdByTopic(String topic){
        String query = "SELECT * FROM `kish_notification` " +
                "WHERE `topic` = '" + topic + "'";

        ArrayList<String> list = new ArrayList<>();
        for (Map<String, Object> user : jdbcTemplate.queryForList(query)) {
            list.add((String) user.get("device_id"));
        }

        return list;
    }

    public int addUserToTopic(String topic, String deviceID){
        String query
                = "INSERT INTO `kish_notification` (`topic`, `device_id`) " +
                "VALUES " +
                "  ('?', '?');";

        return jdbcTemplate.update(query, topic, deviceID);
    }

    public int removeUserFromTopic(String topic, String deviceID){
        String query = "DELETE FROM `kish_notification` " +
                "WHERE `device id` = '?' AND `topic` = ?";
        return jdbcTemplate.update(query, deviceID, topic);
    }

    public int removeUserFromAllTopics(String deviceID){
        String query = "DELETE FROM `kish_notification` " +
                "WHERE `device_id` = '?'";
        return jdbcTemplate.update(query, deviceID);
    }

    public int removeUsersFromAllTopics(ArrayList<String> deviceIDs){
        StringBuilder sb = new StringBuilder();
        String query = "DELETE FROM `kish_notification` " +
                "WHERE `device_id` in (?)";

        for (String deviceID : deviceIDs) {
            sb.append(",'").append(deviceID).append("'");
        }

        return jdbcTemplate.update(query, sb.toString());
    }

    public boolean isUserInTopic(String topic, String deviceID){
        String query = "SELECT COUNT FROM `kish_notification` " +
                "WHERE `topic` = '" + topic + "' " +
                "AND `device_id` = '" + deviceID + "'";
        return jdbcTemplate.queryForObject(query, Integer.class) > 0;
    }
}
