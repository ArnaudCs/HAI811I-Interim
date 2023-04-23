package com.example.interim.offers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interim.R;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class fragment_application_form extends Fragment {
    String offerId;

    public fragment_application_form() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_form, container, false);
        // Get the value of myValue from the arguments
        Bundle bundle = getArguments();
        assert bundle != null;
        offerId = bundle.getString("id");

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button backBtnApplication = view.findViewById(R.id.backBtnApplication);
        Button applyConfirm = view.findViewById(R.id.applyConfirm);
        Button useLastInfos = view.findViewById(R.id.useLastInfos);
        TextInputEditText textApplicantName = view.findViewById(R.id.textApplicantName);
        TextInputEditText textApplicantFirstName = view.findViewById(R.id.textApplicantFirstName);
        TextInputEditText textApplicantPhone = view.findViewById(R.id.textApplicantPhone);
        TextInputEditText textApplicantMail = view.findViewById(R.id.textApplicantMail);
        TextInputEditText textApplicantAdress = view.findViewById(R.id.textApplicantAdress);
        TextInputEditText textApplicantBirth = view.findViewById(R.id.textApplicantBirth);

        backBtnApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        useLastInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Firestore instance and reference to the current user's document
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference userRef = db.collection("Users").document(userId);

                // Retrieve the last application data from the user's document
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Object lastApplicationObj = document.get("lastApplication");
                                if (lastApplicationObj != null && lastApplicationObj instanceof Map) {
                                    Map<String, Object> lastApplication = (Map<String, Object>) lastApplicationObj;

                                    // Set the input fields with the last application data
                                    textApplicantName.setText(lastApplication.get("applicantName").toString());
                                    textApplicantFirstName.setText(lastApplication.get("applicantFirstName").toString());
                                    textApplicantPhone.setText(lastApplication.get("applicantPhone").toString());
                                    textApplicantMail.setText(lastApplication.get("applicantMail").toString());
                                    textApplicantAdress.setText(lastApplication.get("applicantAdress").toString());
                                    textApplicantBirth.setText(lastApplication.get("applicantBirth").toString());
                                }
                            }
                        } else {
                            // Handle any exceptions that may occur during the process
                        }
                    }
                });
            }
        });



        applyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input data
                String applicantName = textApplicantName.getText().toString();
                String applicantFirstName = textApplicantFirstName.getText().toString();
                String applicantPhone = textApplicantPhone.getText().toString();
                String applicantMail = textApplicantMail.getText().toString();
                String applicantAdress = textApplicantAdress.getText().toString();
                String applicantBirth = textApplicantBirth.getText().toString();

                // Get Firestore instance and reference to "Applications" collection
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference applicationsRef = db.collection("Applications");

                // Create new document in "Applications" collection
                DocumentReference newApplicationRef = applicationsRef.document();

                // Set data for new document
                Map<String, Object> data = new HashMap<>();
                data.put("applicantName", applicantName);
                data.put("applicantId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                data.put("applicantFirstName", applicantFirstName);
                data.put("applicantPhone", applicantPhone);
                data.put("applicantMail", applicantMail);
                data.put("applicantAdress", applicantAdress);
                data.put("applicantBirth", applicantBirth);
                data.put("offerId", offerId);

                newApplicationRef.set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Document has been added successfully
                                textApplicantName.setText("");
                                textApplicantFirstName.setText("");
                                textApplicantPhone.setText("");
                                textApplicantMail.setText("");
                                textApplicantAdress.setText("");
                                textApplicantBirth.setText("");

                                // Get reference to the user's document
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DocumentReference userRef = db.collection("Users").document(userId);

                                // Add the new application data to the user's document
                                Map<String, Object> applicationData = new HashMap<>();
                                applicationData.put("applicantName", applicantName);
                                applicationData.put("applicantFirstName", applicantFirstName);
                                applicationData.put("applicantPhone", applicantPhone);
                                applicationData.put("applicantMail", applicantMail);
                                applicationData.put("applicantAdress", applicantAdress);
                                applicationData.put("applicantBirth", applicantBirth);
                                applicationData.put("offerId", offerId);

                                userRef.update("lastApplication", applicationData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // User's document has been updated successfully
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Handle any exceptions that may occur during the process
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any exceptions that may occur during the process
                            }
                        });
            }
        });







    }

}