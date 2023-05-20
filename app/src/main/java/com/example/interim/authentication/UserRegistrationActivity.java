package com.example.interim.authentication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.interim.PDFClass;
import com.example.interim.R;
import com.example.interim.models.JobSeekerUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserRegistrationActivity extends AppCompatActivity {
    private List<String> cities = new ArrayList<>();
    private List<String> nationalities = Arrays.asList("French", "British", "German", "Spanish", "Italian");

    FirebaseFirestore db;

    boolean resume = false;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    StorageReference storageRef;
    DatabaseReference databaseRefResume, databaseRefCover;
    Button uploadResume, uploadCoverLetter;

    TextView coverLetterDisplay, resumeDisplay;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private Calendar calendar;
    private TextInputEditText birthdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Button registerButton = findViewById(R.id.createAccount);
        TextInputEditText name = findViewById(R.id.textName);
        TextInputEditText firstName = findViewById(R.id.textFirstName);
        Spinner nationality = findViewById(R.id.nationalitySpinner);
        TextInputEditText email = findViewById(R.id.textMail);
        TextInputEditText phoneNumber = findViewById(R.id.textNumber);
        birthdate = findViewById(R.id.textBirthdate);
        TextInputLayout layoutPassword = findViewById(R.id.layoutPassword);
        TextInputEditText password = findViewById(R.id.textPassword);
        TextInputLayout layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
        TextInputEditText confirmPassword = findViewById(R.id.textConfirmPassword);
        TextInputEditText city = findViewById(R.id.textCity);

        calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateEditText();
            }
        };

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(UserRegistrationActivity.this, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pass = charSequence.toString();
                if(pass.length() >= 8){
                    Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                    Matcher matcher = pattern.matcher(pass);
                    boolean isPwdContainsSpeChar = matcher.find();
                    if(isPwdContainsSpeChar){
                        layoutPassword.setHelperText(getString(R.string.strongPass));
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

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String masterpass = password.getText().toString();
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

        registerButton.setOnClickListener(v -> {
            if (email.getText().toString().trim().equals("") || password.getText().toString().trim().equals("") || confirmPassword.getText().toString().trim().equals("")
                    || name.getText().toString().trim().equals("") || firstName.getText().toString().trim().equals("") || nationality.getSelectedItem().toString().trim().equals("")
                    || phoneNumber.getText().toString().trim().equals("") || birthdate.getText().toString().trim().equals("") || city.getText().toString().trim().equals("")) {
                Toast.makeText(UserRegistrationActivity.this, R.string.emptyFields, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.PHONE.matcher(phoneNumber.getText()).matches() || !Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                Toast.makeText(UserRegistrationActivity.this, R.string.incorrectFields, Toast.LENGTH_SHORT).show();
                return;
            }


            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            String confirmPasswordText = confirmPassword.getText().toString();

            if (passwordText.length() < 8 || confirmPasswordText.length() < 8){
                Toast.makeText(UserRegistrationActivity.this, R.string.minPass, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!checkPasswords(passwordText, confirmPasswordText)) {
                Toast.makeText(UserRegistrationActivity.this, R.string.passwordMismatch, Toast.LENGTH_SHORT).show();
                return;
            }
            ;

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
                                            Intent files = new Intent(UserRegistrationActivity.this, uploadFilesRegistrationUser.class);
                                            startActivity(files);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Error saving user data
                                            Toast.makeText(UserRegistrationActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        });
                            } else {
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
    private void updateDateEditText() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        birthdate.setText(simpleDateFormat.format(calendar.getTime()));
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

