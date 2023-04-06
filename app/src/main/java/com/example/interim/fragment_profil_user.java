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
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public fragment_profil_user() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            this.getActivity().finish();
            return null;
        }
        return inflater.inflate(R.layout.fragment_profil_user, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView birthdate, savedOffers, nameTextView, phoneNumberTextView, emailTextView;
        Button decoBtn, editProfilBtn;


        nameTextView = view.findViewById(R.id.nameDisplay);
        phoneNumberTextView = view.findViewById(R.id.phoneNumber);
        emailTextView = view.findViewById(R.id.mailAdress);
        birthdate = view.findViewById(R.id.birthDate);
        decoBtn = view.findViewById(R.id.decoBtn);
        savedOffers = view.findViewById(R.id.savedOffers);
        editProfilBtn = view.findViewById(R.id.settingsBtn);


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
                        String birth = document.getString("birthdate");
                        String preferedCity = document.getString("city");

                        nameTextView.setText(firstName + " " + name);
                        phoneNumberTextView.setText(phoneNumber);
                        birthdate.setText(birth);
                        emailTextView.setText(email);
                        savedOffers.setText("0" + getResources().getString(R.string.savedOffersDisplay));
                        // Ajouter line ville favorite
                    }
                }
            });

        decoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent profile = new Intent(getActivity(), AppActivity.class);
                startActivity(profile);
            }
        });



    }
}