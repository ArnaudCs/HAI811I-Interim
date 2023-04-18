package com.example.interim.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.interim.AppActivity;
import com.example.interim.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneValidation extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Button receiveCodeBtn;
    private Button validateMobileBtn;
    private String mobileNumberInput;
    private EditText mobileConfirmationInput;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private FirebaseUser currentUser;
    private boolean isPro;
    private String phoneNumber = "00000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_validation);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.getBoolean("verified")) {
                                Intent profile = new Intent(PhoneValidation.this, AppActivity.class);
                                startActivity(profile);
                                finish();
                            }
                            else {
                                phoneNumber = documentSnapshot.getString("phoneNumber");
                                if(phoneNumber == null || phoneNumber.isEmpty()) {
                                    Toast.makeText(PhoneValidation.this, "Provide phone number", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(phoneNumber.length() < 8) {
                                    Toast.makeText(PhoneValidation.this, "Provide valid phone number", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        else {
                            db.collection("Pros").document(currentUser.getUid()).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            if (documentSnapshot.getBoolean("verified")) {
                                                Intent profile = new Intent(PhoneValidation.this, PaymentAndSubscription.class);
                                                startActivity(profile);
                                                finish();
                                            }
                                            else {
                                                isPro = true;
                                                phoneNumber = documentSnapshot.getString("phoneNumber");
                                                if(phoneNumber == null || phoneNumber.isEmpty()) {
                                                    Toast.makeText(PhoneValidation.this, "Provide phone number", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                if(phoneNumber.length() < 8) {
                                                    Toast.makeText(PhoneValidation.this, "Provide valid phone number", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                            }
                                        }
                                    }
                                });
                        }
                    }
                });



        receiveCodeBtn = findViewById(R.id.receiveNewCodeBtn);
        validateMobileBtn = findViewById(R.id.validateMobileBtn);
        mobileConfirmationInput = findViewById(R.id.mobileConfirmationInput);


        receiveCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send verification code to the entered phone number
                PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+33"+phoneNumber)
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(isPro) {
            db.collection("Pros").document(currentUser.getUid()).update("verified", true);
            Intent profile = new Intent(PhoneValidation.this, PaymentAndSubscription.class);
            startActivity(profile);
            finish();
        }
        else {
            db.collection("Users").document(currentUser.getUid()).update("verified", true);
            Intent profile = new Intent(PhoneValidation.this, AppActivity.class);
            startActivity(profile);
            finish();
        }
    }

}