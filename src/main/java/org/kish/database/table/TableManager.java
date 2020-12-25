package org.kish.database.table;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.LinkedHashMap;

public class TableManager extends JdbcDaoSupport {
    private LinkedHashMap<String, Table> tables = new LinkedHashMap<>();
    private JdbcTemplate jdbcTemplate;

    public TableManager(){
        jdbcTemplate = getJdbcTemplate();
        this.addTable(new PostTable());
    }

    public void checkAllTable(){
        for (Table table : tables.values()) {
            this.checkTable(table);
        }
    }

    public void checkTable(Table table){
        String tableExitQuery = "SHOW TABLES LIKE '" + table.toString() + "'";
        if(jdbcTemplate.queryForList(tableExitQuery).size() < 1){
            jdbcTemplate.execute(table.getCreateQuery());
        }
    }

    public void addTable(Table table){
        this.tables.put(table.getName(), table);
    }

    public void removeTable(Table table){
        this.tables.remove(table.getName());
    }
}
