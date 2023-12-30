package com.example.imessage.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {

    private String chatRoomID;
    private List<String> userIDs;
    private Timestamp lastMessageTimeStamp;
    private String lastMessageSenderID;
    private String lastMessage;

    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatRoomID, List<String> userIDs, Timestamp lastMessageTimeStamp, String lastMessageSenderID) {
        this.chatRoomID = chatRoomID;
        this.userIDs = userIDs;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.lastMessageSenderID = lastMessageSenderID;
    }

    public String getChatRoomID() {
        return chatRoomID;
    }

    public void setChatRoomID(String chatRoomID) {
        this.chatRoomID = chatRoomID;
    }

    public List<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(List<String> userIDs) {
        this.userIDs = userIDs;
    }

    public Timestamp getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(Timestamp lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getLastMessageSenderID() {
        return lastMessageSenderID;
    }

    public void setLastMessageSenderID(String lastMessageSenderID) {
        this.lastMessageSenderID = lastMessageSenderID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
