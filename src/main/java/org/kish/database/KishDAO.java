package org.kish.database;

import org.kish.KishServer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.*;


@Repository
public class KishDAO {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public JdbcTemplate jdbcTemplate;

    public KishDAO(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        KishServer.jdbcTemplate = this.jdbcTemplate;
    }

    public int addAdmin(String deviceID){
        String query
                = "INSERT INTO `kish_admin` (`device_id`) VALUES (?);";
        return jdbcTemplate.update(query, deviceID);
    }

    public int removeAdmin(String deviceID){
        String query = "DELETE FROM `kish_admin` WHERE `device_id` = ?";
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
        if(!KishServer.firebaseManager.isExistUser(deviceID)) return -1;
        if(isUserInTopic(topic, deviceID)) return -2;

        String query
                = "INSERT INTO `kish_notification` (`topic`, `device_id`) " +
                "VALUES " +
                "  (?, ?);";

        return jdbcTemplate.update(query, topic, deviceID);
    }

    public int removeUserFromTopic(String topic, String deviceID){
        String query = "DELETE FROM `kish_notification` " +
                "WHERE `device_id` = ? AND `topic` = ?";
        return jdbcTemplate.update(query, deviceID, topic);
    }

    public int removeUserFromAllTopics(String deviceID){
        String query = "DELETE FROM `kish_notification` " +
                "WHERE `device_id` = ?";
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
        String query = "SELECT COUNT(*) FROM `kish_notification` " +
                "WHERE `topic` = '" + topic + "' " +
                "AND `device_id` = '" + deviceID + "'";
        return jdbcTemplate.queryForObject(query, Integer.class) > 0;
    }

    public int addPlanToCalendar(Calendar date, String plan){
        String strDate = sdf.format(date.getTime());
        String query
                = "INSERT INTO `kish_calendar`(`date`, `plan`)\n" +
                "VALUES(?, ?);";

        return jdbcTemplate.update(query, strDate, plan);
    }

    public int removePlanFromCalendar(Calendar date, String plan){
        String strDate = sdf.format(date.getTime());
        String query
                = "DELETE FROM `kish_calendar` WHERE `date` = ? AND `plan` = ?";

        return jdbcTemplate.update(query, strDate, plan);
    }

    // Year and month
    public List<Map<String, Object>> getPlansByYM(Calendar date){
        date.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = sdf.format(date.getTime());
        date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = sdf.format(date.getTime());

        String query = "SELECT * FROM `kish_calendar` " +
                "WHERE `date` " +
                "BETWEEN '" + startDate + "' " +
                "AND '" + endDate + "'";
        return jdbcTemplate.queryForList(query);
    }
}
