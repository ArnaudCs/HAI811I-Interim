package com.example.interim;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class GroupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);
        getSupportFragmentManager().beginTransaction().replace(R.id.groupCreationContainer, new fragment_group_creation()).commit();
    }
}
