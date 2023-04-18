package com.example.interim.authentication;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.interim.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class fragment_payment_adress extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String mUserEmail;
    private String mUserId;

    public fragment_payment_adress() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_payment_adress, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUserEmail = mAuth.getCurrentUser().getEmail();
        mUserId = mAuth.getCurrentUser().getUid();
        super.onViewCreated(view, savedInstanceState);
        Button useAdress = view.findViewById(R.id.useAdressBtn);
        Button backButtonAdressInfos = view.findViewById(R.id.backAdressBtn);
        TextInputEditText street = view.findViewById(R.id.textStreet);
        TextInputEditText appartment = view.findViewById(R.id.textApt);
        TextInputEditText country = view.findViewById(R.id.textCountry);
        TextInputEditText state = view.findViewById(R.id.textState);
        TextInputEditText postalCode = view.findViewById(R.id.textPostalCode);

        CheckBox defaultAdress = view.findViewById(R.id.defaultAdress);

        DocumentReference docRef = mFirestore.collection("Pros").document(mUserId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("adress")) {
                Map<String, Object> adress = (Map<String, Object>) documentSnapshot.getData().get("adress");
                if (adress != null) {
                    street.setText((String) adress.get("street"));
                    appartment.setText((String) adress.get("appartment"));
                    country.setText((String) adress.get("country"));
                    state.setText((String) adress.get("state"));
                    postalCode.setText((String) adress.get("code"));
                }
            }
        }).addOnFailureListener(e -> {
            // Error retrieving card information
            Log.w(TAG, "Error getting card information", e);
        });

        if (useAdress != null) {
            useAdress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(street.getText()) && !TextUtils.isEmpty(appartment.getText()) && !TextUtils.isEmpty(state.getText())
                            && !TextUtils.isEmpty(country.getText()) && !TextUtils.isEmpty(postalCode.getText())) {

                        fragment_order_summary fragmentOrderSummary = new fragment_order_summary();

                        // Remplacer le fragment actuel par le nouveau fragment
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.paymentContainer, fragmentOrderSummary)
                                .addToBackStack(null)
                                .commit();

                        if (defaultAdress.isChecked()) {
                            Map<String, Object> adress = new HashMap<>();
                            adress.put("street", street.getText().toString());
                            adress.put("appartment", appartment.getText().toString());
                            adress.put("country", country.getText().toString());
                            adress.put("state", state.getText().toString());
                            adress.put("code", postalCode.getText().toString());

                            DocumentReference docRef = mFirestore.collection("Pros").document(mUserId);
                            docRef.update("adress", adress);
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.missingFieldsErroToast, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        backButtonAdressInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });
    }
}