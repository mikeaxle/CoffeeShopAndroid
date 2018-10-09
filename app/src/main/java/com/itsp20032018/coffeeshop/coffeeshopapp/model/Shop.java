package com.itsp20032018.coffeeshop.coffeeshopapp.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Shop {
    private String name = "Coffee Shop";
    private String owner = "WUcTyxxcVmWeJr4ht6bvITCdilE2";


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
