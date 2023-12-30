package com.example.imessage.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imessage.R;
import com.example.imessage.activities.ChatActivity;
import com.example.imessage.models.ChatRoomModel;
import com.example.imessage.models.UserModel;
import com.example.imessage.utils.AndroidUtil;
import com.example.imessage.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatAdapters extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatAdapters.ChatRoomModelViewHolder> {
    Context context;
    public RecentChatAdapters(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context=context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUser(model.getUserIDs())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentBy = model.getLastMessageSenderID().equals(FirebaseUtil.currentUserID());
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            UserModel otherUserModel = document.toObject(UserModel.class);

                            assert otherUserModel != null;
                            FirebaseUtil.getOtherProfilePicStrorageReffrence(otherUserModel.getUserID()).getDownloadUrl()
                                    .addOnCompleteListener(t -> {
                                        if(t.isSuccessful()){
                                            Uri uri = t.getResult();
                                            AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                                        }
                                    });

                            // Populate views and handle data as before
                            holder.userName.setText(otherUserModel.getUserName());
                            // ... other code related to setting data to views
                            if (lastMessageSentBy) {
                                holder.lastMessageTXT.setText("You : " + model.getLastMessage());
                            } else {
                                holder.lastMessageTXT.setText(model.getLastMessage());
                            }
                            holder.lastMessageTime.setText(
                                    FirebaseUtil.stringTime(model.getLastMessageTimeStamp())
                            );

                            // Set item click listener inside the null check for otherUserModel
                            holder.itemView.setOnClickListener(view -> {
                                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(30);
                                Intent intent = new Intent(context, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });
                        } else {
                            // Handle the situation when the document doesn't exist
                            // For example, show default values or error message
                            Log.e("RecentChatAdapter", "Document does not exist");
                        }
                    } else {
                        // Handle task failure
                        Log.e("RecentChatAdapter", "Error getting user document: " + task.getException());
                    }
                });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chats_row,parent,false);
        return new ChatRoomModelViewHolder(view);
    }

    static class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView userName,lastMessageTXT,lastMessageTime;
        ImageView profilePic,moveForward;
        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.userNameTV);
            lastMessageTXT=itemView.findViewById(R.id.lastMassageTXT);
            profilePic=itemView.findViewById(R.id.profileProfileView);
            moveForward=itemView.findViewById(R.id.moveForward);
            lastMessageTime=itemView.findViewById(R.id.lastMessageTime);
        }
    }

}
