package com.example.interim.offers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.interim.R;

public class ApplicationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        Intent intent = getIntent();
        String myValue = intent.getStringExtra("jobId");

        // Get the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new fragment_application_form();

        // Create a bundle and add the myValue variable to it
        Bundle bundle = new Bundle();
        bundle.putString("id", myValue);

        // Set the arguments of the fragment to the bundle
        fragment.setArguments(bundle);

        // Add the fragment to the layout
        fragmentTransaction.add(R.id.fragmentContainerView6, fragment);
        fragmentTransaction.commit();
    }
}