package com.example.interim.discussion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.interim.AppActivity;
import com.example.interim.R;

public class CelebrationGroupCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celebration_group_creation);

        Button goToMessagesBtn = findViewById(R.id.goToMessagesBtn);

        goToMessagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToHome = new Intent(CelebrationGroupCreationActivity.this, AppActivity.class);
                startActivity(goToHome);
                finish();
            }
        });
    }
}