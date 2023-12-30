package com.example.imessage.models;

import com.google.firebase.Timestamp;

public class UserModel {

    private String phone;
    private String userName;
    private Timestamp createdTimeStamp;
    private String userID;
    private String fcmToken;
    public UserModel(){

    }
    public UserModel(String phone, String userName, Timestamp createdTimeStamp, String userID) {
        this.phone = phone;
        this.userName = userName;
        this.createdTimeStamp = createdTimeStamp;
        this.userID=userID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
