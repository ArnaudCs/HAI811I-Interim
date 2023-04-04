package com.example.interim;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class fragment_profil_user extends Fragment {

    public fragment_profil_user() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil_user, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseFirestore db;
        FirebaseAuth mAuth;
        TextView firstNameTextView, nameTextView, phoneNumberTextView, emailTextView;
        Button decoBtn, editProfilBtn;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        firstNameTextView = view.findViewById(R.id.textView7);
        nameTextView = view.findViewById(R.id.textView6);
        phoneNumberTextView = view.findViewById(R.id.textView8);
        emailTextView = view.findViewById(R.id.textView12);
        decoBtn = view.findViewById(R.id.decoBtn);
        editProfilBtn = view.findViewById(R.id.settingsBtn);

        if(mAuth.getCurrentUser() != null){
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                    } else {

                    }
                }
            });
        } else {
            getActivity().finish();
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
        }

        decoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                getActivity().finish();
                Intent profile = new Intent(getActivity(), MainActivity.class);
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
            getActivity().finish();
            Intent profile = new Intent(getActivity(), MainActivity.class);
            startActivity(profile);
        }
    }
}