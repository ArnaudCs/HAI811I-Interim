package com.example.interim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



public class ProfileActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView firstNameTextView, nameTextView, phoneNumberTextView, emailTextView;
    Button decoBtn, editProfilBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        firstNameTextView = findViewById(R.id.textView7);
        nameTextView = findViewById(R.id.textView6);
        phoneNumberTextView = findViewById(R.id.textView8);
        emailTextView = findViewById(R.id.textView12);
        decoBtn = findViewById(R.id.decoBtn);
        editProfilBtn = findViewById(R.id.editProfileBtn);

        if(mAuth.getCurrentUser().getUid() != null){
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fragment_navbar_user navbarFrag = new fragment_navbar_user();

                        // Remplacer le fragment actuel par le nouveau fragment
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.navbarContainer, navbarFrag)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        fragment_navbar_entreprise navbarFrag = new fragment_navbar_entreprise();

                        // Remplacer le fragment actuel par le nouveau fragment
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.navbarContainer, navbarFrag)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
        }

        decoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                Intent profile = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(profile);
            }
        });

        if(mAuth.getCurrentUser() != null) {

            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String firstName = document.getString("firstName");
                        String name = document.getString("name");
                        String phoneNumber = document.getString("phoneNumber");
                        String email = mAuth.getCurrentUser().getEmail();

                        firstNameTextView.setText(firstName);
                        nameTextView.setText(name);
                        phoneNumberTextView.setText(phoneNumber);
                        emailTextView.setText(email);
                    }
                }
            });
        }
        else {
            finish();
            Intent profile = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(profile);
        }
    }
}
