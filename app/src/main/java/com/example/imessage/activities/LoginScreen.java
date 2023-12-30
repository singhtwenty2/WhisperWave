package com.example.imessage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.EditText;

import com.example.imessage.R;
import com.example.imessage.utils.ThemeUtils;
import com.hbb20.CountryCodePicker;

public class LoginScreen extends AppCompatActivity {

    Button genrateOTPButton;
    CountryCodePicker countryCodePicker;
    EditText inputPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        ThemeUtils.SetStatusBar(this,R.color.cupertino_grey);
        genrateOTPButton=findViewById(R.id.btn);
        countryCodePicker=findViewById(R.id.countryCodeID);
        inputPhoneNumber=findViewById(R.id.inputPhone);

        countryCodePicker.registerCarrierNumberEditText(inputPhoneNumber);


        genrateOTPButton.setOnClickListener(view -> {
            vibrator.vibrate(30);
            if(!countryCodePicker.isValidFullNumber()) {
                inputPhoneNumber.setError("Please Enter Phone Number In Proper Format");
                return;
            }
            Intent intent = new Intent(getApplicationContext(), OTPAuthentication.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}