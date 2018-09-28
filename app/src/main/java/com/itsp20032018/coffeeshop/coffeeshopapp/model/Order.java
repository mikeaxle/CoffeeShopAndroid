package com.itsp20032018.coffeeshop.coffeeshopapp.model;

import java.util.ArrayList;
import java.util.Date;

/** order class  - stores all the details for a single order **/
public class Order {
    // class variables
    private Date timestamp;
    private String status = "pending";
    private String employee = "test_staff";

    // array list of menu items where quantity > 0
    public ArrayList<MenuEntry> orderItems = new ArrayList<>();

    // no args constructor, needed to use with FireStore
    public Order(){}

    /**
     * constructor
     * @param timestamp     time the order was added to FireBase
     * @param status        status of the order, can be pending, ready, completed, cancelled
     * @param employee      firebase id of logged in employee creating the order
     * @param orderItems    array list of menu items with quantity > 0
     */
    public Order(Date timestamp, String status, String employee, ArrayList<MenuEntry> orderItems) {
        this.timestamp = timestamp;
        this.status = status;
        this.employee = employee;
        this.orderItems = orderItems;
    }

    /**
     * getTotal     compute total value of order and return
     * @return total
     */
    public double getTotal(){
        double total = 0;

        // total order item value
        if(!orderItems.isEmpty()) {
            for(MenuEntry menuItem: orderItems) {
                // add menu item * quantity to total
                total += (menuItem.getPrice() * menuItem.getQuantity());
            }
        }

        return total;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }


    public ArrayList<MenuEntry> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<MenuEntry> orderItems) {
        this.orderItems = orderItems;
    }
}
