package com.example.interim;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.example.interim.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        db = FirebaseFirestore.getInstance();
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            // Get the user data from the input fields
            String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
            String firstName = ((EditText) findViewById(R.id.firstNameEditText)).getText().toString();
            String nationality = ((Spinner) findViewById(R.id.nationalitySpinner)).getSelectedItem().toString();
            String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
            String phoneNumber = ((EditText) findViewById(R.id.phoneEditText)).getText().toString();
            String birthdate = ((EditText) findViewById(R.id.birthdateEditText)).getText().toString();
            String city = ((AutoCompleteTextView) findViewById(R.id.cityAutoCompleteTextView)).getText().toString();
            String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

            // Create a new job seeker user object
            JobSeekerUser user = new JobSeekerUser(name, firstName, nationality, email, phoneNumber, birthdate, city, password);

            // Save the user data to Firestore
            db.collection("Users").document(email).set(user)
                    .addOnSuccessListener(documentReference -> {
                        // User data saved successfully
                        Toast.makeText(UserRegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    })
                    .addOnFailureListener(e -> {
                        // Error saving user data
                        Toast.makeText(UserRegistrationActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });


        new FetchCitiesTask().execute();

        // Set up the adapter for the nationality spinner
        ArrayAdapter<String> nationalityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nationalities);
        Spinner nationalitySpinner = findViewById(R.id.nationalitySpinner);
        nationalitySpinner.setAdapter(nationalityAdapter);

        // Set up the listeners for the password fields
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText passwordConfirmationEditText = findViewById(R.id.passwordConfirmationEditText);
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
            System.out.println(cities.toString());
            return cities;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            cities = result;

            // Set up the adapter for the city autocomplete text input
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(UserRegistrationActivity.this, android.R.layout.simple_dropdown_item_1line, cities);
            AutoCompleteTextView cityAutoCompleteTextView = findViewById(R.id.cityAutoCompleteTextView);
            cityAutoCompleteTextView.setAdapter(cityAdapter);
            cityAutoCompleteTextView.setThreshold(3);
        }
    }


}
