package com.example.interim;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class fragment_user_company extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String mUserEmail;
    private String mUserId;
    private String companyNameText;
    private String subPlanText;
    private String sirenNumText;
    private String contactNameText;
    private String phoneNumText;
    private String emailText;


    public fragment_user_company() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(mAuth.getCurrentUser() == null){
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            this.getActivity().finish();
            return null;
        }
        db.collection("Pros").document(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean verified = documentSnapshot.getBoolean("verified");
                if(!verified) {
                    Intent mainActivity = new Intent(getActivity(), PhoneValidation.class);
                    startActivity(mainActivity);
                    getActivity().finish();
                }
                else {
                    db.collection("Subscriptions").document(mAuth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    subPlanText = documentSnapshot.getString("plan");
                                    Date endDate = documentSnapshot.getDate("endDate");
                                    Date startDate = documentSnapshot.getDate("startDate");
                                    boolean isUnlimited = subPlanText.contains("One Time");

                                    if (isUnlimited || (endDate != null && endDate.after(new Date())) || (startDate != null && startDate.after(new Date()))) {
                                        // User has an active subscription

                                    } else {
                                        // User does not have an active subscription
                                        Intent subscription = new Intent(getActivity(), PaymentAndSubscription.class);
                                        startActivity(subscription);
                                        getActivity().finish();
                                    }
                                } else {
                                    // User does not have a subscription
                                    Intent subscription = new Intent(getActivity(), PaymentAndSubscription.class);
                                    startActivity(subscription);
                                    getActivity().finish();
                                }
                            }
                        });
                }
            }
        });

        return inflater.inflate(R.layout.fragment_user_company, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Pros").document(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        companyNameText = documentSnapshot.getString("companyName");
                        sirenNumText = documentSnapshot.getString("nationalNumber");
                        contactNameText = documentSnapshot.getString("name");
                        phoneNumText = documentSnapshot.getString("phoneNumber");
                        emailText = documentSnapshot.getString("email");
                        TextView companyName = view.findViewById(R.id.companyName);
                        TextView subPlan = view.findViewById(R.id.subPlan);
                        TextView sirenNum = view.findViewById(R.id.sirenNum);
                        TextView contactName = view.findViewById(R.id.contactName);
                        TextView phoneNum = view.findViewById(R.id.phoneNum);
                        TextView email = view.findViewById(R.id.email);
                        System.out.println(emailText);
                        companyName.setText(companyNameText);
                        subPlan.setText(subPlanText);
                        sirenNum.setText(sirenNumText);
                        contactName.setText(contactNameText);
                        phoneNum.setText(phoneNumText);
                        email.setText(emailText);

                        db.collection("Subscriptions").document(mAuth.getCurrentUser().getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        subPlanText = documentSnapshot.getString("plan");
                                    }
                                }
                            });
                        }
                });

        super.onViewCreated(view, savedInstanceState);
        Button modifyProfil = view.findViewById(R.id.modifyProfileBtn);
        Button deconnectionBtn = view.findViewById(R.id.decoBtn);


        deconnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                getActivity().finish();
                Intent profile = new Intent(getActivity(), MainActivity.class);
                startActivity(profile);
            }
        });

        modifyProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}