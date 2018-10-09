package com.itsp20032018.coffeeshop.coffeeshopapp.model;

public class Shop {
    private String name;
    private String owner;

    Shop(){}

    public Shop(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
