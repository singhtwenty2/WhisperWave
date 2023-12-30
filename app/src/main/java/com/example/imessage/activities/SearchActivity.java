package com.example.imessage.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imessage.R;
import com.example.imessage.adapters.SearchAdapter;
import com.example.imessage.models.UserModel;
import com.example.imessage.utils.FirebaseUtil;
import com.example.imessage.utils.ThemeUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {
    TextView backButton;
    EditText searchInput;
    ImageView imageButton;
    RecyclerView searchRecyclerView;
    SearchAdapter adapter;
    String searchTerm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_search);

        ThemeUtils.SetStatusBar(this,R.color.white);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        backButton=findViewById(R.id.backFromSearch);
        searchInput =findViewById(R.id.searchET);
        imageButton=findViewById(R.id.searchImageButton);
        searchRecyclerView=findViewById(R.id.searchRV);

        searchInput.requestFocus(); //Keyboard will be open as soon as the activity will be called...

        backButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            onBackPressed();
        }));

        imageButton.setOnClickListener((view -> {
            vibrator.vibrate(30);
            searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                searchInput.setError("Invalid Username");
                return;
            }
            setupRV(searchTerm);
        }));
    }
    private void setupRV(String userName){

        Query query = FirebaseUtil.getUsersCollection()
                .whereGreaterThanOrEqualTo("userName",searchTerm)
                .whereLessThanOrEqualTo("userName",searchTerm+'\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        adapter = new SearchAdapter(options,getApplicationContext());
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(adapter);
        adapter.startListening();

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