package com.example.imessage.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.imessage.R;
import com.example.imessage.models.UserModel;
import com.example.imessage.utils.AndroidUtil;
import com.example.imessage.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfileActivity extends AppCompatActivity {
    ImageView backButton,userProfilePic;
    TextView userName,phoneNumber;
    Button editButton,logoutButton;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Vibrator vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        backButton=findViewById(R.id.backFromProfile);
        userProfilePic=findViewById(R.id.profilePic);
        userName=findViewById(R.id.username);
        phoneNumber=findViewById(R.id.phonenumber);
        logoutButton=findViewById(R.id.logOutButton);
        editButton=findViewById(R.id.editButton);

        getUserDetails();

        // Click Listeners...
        backButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            onBackPressed();
            overridePendingTransition(R.anim.slide_in_left_reverse, R.anim.slide_out_right_reverse);
        }));

        userProfilePic.setOnClickListener((view -> {
            vibrator.vibrate(30);
            Intent intent = new Intent(getApplicationContext(), EditUserProfile.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }));

        userName.setOnClickListener((view -> {
            vibrator.vibrate(30);
            Intent intent = new Intent(getApplicationContext(), EditUserProfile.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }));

        editButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            Intent intent = new Intent(getApplicationContext(), EditUserProfile.class);
            startActivity(intent);
            // Add transition animation
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }));
        //Log Out...
        logoutButton.setOnClickListener((view -> {
            vibrator.vibrate(80);
            showDialogBox();
        }));
    }

    private void showDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to Logout ?")
                .setPositiveButton("Logout",(dialogInterface, i) -> {
                    logoutUser();
                })
                .setNegativeButton("Cancel",(dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }
    private void logoutUser(){
        FirebaseMessaging.getInstance().deleteToken()
                        .addOnCompleteListener(task -> {
                            FirebaseUtil.logOut();
                            Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        });
    }

    private void getUserDetails(){
        FirebaseUtil.getCurrentProfilePicStrorageReffrence().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(getApplicationContext(),uri,userProfilePic);
                            }
                        });
        FirebaseUtil.currentUserDetail().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                userModel = task.getResult().toObject(UserModel.class);
                if(userModel!=null){
                    userName.setText(userModel.getUserName());
                    phoneNumber.setText(userModel.getPhone());
                }
            }
        });
    }

}