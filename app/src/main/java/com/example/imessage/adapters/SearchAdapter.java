package com.example.imessage.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imessage.R;
import com.example.imessage.activities.ChatActivity;
import com.example.imessage.models.UserModel;
import com.example.imessage.utils.AndroidUtil;
import com.example.imessage.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchAdapter extends FirestoreRecyclerAdapter<UserModel, SearchAdapter.UserModelViewHolder> {
    Context context;
    public SearchAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context=context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.userName.setText(model.getUserName());
        holder.phoneNumber.setText(model.getPhone());
        FirebaseUtil.getOtherProfilePicStrorageReffrence(model.getUserID()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                    }
                });
        if(model.getUserID().equals(FirebaseUtil.currentUserID())){
            holder.userName.setText(model.getUserName()+" (Me)");
            holder.moveForward.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(view -> {
            if(!model.getUserID().equals(FirebaseUtil.currentUserID())){
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(30);
                Intent intent = new Intent(context, ChatActivity.class);
                AndroidUtil.passUserModelAsIntent(intent,model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_row,parent,false);
        return new UserModelViewHolder(view);
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView userName,phoneNumber;
        ImageView profilePic,moveForward;
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.userNameTV);
            phoneNumber=itemView.findViewById(R.id.phoneNumberTV);
            profilePic=itemView.findViewById(R.id.profileProfileView);
            moveForward=itemView.findViewById(R.id.moveForward);
        }
    }

}
