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
    private String emailAddress;
    private String image;

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
     * @param emailAddress
     * @param image
     */
    public StaffMember(String firstName, String lastName, String role, String gender, String address, String phoneNumber, String emailAddress, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.image = image;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
