package com.example.interim;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class PaymentAndSubscription extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String mUserEmail;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Subscriptions")
                .document(mAuth.getCurrentUser().getUid())
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
                                Intent profile = new Intent(PaymentAndSubscription.this, AppActivity.class);
                                startActivity(profile);
                                finish();
                            }
                        }
                    } else {
                        // Error retrieving subscription info
                        Log.w(TAG, "Error getting subscription info", task.getException());
                    }
                });

        setContentView(R.layout.activity_payment_and_subscription);

        Button dailyBtn = findViewById(R.id.dailyPlanBtn);
        Button monthlyBtn = findViewById(R.id.monthlyPlanBtn);
        Button trimestrialBtn = findViewById(R.id.trimestrialPlanBtn);
        Button semestrialPlanBtn = findViewById(R.id.semestrialPlanBtn);
        Button yearlyPlanBtn = findViewById(R.id.yearlyPlanBtn);
        Button unlimitedPlanBtn = findViewById(R.id.unlimitedPlanBtn);

        dailyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planPrice = "10";
                Intent choosePlan = new Intent(PaymentAndSubscription.this, PaymentActivity.class);
                DataHolder.getInstance().setPlanPrice(planPrice);
                DataHolder.getInstance().setPlanName("Daily Subscription");
                startActivity(choosePlan);
            }
        });

        monthlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planPrice = "20";
                DataHolder.getInstance().setPlanPrice(planPrice);
                DataHolder.getInstance().setPlanName("Monthly Subscription");
                Intent choosePlan = new Intent(PaymentAndSubscription.this, PaymentActivity.class);
                startActivity(choosePlan);
            }
        });

        trimestrialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planPrice = "50";
                DataHolder.getInstance().setPlanPrice(planPrice);
                DataHolder.getInstance().setPlanName("Trimestrial Subscription");
                Intent choosePlan = new Intent(PaymentAndSubscription.this, PaymentActivity.class);
                startActivity(choosePlan);
            }
        });

        semestrialPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planPrice = "100";
                DataHolder.getInstance().setPlanPrice(planPrice);
                DataHolder.getInstance().setPlanName("Semestrial Subscription");
                Intent choosePlan = new Intent(PaymentAndSubscription.this, PaymentActivity.class);
                startActivity(choosePlan);
            }
        });

        yearlyPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planPrice = "190";
                DataHolder.getInstance().setPlanPrice(planPrice);
                DataHolder.getInstance().setPlanName("Yearly Subscription");
                Intent choosePlan = new Intent(PaymentAndSubscription.this, PaymentActivity.class);
                startActivity(choosePlan);
            }
        });

        unlimitedPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planPrice = "1000";
                DataHolder.getInstance().setPlanPrice(planPrice);
                DataHolder.getInstance().setPlanName("One Time Subscription");
                Intent choosePlan = new Intent(PaymentAndSubscription.this, PaymentActivity.class);
                startActivity(choosePlan);
            }
        });

    }
}