package com.example.imessage.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imessage.R;
import com.example.imessage.models.UserModel;

public class AndroidUtil {

    public static void passUserModelAsIntent(Intent intent, UserModel userModel){
        intent.putExtra("userName",userModel.getUserName());
        intent.putExtra("phoneNumber",userModel.getPhone());
        intent.putExtra("userID",userModel.getUserID());
        intent.putExtra("fcmToken",userModel.getFcmToken());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUserName(intent.getStringExtra("userName"));
        userModel.setPhone(intent.getStringExtra("phoneNumber"));
        userModel.setUserID(intent.getStringExtra("userID"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));

        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

}
