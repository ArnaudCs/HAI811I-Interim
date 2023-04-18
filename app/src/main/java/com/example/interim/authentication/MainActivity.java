package com.example.interim.authentication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.interim.AppActivity;
import com.example.interim.R;
import com.example.interim.models.Pro;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                if (!documentSnapshot.getBoolean("verified")) {
                                    Intent profile = new Intent(MainActivity.this, PhoneValidation.class);
                                    startActivity(profile);
                                    finish();
                                }
                                else {
                                    // User is a regular user
                                    Intent profile = new Intent(MainActivity.this, AppActivity.class);
                                    startActivity(profile);
                                    finish();
                                }

                            } else {
                                db.collection("Pros").document(currentUser.getUid()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    if (!documentSnapshot.getBoolean("verified")) {
                                                        Intent profile = new Intent(MainActivity.this, PhoneValidation.class);
                                                        startActivity(profile);
                                                        finish();
                                                    }
                                                    else {
                                                        // User is a Pro
                                                        Pro pro = documentSnapshot.toObject(Pro.class);

                                                        isSubscribed(currentUser.getUid());
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }

        TextInputLayout layoutPassword = findViewById(R.id.layoutPassword);
        TextInputEditText textPassword = findViewById(R.id.textPassword);
        Button loginButton = findViewById(R.id.loginButton);
        TextInputEditText textEmail = findViewById(R.id.textUsername);
        TextView register = findViewById(R.id.goRegisterUser);
        TextView registerPro = findViewById(R.id.goRegisterCorp);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profile = new Intent(MainActivity.this, UserRegistrationActivity.class);
                startActivity(profile);
            }
        });

        registerPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profile = new Intent(MainActivity.this, SlideActivity.class);
                startActivity(profile);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(textEmail.getText()) && !TextUtils.isEmpty(textPassword.getText())){
                    signInUser(textEmail.getText().toString(), textPassword.getText().toString());
                }
            }
        });

        textPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Connexion réussie
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Users").document(user.getUid()).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            if (!documentSnapshot.getBoolean("verified")) {
                                                Intent profile = new Intent(MainActivity.this, AppActivity.class);
                                                startActivity(profile);
                                                finish();
                                            }
                                            else {
                                                // User is a regular user
                                                Intent profile = new Intent(MainActivity.this, AppActivity.class);
                                                startActivity(profile);
                                                finish();
                                            }

                                        } else {
                                            db.collection("Pros").document(user.getUid()).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            if (documentSnapshot.exists()) {
                                                                if (!documentSnapshot.getBoolean("verified")) {
                                                                    Intent profile = new Intent(MainActivity.this, PhoneValidation.class);
                                                                    startActivity(profile);
                                                                    finish();
                                                                }
                                                                else {
                                                                    // User is a Pro
                                                                    Pro pro = documentSnapshot.toObject(Pro.class);

                                                                    isSubscribed(user.getUid());
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                    } else {
                        System.out.println("Login error");
                    }
                });
    }


    private void isSubscribed(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Subscriptions")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String plan = document.getString("plan");
                            Date endDate = document.getDate("endDate");
                            Date startDate = document.getDate("startDate");
                            boolean isUnlimited = plan.contains("One Time");

                            if (isUnlimited || (endDate != null && endDate.after(new Date())) || (startDate != null && startDate.after(new Date()))) {
                                // User has an active subscription
                                Intent profile = new Intent(MainActivity.this, AppActivity.class);
                                startActivity(profile);
                                finish();
                            } else {
                                // User does not have an active subscription
                                Intent subscription = new Intent(MainActivity.this, PaymentAndSubscription.class);
                                startActivity(subscription);
                                finish();
                            }
                        } else {
                            // User does not have a subscription
                            Intent subscription = new Intent(MainActivity.this, PaymentAndSubscription.class);
                            startActivity(subscription);
                            finish();
                        }
                    } else {
                        // Error retrieving subscription info
                        Log.w(TAG, "Error getting subscription info", task.getException());
                    }
                });
    }







}