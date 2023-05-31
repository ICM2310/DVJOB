package com.example.compumovilp;

public class User {
    private String name;
    private String email;
    private String userID;
    private String profileImageURL;
    private int roll; //1 coordinador //0 usuario

    public void setRoll(int roll){
        this.roll = roll;
    }

    public int getRoll(){
        return roll;
    }


    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getProfileImageURL() {
        return profileImageURL;
    }
    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

}
