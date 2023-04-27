package com.example.interim.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.interim.PDFClass;
import com.example.interim.R;
import com.example.interim.models.JobSeekerUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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

    boolean resume = false;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    StorageReference storageRef;
    DatabaseReference databaseRefResume, databaseRefCover;
    Button uploadResume, uploadCoverLetter;

    TextView coverLetterDisplay, resumeDisplay;
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

        uploadCoverLetter = findViewById(R.id.uploadCoverLetter);
        uploadResume = findViewById(R.id.uploadResume);

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRefResume = FirebaseDatabase.getInstance().getReference("uploadResume");
        databaseRefCover = FirebaseDatabase.getInstance().getReference("uploadCover");

        coverLetterDisplay = findViewById(R.id.coverLetterDisplay);
        resumeDisplay = findViewById(R.id.resumeDisplay);

        uploadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();
                resume = true;
            }
        });

        uploadCoverLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();
                resume = false;
            }
        });

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
                                            StorageReference storageRefResume = FirebaseStorage.getInstance().getReference().child("Resume/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_Resume.pdf");
                                            storageRefResume.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                                @Override
                                                public void onSuccess(StorageMetadata storageMetadata) {
                                                    String fileName = storageMetadata.getName();
                                                    resumeDisplay.setText(getApplicationContext().getResources().getString(R.string.resumeInDatabase));
                                                    // Utiliser le nom du fichier récupéré pour afficher le nom du fichier sur votre interface utilisateur
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    resumeDisplay.setText(getApplicationContext().getResources().getString(R.string.resumeNotFound));
                                                }
                                            });

                                            StorageReference storageRefCover = FirebaseStorage.getInstance().getReference().child("CoverLetters/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_CoverLetter.pdf");
                                            storageRefCover.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                                @Override
                                                public void onSuccess(StorageMetadata storageMetadata) {
                                                    String fileName = storageMetadata.getName();
                                                    coverLetterDisplay.setText(getApplicationContext().getResources().getString(R.string.coverLetterinDatabase));
                                                    // Utiliser le nom du fichier récupéré pour afficher le nom du fichier sur votre interface utilisateur
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    coverLetterDisplay.setText(getApplicationContext().getResources().getString(R.string.coverNotFound));
                                                }
                                            });
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

    private void selectFiles() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getApplicationContext().getResources().getString(R.string.selectPDF)), 12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==12 && resultCode==RESULT_OK && data!=null &&data.getData()!=null){
            if(resume) {
                UploadFiles(data.getData());
            } else {
                UploadFilesCover(data.getData());
            }
        }
    }

    private void UploadFiles(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setTitle("Uploading ...");
        progressDialog.show();

        StorageReference reference = storageRef.child("Resume/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_Resume.pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri url = uriTask.getResult();

                        String fileName = "Resume_" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                        PDFClass pdf = new PDFClass(fileName, url.toString());

                        databaseRefResume.child(databaseRefResume.push().getKey()).setValue(pdf);

                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fileUploaded), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        resumeDisplay.setText(getApplicationContext().getResources().getString(R.string.resumeInDatabase));
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage(" Uploaded : " + (int)progress+"%");
                    }
                });
    }

    private void UploadFilesCover(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setTitle("Uploading ...");
        progressDialog.show();

        StorageReference reference = storageRef.child("CoverLetters/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_CoverLetter.pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri url = uriTask.getResult();

                        String fileName = "CoverLetter_" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                        PDFClass pdf = new PDFClass(fileName, url.toString());

                        databaseRefCover.child(databaseRefCover.push().getKey()).setValue(pdf);

                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.fileUploaded), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        coverLetterDisplay.setText(getApplicationContext().getResources().getString(R.string.coverLetterinDatabase));
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage(" Uploaded : " + (int)progress+"%");
                    }
                });
    }


}
