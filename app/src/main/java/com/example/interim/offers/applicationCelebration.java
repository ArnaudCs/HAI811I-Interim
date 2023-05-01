package com.example.interim.offers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.interim.AppActivity;
import com.example.interim.R;

public class applicationCelebration extends AppCompatActivity {

    Button sentApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_celebration);

        sentApplication = findViewById(R.id.sentApplication);

        sentApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent applicationHome = new Intent(applicationCelebration.this, AppActivity.class);
                startActivity(applicationHome);
                finish();
            }
        });
    }
}