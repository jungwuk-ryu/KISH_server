package org.kish.database.mapper;

import org.kish.entity.LunchMenu;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class LunchMenuMapper implements RowMapper<LunchMenu> {
    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public LunchMenu mapRow(ResultSet rs, int i) throws SQLException {
        LunchMenu menu = new LunchMenu(
                SDF.format(rs.getDate("lunch_date")),
                rs.getString("menu"),
                rs.getString("detail"),
                rs.getString("image_url"));

        menu.setDinnerMenu(rs.getString("dinner_menu"));
        return menu;
    }
}
