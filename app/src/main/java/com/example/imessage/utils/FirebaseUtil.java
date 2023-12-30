package com.example.imessage.utils;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    public static String currentUserID(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        return currentUserID() != null;
    }

    public static DocumentReference currentUserDetail(){
       return FirebaseFirestore.getInstance().collection("users").document(currentUserID());
    }

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection("users");
    }
    public static DocumentReference getChatRoomReference(String chatRoomId){
       return FirebaseFirestore.getInstance().collection("chatroom").document(chatRoomId);
    }
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatRoomReference(chatroomId).collection("chats");
    }
    public static String getChatRoomId(String userId1, String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2 ;
        }
        else{
            return userId2+"_"+userId1 ;
        }
    }
    public static CollectionReference allChatRoomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatroom");
    }

    public static DocumentReference getOtherUser(List<String> userIds){

        if(userIds.get(0).equals(FirebaseUtil.currentUserID())){
            return getUsersCollection().document(userIds.get(1));
        }
        else {
            return getUsersCollection().document(userIds.get(0));
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String stringTime(Timestamp timestamp){
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }
    public static void logOut(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicStrorageReffrence(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserID());
    }
    public static StorageReference getOtherProfilePicStrorageReffrence(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
    

}
