package com.example.imessage.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.imessage.R;
import com.example.imessage.models.UserModel;
import com.example.imessage.utils.AndroidUtil;
import com.example.imessage.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

public class EditUserProfile extends AppCompatActivity {
    TextView cancelButton,saveButton;
    ImageView editProfilePic;
    EditText editUserName;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_edit_user_profile);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getApplicationContext(),selectedImageUri,editProfilePic);
                        }
                    }
                });

        cancelButton=findViewById(R.id.cancelTV);
        saveButton=findViewById(R.id.saveTV);
        editProfilePic=findViewById(R.id.editProfilePic);
        editUserName=findViewById(R.id.editUserName);
        getUserDetails();
        FirebaseUtil.getCurrentProfilePicStrorageReffrence().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getApplicationContext(),uri,editProfilePic);
                    }
                });

        //CLick Listeners...
        cancelButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            onBackPressed();
            // Add reverse transition animation
            overridePendingTransition(R.anim.slide_in_left_reverse, R.anim.slide_out_right_reverse);
        }));

        editProfilePic.setOnClickListener((view -> {
            vibrator.vibrate(30);
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(512)
                    .maxResultSize(512,512)
                    .createIntent(intent -> {
                        imagePickLauncher.launch(intent);
                        return null;
                    });
        }));

        saveButton.setOnClickListener((view -> {
            String updatedUserName = editUserName.getText().toString();
            if(updatedUserName.isEmpty() || updatedUserName.length()<3){
                editUserName.setError("Please Provide Correct UserName");
                return;
            }
            vibrator.vibrate(30);
            updateButtonClick(updatedUserName);
        }));

    }
    private void updateButtonClick(String newUserName){
        currentUserModel.setUserName(newUserName);
        if(selectedImageUri!=null){
            FirebaseUtil.getCurrentProfilePicStrorageReffrence().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updateDB();
                    });
        }
        else {
            updateDB();
        }
    }
    private void updateDB(){
        FirebaseUtil.currentUserDetail().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                    else{
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getUserDetails(){
        FirebaseUtil.currentUserDetail().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                currentUserModel = task.getResult().toObject(UserModel.class);
                if(currentUserModel !=null){
                    editUserName.setText(currentUserModel.getUserName());
                }
            }
        });
    }
}