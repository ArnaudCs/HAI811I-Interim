package com.example.interim.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interim.authentication.MainActivity;
import com.example.interim.authentication.PaymentAndSubscription;
import com.example.interim.authentication.PhoneValidation;
import com.example.interim.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class fragment_user_company extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String mUserEmail;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String mUserId;
    private String companyNameText;
    private String subPlanText;
    private String sirenNumText;
    private String contactNameText;
    private String phoneNumText;
    private String emailText;

    private ImageView profileCompanyPic;

    private Button settingsBtn;

    private LinearLayout editProfileCompany;


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
        Button deconnectionBtn = view.findViewById(R.id.decoBtn);

        settingsBtn = view.findViewById(R.id.settingsBtn);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settings);
            }
        });


        deconnectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                getActivity().finish();
                Intent profile = new Intent(getActivity(), MainActivity.class);
                startActivity(profile);
            }
        });

        editProfileCompany = view.findViewById(R.id.editProfileCompanyContainer);
        Button editprofileBtn = view.findViewById(R.id.editProfileCompanyBtn);

        editProfileCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfile = new Intent(getActivity(), com.example.interim.profile.editProfile.class);
                startActivity(editProfile);
            }
        });

        editprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfile = new Intent(getActivity(), editProfile.class);
                startActivity(editProfile);
            }
        });

        profileCompanyPic = view.findViewById(R.id.profileCompanyPic);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("uploads/" + mAuth.getCurrentUser().getUid());
        try {
            final File localFile = File.createTempFile("profilePic", "jpg");
            mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Uri fileUri = Uri.fromFile(localFile);
                    String imageUrl = fileUri.toString();
                    Picasso.with(getContext()).load(imageUrl).fit().centerCrop().into(profileCompanyPic);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error while retrieving picture", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}