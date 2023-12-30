package com.example.imessage.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.imessage.R;
import com.example.imessage.models.UserModel;
import com.example.imessage.utils.FirebaseUtil;
import com.example.imessage.utils.ThemeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserNameInput extends AppCompatActivity {

    Button getStartedButton;
    EditText inputUserName;
    LottieAnimationView progressBar;
    String phoneNumber;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_input);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ThemeUtils.SetStatusBar(this,R.color.cupertino_grey);

        getStartedButton=findViewById(R.id.getStarted);
        inputUserName=findViewById(R.id.inputUsername);
        progressBar=findViewById(R.id.iosProgress);

        phoneNumber=getIntent().getStringExtra("phone");

        getUsername();


        getStartedButton.setOnClickListener(view -> {
            vibrator.vibrate(30);
            setUserName();
        });
    }

    private void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUserDetail().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful()){
                userModel = task.getResult().toObject(UserModel.class);
                if(userModel!=null){
                    inputUserName.setText(userModel.getUserName());
                }
            }
        });
    }

    private void setUserName(){
        String userName = inputUserName.getText().toString();
        if(userName.isEmpty() || userName.length()<3){
            inputUserName.setError("Username should be greater than 3 characters");
            return;
        }
        setInProgress(true);
        if(userModel!=null){
            userModel.setUserName(userName);
        }
        else{
            userModel = new UserModel(phoneNumber,userName, Timestamp.now(),FirebaseUtil.currentUserID());
        }

        FirebaseUtil.currentUserDetail().set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if(task.isSuccessful()){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                Toast.makeText(UserNameInput.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            getStartedButton.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            getStartedButton.setVisibility(View.VISIBLE);
        }
    }
}