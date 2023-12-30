package com.example.imessage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imessage.R;
import com.example.imessage.models.UserModel;
import com.example.imessage.utils.AndroidUtil;
import com.example.imessage.utils.FirebaseUtil;

public class OtherUserDetail extends AppCompatActivity {

    TextView otherUserName,otherUserPhone;
    Button doneButton;
    ImageView otherUserProfilePic;
    UserModel senderModel;
     LinearLayout callButton, chatButton, videoCallButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_other_user_detail);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        senderModel = AndroidUtil.getUserModelFromIntent(getIntent());

        otherUserName=findViewById(R.id.otherUserName);
        otherUserPhone=findViewById(R.id.otherUserPhone);
        otherUserProfilePic=findViewById(R.id.otherUserProfilePic);
        callButton=findViewById(R.id.callItem);
        chatButton=findViewById(R.id.chatItem);
        videoCallButton =findViewById(R.id.videoItem);
        doneButton=findViewById(R.id.doneTV);


        callButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            String phoneNumber = senderModel.getPhone();
            if(phoneNumber!=null && !phoneNumber.isEmpty()){
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(intent);
            }
        }));
        chatButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            onBackPressed();
            overridePendingTransition(R.anim.slide_in_left_reverse, R.anim.slide_out_right_reverse);
        }));
        videoCallButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            Intent intent = new Intent(getApplicationContext(), VideoCallUI.class);
            startActivity(intent);
 //           Toast.makeText(this, "Feature unavailable; unable to initiate video calls programmatically.", Toast.LENGTH_LONG).show();
        }));


        FirebaseUtil.getOtherProfilePicStrorageReffrence(senderModel.getUserID()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(getApplicationContext(),uri,otherUserProfilePic);
                    }
                });
        otherUserName.setText(senderModel.getUserName());
        otherUserPhone.setText(senderModel.getPhone());


        doneButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            onBackPressed();
            overridePendingTransition(R.anim.slide_in_left_reverse, R.anim.slide_out_right_reverse);
        }));

        otherUserPhone.setOnLongClickListener((view -> {
            String text = otherUserPhone.getText().toString().trim();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Phone Number", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Phone number copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        }));
    }
}