package com.example.imessage.activities;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.imessage.R;
import com.example.imessage.adapters.ChatAdapter;
import com.example.imessage.models.ChatMessageModel;
import com.example.imessage.models.ChatRoomModel;
import com.example.imessage.models.UserModel;
import com.example.imessage.utils.AndroidUtil;
import com.example.imessage.utils.FirebaseUtil;
import com.example.imessage.utils.ThemeUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    TextView backButton,senderName;
    ImageView senderProfilePic,sendButton,callButton;
    RecyclerView recyclerView;
    EditText inputMessage;
    String chatroomId,message;
    ChatRoomModel chatRoomModel;
    ChatAdapter adapter;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setDynamicTheme(this);
        setContentView(R.layout.activity_chat);

        ThemeUtils.SetStatusBar(this,R.color.cupertino_grey);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mediaPlayer = MediaPlayer.create(this,R.raw.out_going_tune);

        //Get UserModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        //Chatroom ID.. Unique but common for sender and receiver ...
        chatroomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserID(),otherUser.getUserID());

        backButton=findViewById(R.id.backFromChat);
        senderName=findViewById(R.id.chatUserName);
        senderProfilePic=findViewById(R.id.chatProfilePic);
        recyclerView=findViewById(R.id.chatRV);
        inputMessage=findViewById(R.id.inputChat);
        sendButton=findViewById(R.id.sendIV);
        callButton=findViewById(R.id.callButton);

        senderName.setText(otherUser.getUserName());
        FirebaseUtil.getOtherProfilePicStrorageReffrence(otherUser.getUserID()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getApplicationContext(),uri,senderProfilePic);
                    }
                });

        senderName.setOnClickListener((view -> {
            vibrator.vibrate(30);
            Intent intent = new Intent(getApplicationContext(), OtherUserDetail.class);
            AndroidUtil.passUserModelAsIntent(intent,otherUser);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }));
        senderProfilePic.setOnClickListener((view -> {
            vibrator.vibrate(30);
            Intent intent = new Intent(getApplicationContext(), OtherUserDetail.class);
            AndroidUtil.passUserModelAsIntent(intent,otherUser);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }));
        getOrCreateChatRoomModel();

        setUpChatRV();

        sendButton.setOnClickListener(view -> {
            message=inputMessage.getText().toString().trim();
            if(message.isEmpty()){
                return;
            }
            vibrator.vibrate(30);
            playMediaTune();
            inputMessage.setText("");
            sendMessageToUser(message);
        });

        backButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            onBackPressed();
        }));

        callButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
           String phoneNumber = otherUser.getPhone();
           if(phoneNumber!=null && !phoneNumber.isEmpty()){
               Intent intent = new Intent(Intent.ACTION_DIAL);
               intent.setData(Uri.parse("tel:"+phoneNumber));
               startActivity(intent);
           }
           else{
               Toast.makeText(this, "Phone call is not available at this movement", Toast.LENGTH_SHORT).show();
           }
        }));
    }

    private void playMediaTune(){
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    private void setUpChatRV(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> fOptions = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .build();
        adapter = new ChatAdapter(fOptions,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }
    private void getOrCreateChatRoomModel(){
        FirebaseUtil.getChatRoomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatRoomModel == null) {
                    //First time chatting...
                    chatRoomModel = new ChatRoomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserID(), otherUser.getUserID()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatRoomReference(chatroomId).set(chatRoomModel);
                }
            }
        });
    }

    private void sendMessageToUser(String message){
        chatRoomModel.setLastMessageTimeStamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderID(FirebaseUtil.currentUserID());
        chatRoomModel.setLastMessage(message);
        FirebaseUtil.getChatRoomReference(chatroomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(
                message,
                FirebaseUtil.currentUserID(),
                Timestamp.now()
        );
        FirebaseUtil.getChatroomMessageReference(chatroomId)
                .add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                       sendNotification(message);
                    }
                });
    }
    private void sendNotification(String message){
        FirebaseUtil.currentUserDetail().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationOBJ = new JSONObject();
                    assert currentUser != null;
                    notificationOBJ.put("title",currentUser.getUserName());
                    notificationOBJ.put("body",message);

                    JSONObject dataOBJ = new JSONObject();
                    dataOBJ.put("userId",currentUser.getUserID());

                    jsonObject.put("notification",notificationOBJ);
                    jsonObject.put("data",dataOBJ);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callAPI(jsonObject);

                } catch (Exception e){

                }
            }
        });
    }
    private void callAPI(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");

        //Retrieving API Key...
        Properties properties = new Properties();
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("apikeys.properties");
            properties.load(inputStream);
            String apiKey = properties.getProperty("API_KEY");
//            Log.e("APIFetching", "key " + apiKey);

            OkHttpClient client = new OkHttpClient();
            String url = "https://fcm.googleapis.com/fcm/send";
            RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization","Bearer "+apiKey)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                }
            });

        }catch (IOException e) {
            e.printStackTrace();
            Log.e("APIFetching","Error is "+ e.getMessage());
            // Handle file not found or other exceptions
        }

    }
}