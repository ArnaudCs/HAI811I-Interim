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
        TextView birthdate, savedOffers, nameTextView, phoneNumberTextView, emailTextView;
        Button decoBtn, editProfilBtn;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nameTextView = view.findViewById(R.id.nameDisplay);
        phoneNumberTextView = view.findViewById(R.id.phoneNumber);
        emailTextView = view.findViewById(R.id.mailAdress);
        birthdate = view.findViewById(R.id.birthDate);
        decoBtn = view.findViewById(R.id.decoBtn);
        savedOffers = view.findViewById(R.id.savedOffers);
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
                        String birth = document.getString("birthDate");

                        nameTextView.setText(firstName + name);
                        phoneNumberTextView.setText(phoneNumber);
                        birthdate.setText(birth);
                        emailTextView.setText(email);
                        savedOffers.setText("0" + getResources().getString(R.string.savedOffersDisplay));
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