package com.example.interim.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.interim.AppActivity;
import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.authentication.PhoneValidation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminDashboard extends AppCompatActivity {

    Button statActivityBtn, usersManagement, logoutBtn, signaledActivityBtn;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        statActivityBtn = findViewById(R.id.statActivityBtn);
        usersManagement = findViewById(R.id.usersManagement);
        logoutBtn = findViewById(R.id.logoutBtn);
        signaledActivityBtn = findViewById(R.id.signaledActivityBtn);

        statActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stats = new Intent(AdminDashboard.this, ActivityStat.class);
                startActivity(stats);
            }
        });

        usersManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent manager = new Intent(AdminDashboard.this, AdminManageUser.class);
                startActivity(manager);
            }
        });

        signaledActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signaledOffers = new Intent(AdminDashboard.this, SignaledOffersActivity.class);
                startActivity(signaledOffers);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent profile = new Intent(AdminDashboard.this, adminLoginPage.class);
                startActivity(profile);
                finish();
            }
        });
    }
}