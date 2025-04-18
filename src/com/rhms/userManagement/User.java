package com.rhms.userManagement;

public class User{
    protected String name;
    protected String email;
    protected String password;      //Variables
    protected String phone;
    protected String address;
    protected int userID;

    public User(String name, String email, String password, String phone, String address, int userID){
        this.name = name;
        this.email = email;
        this.password = password;   //Constructors
        this.phone= phone;
        this.address = address;
        this.userID = userID;
    }

    public String getName(){    //Getters
        return name;
    }
    
    public String getEmail(){
        return email;
    }

    // Add new getter method for address
    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}