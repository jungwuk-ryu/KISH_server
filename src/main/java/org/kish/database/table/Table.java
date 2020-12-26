package org.kish.database.table;

public class Table {
    private final String name, createQuery;

    public Table(){
        this.name = "";
        this.createQuery = "";
    }

    public Table(String name, String createQuery){
        this.name = name;
        this.createQuery = createQuery;
    }

    public String getName() {
        return name;
    }

    public String getCreateQuery() {
        return createQuery;
    }
}
