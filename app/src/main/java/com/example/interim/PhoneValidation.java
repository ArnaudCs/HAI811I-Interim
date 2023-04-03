package com.example.interim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URI;
import java.math.BigDecimal;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PhoneValidation extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Button receiveCodeBtn;
    private Button validateMobileBtn;
    private String mobileNumberInput;
    private EditText mobileConfirmationInput;
    private FirebaseAuth mAuth;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_validation);
        receiveCodeBtn = findViewById(R.id.receiveNewCodeBtn);
        validateMobileBtn = findViewById(R.id.validateMobileBtn);
        mobileConfirmationInput = findViewById(R.id.mobileConfirmationInput);
        mAuth = FirebaseAuth.getInstance();

        receiveCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "+33658529940";
                if(phoneNumber.isEmpty()) {
                    Toast.makeText(PhoneValidation.this, "Provide phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phoneNumber.length() < 10) {
                    Toast.makeText(PhoneValidation.this, "Provide valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send verification code to the entered phone number
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(PhoneValidation.this)
                                .setCallbacks(mCallbacks)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        validateMobileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mobileConfirmationInput.getText().toString().trim();
                if (code.isEmpty()) {
                    mobileConfirmationInput.setError("Code is required");
                    mobileConfirmationInput.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });

        // Initialize callbacks for phone authentication
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    mobileConfirmationInput.setText(code);
                    verifyCode(code);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneValidation.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId = verificationId;
                Toast.makeText(PhoneValidation.this, "Verification code sent", Toast.LENGTH_LONG).show();
            }
        };

    }


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Phone verification succeeded, sign in with the credential
                            FirebaseUser user = task.getResult().getUser();
                            // Update the "verified" boolean in the document for the user
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document(user.getEmail())
                                    .update("verified", true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.phoneVerified), Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Failed to update user document: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            // Phone verification failed
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrongOTP), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}