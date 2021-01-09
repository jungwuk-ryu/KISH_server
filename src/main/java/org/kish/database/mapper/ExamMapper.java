package org.kish.database.mapper;

import org.kish.entity.Exam;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExamMapper implements RowMapper<Exam> {
    @Override
    public Exam mapRow(ResultSet resultSet, int i) throws SQLException {
        Exam exam = new Exam(resultSet.getInt("id")
                , resultSet.getDate("date").getTime() / 1000
                , resultSet.getString("label"));

        return exam;
    }
}
