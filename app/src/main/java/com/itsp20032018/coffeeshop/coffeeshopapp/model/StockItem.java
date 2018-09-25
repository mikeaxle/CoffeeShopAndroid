package com.itsp20032018.coffeeshop.coffeeshopapp.model;

/**
 * Stock Item class
 */
public class StockItem{
    // class variables
    private String name;
    private double price;
    private double quantity;
    private double reorder;
    private String image;
    private boolean available;

    // no args constructor, needed to use with FireStore
    StockItem(){}

    /**
     * Constructor
     * @param name      name of stock item
     * @param price     price of stock item
     * @param quantity  available quantity of item in stock
     * @param reorder   level at which stock is automatically reordered from supplier
     * @param image     string url of image location in FireBase storage
     */
    public StockItem(String name, double price, double quantity, double reorder, String image, boolean available){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.reorder = reorder;
        this.image = image;
        this.available = available;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getReorder() {
        return reorder;
    }

    public void setReorder(double reorder) {
        this.reorder = reorder;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}