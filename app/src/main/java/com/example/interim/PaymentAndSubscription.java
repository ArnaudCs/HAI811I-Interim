package com.example.interim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PaymentAndSubscription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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