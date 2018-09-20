package com.itsp20032018.coffeeshop.coffeeshopapp.model;

/**
 * Stock Item class
 */
public class StockItem{
    // class variables
    public String name;
    public double price;
    public double quantity;
    public double reorder;
    public String image;
    public boolean available;

    // no args constructor
    StockItem(){}

    /**
     * Constructor
     * @param name
     * @param price
     * @param quantity
     * @param reorder
     * @param image
     */
    public StockItem(String name, double price, double quantity, double reorder, String image, boolean available){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.reorder = reorder;
        this.image = image;
        this.available = available;
    }
}