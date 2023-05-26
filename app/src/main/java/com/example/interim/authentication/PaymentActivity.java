package com.example.interim.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.res.Configuration;
import android.os.Bundle;

import com.example.interim.R;
import com.example.interim.authentication.fragment_order_summary;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            setTheme(R.style.ThemeDark_Interim);
        else
            setTheme(R.style.Theme_Interim);
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