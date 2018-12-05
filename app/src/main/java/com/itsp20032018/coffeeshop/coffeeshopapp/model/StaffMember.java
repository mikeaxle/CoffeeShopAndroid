package com.itsp20032018.coffeeshop.coffeeshopapp.model;

/**
 * Staff Member class
 */
public class StaffMember {
    // role enum
    enum Role {
        WAITER,
        WAITRESS,
        CHEF,
        MANAGER;
    }

    // gender enum
    enum Gender {
        MALE,
        FEMALE;
    }

    // class variables
    private String firstName;
    private String lastName;
    private String role;
    private String gender;
    private String address;
    private String phoneNumber;
    private String email;
    private String image;
    private String shop;
    private String uid;
    private String passWord;

    // no args constructor, needed to use with FireStore
    public StaffMember(){}

    /**
     * Constructor
     * @param firstName
     * @param lastName
     * @param role
     * @param gender
     * @param address
     * @param phoneNumber
     * @param email
     * @param image
     */
    public StaffMember(String firstName, String lastName, String role, String gender, String address, String phoneNumber, String email, String image, String shop, String uid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.image = image;
        this.shop = shop;
        this.uid = uid;
    }

    /**
     * Constructor with password
     * @param firstName
     * @param lastName
     * @param role
     * @param gender
     * @param address
     * @param phoneNumber
     * @param emailAddress
     * @param image
     * @param password
     */
    public StaffMember(String firstName, String lastName, String role, String gender, String address, String phoneNumber, String emailAddress, String image, String shop, String uid, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = emailAddress;
        this.image = image;
        this.shop = shop;
        this.uid = uid;
        this.passWord = password;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
