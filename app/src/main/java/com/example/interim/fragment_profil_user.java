package com.example.interim;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class fragment_profil_user extends Fragment {

    LinearLayout editProfileUser;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String mUserId;

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

        LinearLayout editProfileUserContainer = view.findViewById(R.id.editProfileUserContainer);
        Button editProfileUserBtn = view.findViewById(R.id.editProfileUserBtn);

        editProfileUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfile = new Intent(getActivity(), editProfile.class);
                startActivity(editProfile);
            }
        });

        editProfileUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfile = new Intent(getActivity(), editProfile.class);
                startActivity(editProfile);
            }
        });

        ImageView profileUserPic = view.findViewById(R.id.profileUserPic);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("uploads/" + mAuth.getCurrentUser().getUid());
        try {
            final File localFile = File.createTempFile("profilePic", "jpg");
            mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Uri fileUri = Uri.fromFile(localFile);
                    String imageUrl = fileUri.toString();
                    Picasso.with(getContext()).load(imageUrl).fit().centerCrop().into(profileUserPic);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}