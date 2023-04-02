package com.example.interim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Bundle extras = getIntent().getExtras();
        String planPrice = null;
        String planName = null;
        if (extras != null) {
            planPrice = extras.getString("planPrice");
            planName = extras.getString("planName");
        }

        Bundle planInfos = new Bundle();
        planInfos.putString("planPrice", planPrice);
        planInfos.putString("planName", planName);

        fragment_order_summary fragment = new fragment_order_summary();
        fragment.setArguments(planInfos);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(fragment, "order_summary").commit();
    }
}