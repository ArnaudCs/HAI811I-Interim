package com.example.interim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        TextInputLayout layoutPassword = findViewById(R.id.layoutPassword);
        TextInputEditText textPassword = findViewById(R.id.textPassword);
        TextInputLayout layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
        TextInputEditText textConfirmPassword = findViewById(R.id.textConfirmPassword);

        TextInputLayout layoutContact2Name = findViewById(R.id.layoutContact2Name);
        TextInputLayout layoutContact2MailAdress = findViewById(R.id.layoutContact2MailAdress);
        TextInputLayout layoutContact2Number = findViewById(R.id.layoutContact2Number);
        TextInputLayout layoutService = findViewById(R.id.layoutService);
        TextInputLayout layoutSub = findViewById(R.id.layoutSubService);

        Switch addContact = findViewById(R.id.switchContact2);

        addContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    layoutContact2Name.setVisibility(View.VISIBLE);
                    layoutContact2Number.setVisibility(View.VISIBLE);
                    layoutContact2MailAdress.setVisibility(View.VISIBLE);
                    layoutService.setVisibility(View.VISIBLE);
                    layoutSub.setVisibility(View.VISIBLE);
                } else {
                    layoutContact2Name.setVisibility(View.GONE);
                    layoutContact2Number.setVisibility(View.GONE);
                    layoutContact2MailAdress.setVisibility(View.GONE);
                    layoutService.setVisibility(View.GONE);
                    layoutSub.setVisibility(View.GONE);
                }
            }
        });

        textPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString();
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
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        textConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String masterpass = textPassword.getText().toString();
                System.out.println(masterpass);
                String confirmpassword = charSequence.toString();
                System.out.println(confirmpassword);
                if(confirmpassword.equals(masterpass)){
                    ColorStateList helperTextColor = ColorStateList.valueOf(getResources().getColor(R.color.teal_200));
                    layoutConfirmPassword.setHelperTextColor(helperTextColor);
                    layoutConfirmPassword.setHelperText(getString(R.string.passwordMatching));
                    layoutConfirmPassword.setError("");
                } else {
                    ColorStateList helperTextColor = ColorStateList.valueOf(getResources().getColor(R.color.primary_red));
                    layoutConfirmPassword.setHelperTextColor(helperTextColor);
                    layoutConfirmPassword.setHelperText("");
                    layoutConfirmPassword.setError(getString(R.string.passwordMismatch));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}