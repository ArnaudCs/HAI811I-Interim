package com.example.interim.Admin;

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

import com.example.interim.AppActivity;
import com.example.interim.R;
import com.example.interim.authentication.PhoneValidation;
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

    Boolean isAdmin;

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
                    signInUser(username.getText().toString(), password.getText().toString());
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
                                            if (!documentSnapshot.getBoolean("verified")) {
                                                Toast.makeText(adminLoginPage.this, getResources().getString(R.string.finaliseAccount), Toast.LENGTH_SHORT).show();
                                            }
                                            else if (documentSnapshot.getBoolean("admin")) {
                                                // User is a regular user
                                                Intent adminDash = new Intent(adminLoginPage.this, AdminDashboard.class);
                                                startActivity(adminDash);
                                                finish();
                                            } else if (!documentSnapshot.getBoolean("admin")) {
                                                // User is a regular user
                                                Toast.makeText(adminLoginPage.this, getResources().getString(R.string.yourNotAdmin), Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            db.collection("Pros").document(user.getUid()).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            if (documentSnapshot.exists()) {
                                                                if (!documentSnapshot.getBoolean("verified")) {
                                                                    Toast.makeText(adminLoginPage.this, getResources().getString(R.string.finaliseAccount), Toast.LENGTH_SHORT).show();
                                                                }
                                                                else {
                                                                    // User is a Pro
                                                                    Pro pro = documentSnapshot.toObject(Pro.class);
                                                                    if(documentSnapshot.getBoolean("admin")) {
                                                                        Intent adminDash = new Intent(adminLoginPage.this, AdminDashboard.class);
                                                                        startActivity(adminDash);
                                                                        finish();
                                                                    }
                                                                    else {
                                                                        Toast.makeText(adminLoginPage.this, getResources().getString(R.string.yourNotAdmin), Toast.LENGTH_SHORT).show();
                                                                    }

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
}