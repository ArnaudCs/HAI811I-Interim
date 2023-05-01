package com.example.interim.authentication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.interim.Admin.AdminDashboard;
import com.example.interim.AppActivity;
import com.example.interim.R;
import com.example.interim.models.Pro;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class adminLoginPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login_page);

        Button connexion = findViewById(R.id.loginButton);
        TextInputEditText username = findViewById(R.id.textUsername);
        TextInputEditText password = findViewById(R.id.textPassword);
        ImageView backToLogin = findViewById(R.id.backToLogin);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                if (!documentSnapshot.getBoolean("verified") && documentSnapshot.getBoolean("admin")) {
                                    Toast.makeText(adminLoginPage.this, getResources().getString(R.string.finaliseAccount), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    // User is a regular user
                                    Intent profile = new Intent(adminLoginPage.this, AdminDashboard.class);
                                    startActivity(profile);
                                    finish();
                                }

                            } else {
                                db.collection("Pros").document(currentUser.getUid()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    if (!documentSnapshot.getBoolean("verified") && documentSnapshot.getBoolean("admin")) {
                                                        Toast.makeText(adminLoginPage.this, getResources().getString(R.string.finaliseAccount), Toast.LENGTH_SHORT).show();
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

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                if (!documentSnapshot.getBoolean("verified")) {
                                    Intent profile = new Intent(adminLoginPage.this, PhoneValidation.class);
                                    startActivity(profile);
                                    finish();
                                }
                                else {
                                    // User is a regular user
                                    Intent profile = new Intent(adminLoginPage.this, AppActivity.class);
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
                                                        Intent profile = new Intent(adminLoginPage.this, PhoneValidation.class);
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



        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText())){

                } else {
                    Toast.makeText(adminLoginPage.this, getResources().getString(R.string.missingFieldsErroToast), Toast.LENGTH_SHORT).show();
                }
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
                                            if (!documentSnapshot.getBoolean("verified") && documentSnapshot.getBoolean("admin")) {
                                                Toast.makeText(adminLoginPage.this, getResources().getString(R.string.finaliseAccount), Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                // User is a regular user
                                                Intent adminDash = new Intent(adminLoginPage.this, AdminDashboard.class);
                                                startActivity(adminDash);
                                                finish();
                                            }

                                        } else {
                                            db.collection("Pros").document(user.getUid()).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            if (documentSnapshot.exists()) {
                                                                if (!documentSnapshot.getBoolean("verified") && documentSnapshot.getBoolean("admin")) {
                                                                    Toast.makeText(adminLoginPage.this, getResources().getString(R.string.finaliseAccount), Toast.LENGTH_SHORT).show();
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
                                Intent profile = new Intent(adminLoginPage.this, AdminDashboard.class);
                                startActivity(profile);
                                finish();
                            } else {
                                Toast.makeText(adminLoginPage.this, getResources().getString(R.string.finaliseAccount), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // User does not have a subscription
                            Toast.makeText(adminLoginPage.this, getResources().getString(R.string.finaliseAccount), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error retrieving subscription info
                        Log.w(TAG, "Error getting subscription info", task.getException());
                    }
                });
    }
}