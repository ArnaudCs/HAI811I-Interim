package com.example.interim.offers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.interim.R;

public class MissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);
        Intent intent = getIntent();
        String myValue = intent.getStringExtra("id");

        Bundle args = new Bundle();
        args.putString("id", myValue);

        // Create the fragment and set the arguments
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment_mission_description fragment = new fragment_mission_description();
        fragment.setArguments(args);

        // Add the fragment to the container view
        transaction.add(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }

}