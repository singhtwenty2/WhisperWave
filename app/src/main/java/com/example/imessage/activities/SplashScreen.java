package com.example.imessage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imessage.R;
import com.example.imessage.models.UserModel;
import com.example.imessage.utils.AndroidUtil;
import com.example.imessage.utils.FirebaseUtil;
import com.example.imessage.utils.ThemeUtils;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash_screen);

        ThemeUtils.SetStatusBar(this, R.color.white);

        if (FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null) {
            // App opened through notification
            String userId = getIntent().getStringExtra("userId");
            assert userId != null;
            FirebaseUtil.getUsersCollection().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel model = task.getResult().toObject(UserModel.class);
                            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            assert model != null;
                            AndroidUtil.passUserModelAsIntent(intent, model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
        } else {
            new Handler().postDelayed(() -> {
                if (FirebaseUtil.isLoggedIn()) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginScreen.class));
                }
                finish();
            }, 2000);
        }

    }
}