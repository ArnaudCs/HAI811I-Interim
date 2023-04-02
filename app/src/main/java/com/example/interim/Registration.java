package com.example.interim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        TextInputEditText textName = findViewById(R.id.text1Name);
        TextInputEditText textCompanyName = findViewById(R.id.textCompanyName);
        TextInputEditText textNationalNumber = findViewById(R.id.textNationalNumber);
        TextInputEditText textMail = findViewById(R.id.text1Mail);
        TextInputEditText textNumber = findViewById(R.id.textContact1Number);
        TextInputEditText textCompanyAdress = findViewById(R.id.textCompanyAdress);
        TextInputEditText textWebsite = findViewById(R.id.textWebsite);
        Button registerCompanyBtn = findViewById(R.id.createAccount);

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

        if (registerCompanyBtn != null) {
            registerCompanyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(textName.getText()) && !TextUtils.isEmpty(textCompanyName.getText()) && !TextUtils.isEmpty(textNationalNumber.getText())
                            && !TextUtils.isEmpty(textMail.getText()) && !TextUtils.isEmpty(textNumber.getText()) && !TextUtils.isEmpty(textCompanyAdress.getText()) && !TextUtils.isEmpty(textWebsite.getText())
                            && !TextUtils.isEmpty(textPassword.getText()) && !TextUtils.isEmpty(textConfirmPassword.getText())) {

                        Intent choosePlan = new Intent(Registration.this, PaymentAndSubscription.class);
                        startActivity(choosePlan);
                    } else {
                        Toast.makeText(Registration.this, R.string.missingFieldsErroToast, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

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
                String confirmpassword = charSequence.toString();
                if(confirmpassword.equals(masterpass)){
                    int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_700);
                    layoutConfirmPassword.setHelperTextColor(ColorStateList.valueOf(color));
                    layoutConfirmPassword.setHelperText(getString(R.string.passwordMatching));
                    layoutConfirmPassword.setError("");
                } else {
                    int color = ContextCompat.getColor(getApplicationContext(), R.color.primary_red);
                    layoutConfirmPassword.setHelperTextColor(ColorStateList.valueOf(color));
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