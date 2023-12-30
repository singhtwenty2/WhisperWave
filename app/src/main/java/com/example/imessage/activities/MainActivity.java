package com.example.imessage.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.imessage.R;
import com.example.imessage.adapters.RecentChatAdapters;
import com.example.imessage.models.ChatRoomModel;
import com.example.imessage.utils.AndroidUtil;
import com.example.imessage.utils.FirebaseUtil;
import com.example.imessage.utils.ThemeUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView profileImageView;
    TextView editChatsTextView, searchButton;
    RecyclerView recyclerView;
    RecentChatAdapters adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        ThemeUtils.SetStatusBar(this,R.color.google_white);

        profileImageView=findViewById(R.id.mainAppBarProfile);
        searchButton =findViewById(R.id.mainSearchET);
        editChatsTextView=findViewById(R.id.editInMainAppBar);
        recyclerView=findViewById(R.id.mainRecycler);
        FirebaseUtil.getCurrentProfilePicStrorageReffrence().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getApplicationContext(),uri,profileImageView);
                    }
                });

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setupMainRV();

        profileImageView.setOnClickListener((view -> {
            vibrator.vibrate(30);
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }));

        searchButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
        }));

        generateFCMToken();

    }

    private void setupMainRV() {
        Query query = FirebaseUtil.allChatRoomCollectionReference()
                .whereArrayContains("userIDs",FirebaseUtil.currentUserID())
                .orderBy("lastMessageTimeStamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class)
                .build();

        adapter = new RecentChatAdapters(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void generateFCMToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        String token = task.getResult();
//                        Log.d("Token",token);
                        FirebaseUtil.currentUserDetail().update("fcmToken",token);
                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
    

}