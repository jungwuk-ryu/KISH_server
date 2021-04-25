package org.kish.entity;

import lombok.Getter;

@Getter
public class Menu {
    private int id;
    private String name;

    public Menu(int id, String name) {
       this.id = id;
       this.name = name;
    }
}
