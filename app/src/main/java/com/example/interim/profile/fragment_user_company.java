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

import com.airbnb.lottie.LottieAnimationView;
import com.example.interim.Admin.ActivityStat;
import com.example.interim.authentication.MainActivity;
import com.example.interim.authentication.PaymentAndSubscription;
import com.example.interim.authentication.PhoneValidation;
import com.example.interim.R;
import com.example.interim.offers.FavoritesCompanyActivity;
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
    private String emailText, userId;
    boolean externalProfileView = false;

    String proId = "";

    private ImageView profileCompanyPic;

    private LottieAnimationView settingsBtn;

    private LinearLayout editProfileCompany, backProfileContainer, statsBtn, applySpontaneousContainer;

    private Button favoriteBtnCompany, backProfileBtn, editprofileBtn, deconnectionBtn, applySpontaneous;


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
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("recruiterId");
            externalProfileView = true;
        }
        if(mAuth.getCurrentUser() == null){
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            this.getActivity().finish();
            return null;
        }

//        if(!externalProfileView){
//            db.collection("Pros").document(mAuth.getCurrentUser().getUid()).get()
//                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            Boolean verified = documentSnapshot.getBoolean("verified");
//                            if(verified != null && !verified) {
//                                Intent mainActivity = new Intent(getActivity(), PhoneValidation.class);
//                                startActivity(mainActivity);
//                                getActivity().finish();
//                            }
//                            else {
//                                db.collection("Subscriptions").document(mAuth.getCurrentUser().getUid()).get()
//                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                if (documentSnapshot.exists()) {
//                                                    subPlanText = documentSnapshot.getString("plan");
//                                                    Date endDate = documentSnapshot.getDate("endDate");
//                                                    Date startDate = documentSnapshot.getDate("startDate");
//                                                    boolean isUnlimited = subPlanText.contains("One Time");
//
//                                                    if (isUnlimited || (endDate != null && endDate.after(new Date())) || (startDate != null && startDate.after(new Date()))) {
//                                                        // User has an active subscription
//
//                                                    } else {
//                                                        // User does not have an active subscription
//                                                        Intent subscription = new Intent(getActivity(), PaymentAndSubscription.class);
//                                                        startActivity(subscription);
//                                                        getActivity().finish();
//                                                    }
//                                                } else {
//                                                    // User does not have a subscription
//                                                    Intent subscription = new Intent(getActivity(), PaymentAndSubscription.class);
//                                                    startActivity(subscription);
//                                                    getActivity().finish();
//                                                }
//                                            }
//                                        });
//                            }
//                        }
//                    });
//
//        }

        return inflater.inflate(R.layout.fragment_user_company, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        favoriteBtnCompany = view.findViewById(R.id.favoriteBtnCompany);
        backProfileContainer = view.findViewById(R.id.backProfileContainer);
        backProfileBtn = view.findViewById(R.id.backProfileBtn);
        settingsBtn = view.findViewById(R.id.settingsBtn);
        editProfileCompany = view.findViewById(R.id.editProfileCompanyContainer);
        editprofileBtn = view.findViewById(R.id.editProfileCompanyBtn);
        deconnectionBtn = view.findViewById(R.id.decoBtn);
        statsBtn = view.findViewById(R.id.statsBtn);
        statsBtn.setVisibility(View.GONE);
        applySpontaneous = view.findViewById(R.id.applySpontaneous);
        applySpontaneousContainer = view.findViewById(R.id.applySpontaneousContainer);
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("recruiterId");
            externalProfileView = true;
        }
        if (userId != null && mAuth.getCurrentUser().getUid() != userId){
            externalProfileView = true;
            favoriteBtnCompany.setVisibility(View.GONE);
            backProfileContainer.setVisibility(View.VISIBLE);
            applySpontaneousContainer.setVisibility(View.VISIBLE);
            editProfileCompany.setVisibility(View.GONE);
            editprofileBtn.setVisibility(View.GONE);
            deconnectionBtn.setVisibility(View.GONE);
            settingsBtn.setVisibility(View.GONE);
        }

        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stats = new Intent(getActivity(), ActivityStat.class);
                startActivity(stats);
            }
        });

        proId = mAuth.getCurrentUser().getUid();

        if(externalProfileView){
            proId = userId;
        }

        db.collection("Pros").document(proId).get()
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

                        if(!externalProfileView) {
                            db.collection("Subscriptions").document(mAuth.getCurrentUser().getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                subPlanText = documentSnapshot.getString("plan");
                                                subPlan.setText(subPlanText);
                                                if((subPlanText.contains("Yearly") || subPlanText.contains("Unlimited"))) {
                                                    statsBtn.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });



        backProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                userId = null;
            }
        });

        applySpontaneous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + emailText));
                startActivity(emailIntent);
            }
        });

        favoriteBtnCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent favorites = new Intent(getActivity(), FavoritesCompanyActivity.class);
                String pro = "pro";
                favorites.putExtra("pro", pro);
                startActivity(favorites);
            }
        });


        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsBtn.playAnimation();
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
        if(externalProfileView){
            mStorageRef = FirebaseStorage.getInstance().getReference().child("uploads/" + userId);
        } else {
            mStorageRef = FirebaseStorage.getInstance().getReference().child("uploads/" + mAuth.getCurrentUser().getUid());
        }

        try {
            final File localFile = File.createTempFile("profilePic", "jpg");

            mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Uri fileUri = Uri.fromFile(localFile);
                    String imageUrl = fileUri.toString();
                    Picasso.get().load(imageUrl).fit().centerCrop().into(profileCompanyPic);
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