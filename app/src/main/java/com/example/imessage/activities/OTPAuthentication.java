package com.example.imessage.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.imessage.R;
import com.example.imessage.utils.ThemeUtils;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OTPAuthentication extends AppCompatActivity {

    Button submitButton;
    EditText inputOTP;
    TextView resendOTPTV;
    LottieAnimationView progressBar;
    String phoneNumber;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Long timeout = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpauthentication);
        submitButton=findViewById(R.id.submitOTPBT);
        inputOTP=findViewById(R.id.inputOTP);
        resendOTPTV =findViewById(R.id.resendOTP);
        progressBar =findViewById(R.id.iosLoadAnim);


        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ThemeUtils.SetStatusBar(this,R.color.cupertino_grey);

        // Receiving phone from previous activity
        phoneNumber=getIntent().getStringExtra("phone");

        sendOTP(phoneNumber,false);

        submitButton.setOnClickListener(view -> {
            vibrator.vibrate(30);
            String userOTP = inputOTP.getText().toString();
            if (userOTP.isEmpty()) {
                inputOTP.setError("Please Enter OTP"); // Show an error message
                return; // Stop further execution of code inside the click listener
            }

            // Proceed with the verification process since the OTP is not empty
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, userOTP);
            updateUI(credential);
        });


        resendOTPTV.setOnClickListener((view -> sendOTP(phoneNumber,true)));


    }
    private void sendOTP(String phone_number,boolean isResend) {
        resendOTPTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions
                .newBuilder(mAuth)
                .setPhoneNumber(phone_number)
                .setTimeout(timeout, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        updateUI(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTPAuthentication.this, "Verification: Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode=s;
                        resendingToken=forceResendingToken;
                        Toast.makeText(OTPAuthentication.this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();
                        setInProgress(false);
                    }
                });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }
        else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateUI(PhoneAuthCredential phoneAuthCredential){
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                setInProgress(false);
                Intent intent = new Intent(getApplicationContext(), UserNameInput.class);
                intent.putExtra("phone",phoneNumber);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Failed To Verify The OTP", Toast.LENGTH_SHORT).show();
                setInProgress(false);
            }
        });

    }

    private void resendOTPTimer(){
        resendOTPTV.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                timeout--;
                runOnUiThread(() -> {
                    resendOTPTV.setText("Resend OTP in " + timeout + " Seconds");
                });
                if(timeout <= 0){
                    timeout = 60L;
                    runOnUiThread(() -> {
                        resendOTPTV.setEnabled(true);
                        timer.cancel();
                        resendOTPTV.setText("Resend OTP");
                    });
                }
            }
        }, 0, 1000);
    }

}