package com.example.interim.authentication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.interim.R;
import com.example.interim.models.JobSeekerUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserRegistrationActivity extends AppCompatActivity {
    private List<String> cities = new ArrayList<>();
    private List<String> nationalities = Arrays.asList("French", "British", "German", "Spanish", "Italian");

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Button registerButton = findViewById(R.id.createAccount);
        TextInputEditText name = findViewById(R.id.textName);
        TextInputEditText firstName = findViewById(R.id.textFirstName);
        Spinner nationality =  findViewById(R.id.nationalitySpinner);
        TextInputEditText email = findViewById(R.id.textMail);
        TextInputEditText phoneNumber = findViewById(R.id.textNumber);
        TextInputEditText birthdate = findViewById(R.id.textBirthdate);
        TextInputEditText password = findViewById(R.id.textPassword);
        TextInputEditText confirmPassword = findViewById(R.id.textConfirmPassword);
        TextInputEditText city = findViewById(R.id.textCity);

        registerButton.setOnClickListener(v -> {
            if(email.getText().toString().trim().equals("") || password.getText().toString().trim().equals("") || confirmPassword.getText().toString().trim().equals("")
                    || name.getText().toString().trim().equals("")  || firstName.getText().toString().trim().equals("") || nationality.getSelectedItem().toString().trim().equals("")
                    || phoneNumber.getText().toString().trim().equals("") || birthdate.getText().toString().trim().equals("")  || city.getText().toString().trim().equals("")) {
                Toast.makeText(UserRegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            String confirmPasswordText = confirmPassword.getText().toString();

            if(!checkPasswords(passwordText, confirmPasswordText)) {
                Toast.makeText(UserRegistrationActivity.this, "Passwords does not match !", Toast.LENGTH_SHORT).show();
                return;
            };

            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                JobSeekerUser newUser = new JobSeekerUser(name.getText().toString(), firstName.getText().toString(), nationality.getSelectedItem().toString(), phoneNumber.getText().toString(), birthdate.getText().toString(), city.getText().toString());

                                // Save the user data to Firestore
                                db.collection("Users").document(user.getUid()).set(newUser)
                                        .addOnSuccessListener(documentReference -> {
                                            // User data saved successfully
                                            Toast.makeText(UserRegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                            finish();
                                            Intent phone = new Intent(UserRegistrationActivity.this, PhoneValidation.class);
                                            startActivity(phone);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Error saving user data
                                            Toast.makeText(UserRegistrationActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        });
                            }
                            else {
                                Toast.makeText(UserRegistrationActivity.this, "Failed during user registration", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // Create a new job seeker user object

        });


        new FetchCitiesTask().execute();

        // Set up the adapter for the nationality spinner
        ArrayAdapter<String> nationalityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nationalities);
        nationality.setAdapter(nationalityAdapter);

        // Set up the listeners for the password fields
        TextInputEditText passwordEditText = findViewById(R.id.textPassword);
        TextInputEditText passwordConfirmationEditText = findViewById(R.id.textConfirmPassword);
        passwordConfirmationEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (checkPasswords(passwordEditText.getText().toString(), passwordConfirmationEditText.getText().toString())) {
                    // Passwords match, do something
                } else {
                    // Passwords don't match, show error message
                    Toast.makeText(UserRegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

//        // Set up the listener for the CV file selector button
//        Button cvButton = findViewById(R.id.cvButton);
//        cvButton.setOnClickListener(v -> {
//            // Show file selector dialog
//        });
    }
    private boolean checkPasswords(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    private class FetchCitiesTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            // Create an HTTP client using OkHttp
            OkHttpClient client = new OkHttpClient();

            // Create an HTTP request to retrieve a list of French cities
            String url = "https://api.opencagedata.com/geocode/v1/json?q=france&no_annotations=1&key=";
            Request request = new Request.Builder().url(url).build();

            // Send the request and parse the response
            List<String> cities = new ArrayList<>();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray results = json.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        JSONObject components = result.getJSONObject("components");
                        String city = components.optString("city");
                        if (city != null && !city.isEmpty()) {
                            cities.add(city);
                        }
                    }
                } else {
                    Log.e("TAG", "Error: " + response.code() + " " + response.message());
                }
            } catch (IOException | JSONException e) {
                Log.e("TAG", "Error: " + e.getMessage());
            }
            return cities;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            cities = result;

            // Set up the adapter for the city autocomplete text input
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(UserRegistrationActivity.this, android.R.layout.simple_dropdown_item_1line, cities);
        }
    }


}