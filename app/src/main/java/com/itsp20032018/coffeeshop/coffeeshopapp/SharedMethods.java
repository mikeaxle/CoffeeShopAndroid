package com.itsp20032018.coffeeshop.coffeeshopapp;

class SharedMethods {

    String TAG = "SharedMethods";

    private static final SharedMethods ourInstance = new SharedMethods();

    static SharedMethods getInstance() {
        return ourInstance;
    }

    private SharedMethods() {


    }


}
