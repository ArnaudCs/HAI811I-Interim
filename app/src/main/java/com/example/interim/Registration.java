package com.example.interim;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.interim.models.Pro;


public class Registration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


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
                    // Get all input values
                    String name = textName.getText().toString();
                    String companyName = textCompanyName.getText().toString();
                    String nationalNumber = textNationalNumber.getText().toString();
                    String email = textMail.getText().toString();
                    String phoneNumber = textNumber.getText().toString();
                    String companyAddress = textCompanyAdress.getText().toString();
                    String website = textWebsite.getText().toString();
                    String password = textPassword.getText().toString();
                    String service = layoutService.getEditText().getText().toString();
                    String subService = layoutSub.getEditText().getText().toString();
                    String contact2Name = layoutContact2Name.getEditText().getText().toString();
                    String contact2Email = layoutContact2MailAdress.getEditText().getText().toString();
                    String contact2Phone = layoutContact2Number.getEditText().getText().toString();

                    // Check if any fields are empty
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(companyName) && !TextUtils.isEmpty(nationalNumber)
                            && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(companyAddress)
                            && !TextUtils.isEmpty(website) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(service)
                            && !TextUtils.isEmpty(subService)) {

                        // Create a new instance of the Pro model with all the input values
                        Pro pro = new Pro(name, companyName, nationalNumber, email, phoneNumber, companyAddress, website,
                                password, service, subService, contact2Name, contact2Email, contact2Phone);

                        // Add the Pro object to Firestore database
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Pros").document(email).set(pro)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + email);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });

                        // Create user account with Firebase Authentication
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            // Go to the next activity
                                            finish();
                                            Intent choosePlan = new Intent(Registration.this, PaymentAndSubscription.class);
                                            startActivity(choosePlan);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(Registration.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

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