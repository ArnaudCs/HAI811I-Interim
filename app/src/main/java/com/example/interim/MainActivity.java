package com.example.interim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            System.out.println(currentUser);
            System.out.println("Login successfull : "+ mAuth.getCurrentUser());
            finish();
            Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(profile);
        }

        TextInputLayout layoutPassword = findViewById(R.id.layoutPassword);
        TextInputEditText textPassword = findViewById(R.id.textPassword);
        Button loginButton = findViewById(R.id.loginButton);
        TextInputEditText textEmail = findViewById(R.id.textUsername);
        TextView register = findViewById(R.id.goRegisterUser);
        TextView registerPro = findViewById(R.id.goRegisterCorp);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profile = new Intent(MainActivity.this, UserRegistrationActivity.class);
                startActivity(profile);
            }
        });

        registerPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profile = new Intent(MainActivity.this, SlideActivity.class);
                startActivity(profile);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(textEmail.getText().toString(), textPassword.getText().toString());
            }
        });

        textPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*String password = charSequence.toString();
                if(password.length() >= 8){
                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                    Matcher matcher = pattern.matcher(password);
                    boolean isPwdContainsSpeChar = matcher.find();
                    if(isPwdContainsSpeChar){
                        layoutPassword.setHelperText("Strong Password");
                        layoutPassword.setError("");
                    } else {
                        layoutPassword.setHelperText("");
                        layoutPassword.setError(getString(R.string.badPass));
                    }
                } else {
                    layoutPassword.setHelperText(getString(R.string.minPass));
                    layoutPassword.setError("");
                }
                utile pour la registration*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Connexion réussie
                        FirebaseUser user = mAuth.getCurrentUser();
                        System.out.println("Login successfull : "+ mAuth.getCurrentUser());
                        finish();
                        Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(profile);
                    } else {
                        System.out.println("Login error");
                    }
                });
    }
}