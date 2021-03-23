package org.kish.database.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KishOrderedMenuIdMapper implements RowMapper<String> {

    @Override
    public String mapRow(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getString("id");
    }
}
