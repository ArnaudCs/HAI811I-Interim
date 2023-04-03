package com.example.interim;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.URI;
import java.math.BigDecimal;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PhoneValidation extends AppCompatActivity {

    private int randomNumber;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_validation);

        Button receiveCode = findViewById(R.id.receiveNewCodeBtn);
        Button validateMobile = findViewById(R.id.validateMobileBtn);
        TextInputEditText codeInput = findViewById(R.id.mobileConfirmationInput);
        receiveCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                randomNumber = random.nextInt();

            }
        });

        validateMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(codeInput.getText().toString()).equals("")){
                    if(randomNumber == Integer.valueOf(codeInput.getText().toString())){
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.phoneVerified), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrongOTP), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}