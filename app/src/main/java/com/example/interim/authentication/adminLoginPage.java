package com.example.interim.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.interim.R;
import com.google.android.material.textfield.TextInputEditText;

public class adminLoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login_page);

        Button connexion = findViewById(R.id.loginButton);
        TextInputEditText username = findViewById(R.id.textUsername);
        TextInputEditText password = findViewById(R.id.textPassword);
        ImageView backToLogin = findViewById(R.id.backToLogin);

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText())){

                } else {
                    Toast.makeText(adminLoginPage.this, getResources().getString(R.string.missingFieldsErroToast), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}