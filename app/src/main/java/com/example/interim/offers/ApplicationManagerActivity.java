package com.example.interim.offers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.interim.R;

public class ApplicationManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_manager);

        // Retrieve the extra from the intent
        Intent intent = getIntent();
        String extraValue = intent.getStringExtra("id");
        Bundle bundle = new Bundle();
        bundle.putString("id", extraValue);
        fragment_application_manager fragment = new fragment_application_manager();
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView8, fragment)
                .commit();
    }

}