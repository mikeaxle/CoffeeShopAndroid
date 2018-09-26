package com.itsp20032018.coffeeshop.coffeeshopapp.model;

import java.util.ArrayList;

/**
 * Menu Item class
 */
public class MenuEntry {
    // class variables
    private String name;
    private double price;
    private String description;
    private ArrayList<String> inventoryItems;
    private boolean available;
    private String image;

    // no args constructor, needed to use with FireStore
    public MenuEntry() {
    }

    /**
     * Constructor
     * @param name
     * @param price
     * @param description
     * @param inventoryItems
     * @param available
     * @param image
     */
    public MenuEntry(String name, double price, String description, ArrayList<String> inventoryItems, boolean available, String image) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.inventoryItems = inventoryItems;
        this.available = available;
        this.image = image;
    }

    /**
     * Overloaded Constructor
     * @param name
     * @param price
     * @param description
     * @param available
     * @param image
     */
    public MenuEntry(String name, double price, String description, boolean available, String image) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.available = available;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(ArrayList<String> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
