package com.example.interim.offers;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.interim.R;
import com.example.interim.models.Application;
import com.example.interim.models.Offer;
import com.example.interim.offers.applicationCard_ViewAdapter;
import com.example.interim.offers.applicationOverview_ViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class fragment_application_manager extends Fragment {

    Button pendingApplicationsBtn, acceptedApplications, rejectedApplications, backButtonApplications;
    String userId;
    String jobId;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    LinearLayout acceptedContainer, pendingContainer, rejectedContainer;
    ArrayList<Offer> pendingOffers;
    ArrayList<Offer> acceptedOffers;
    ArrayList<Offer> rejectedOffers;
    RecyclerView pendingDisplay, acceptedDisplay, rejectedDisplay;

    public fragment_application_manager() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_manager, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        } else {
            Log.d(TAG, "User is not logged in !");
            return null;
        }

        // Retrieve the argument value
        Bundle args = getArguments();
        if (args != null) {
            jobId = args.getString("id");
        }
        else {
            System.out.println("ID IS NULL");
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pendingApplicationsBtn = view.findViewById(R.id.pendingApplicationsBtn);
        acceptedApplications = view.findViewById(R.id.acceptedApplications);
        rejectedApplications = view.findViewById(R.id.rejectedApplications);

        pendingContainer = view.findViewById(R.id.pendingContainer);
        acceptedContainer = view.findViewById(R.id.acceptedContainer);
        rejectedContainer = view.findViewById(R.id.rejectedContainer);

        pendingDisplay = view.findViewById(R.id.pendingApplications);
        acceptedDisplay = view.findViewById(R.id.acceptedDisplay);
        rejectedDisplay = view.findViewById(R.id.rejectedDisplay);
        backButtonApplications = view.findViewById(R.id.backButtonApplications);


        if (userId != null) {
            db.collection("Applications")
                    .whereEqualTo("offerId", jobId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            ArrayList<Application> pendingApp = new ArrayList<>();
                            ArrayList<Application> acceptedApp = new ArrayList<>();
                            ArrayList<Application> rejectedApp = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                Application app = documentSnapshot.toObject(Application.class);
                                if(app.getStatus() == 0) {
                                    pendingApp.add(app);
                                } else if (app.getStatus() == 1) {
                                    rejectedApp.add(app);
                                }
                                else {
                                    acceptedApp.add(app);
                                }
                            }
                            pendingDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
                            pendingDisplay.setAdapter(new applicationOverview_ViewAdapter(getContext(), pendingApp));
                            rejectedDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
                            rejectedDisplay.setAdapter(new applicationOverview_ViewAdapter(getContext(), rejectedApp));
                            acceptedDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
                            acceptedDisplay.setAdapter(new applicationOverview_ViewAdapter(getContext(), acceptedApp));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error
                        }
                    });
        }

        backButtonApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        acceptedApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Changement des couleurs des boutons
                acceptedApplications.setBackground(getResources().getDrawable(R.drawable.redbutton));
                acceptedApplications.setTextColor(getResources().getColor(R.color.white));

                pendingApplicationsBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                rejectedApplications.setBackground(getResources().getDrawable(R.drawable.greybutton));
                pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.grey));
                rejectedApplications.setTextColor(getResources().getColor(R.color.grey));

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.VISIBLE);
                pendingContainer.setVisibility(View.GONE);
                rejectedContainer.setVisibility(View.GONE);
            }
        });

        pendingApplicationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Changement des couleurs des boutons
                pendingApplicationsBtn.setBackground(getResources().getDrawable(R.drawable.redbutton));
                pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.white));

                acceptedApplications.setBackground(getResources().getDrawable(R.drawable.greybutton));
                rejectedApplications.setBackground(getResources().getDrawable(R.drawable.greybutton));
                acceptedApplications.setTextColor(getResources().getColor(R.color.grey));
                rejectedApplications.setTextColor(getResources().getColor(R.color.grey));

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.GONE);
                pendingContainer.setVisibility(View.VISIBLE);
                rejectedContainer.setVisibility(View.GONE);
            }
        });

        rejectedApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Changement des couleurs des boutons
                rejectedApplications.setBackground(getResources().getDrawable(R.drawable.redbutton));
                rejectedApplications.setTextColor(getResources().getColor(R.color.white));

                pendingApplicationsBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                acceptedApplications.setBackground(getResources().getDrawable(R.drawable.greybutton));
                pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.grey));
                acceptedApplications.setTextColor(getResources().getColor(R.color.grey));

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.GONE);
                pendingContainer.setVisibility(View.GONE);
                rejectedContainer.setVisibility(View.VISIBLE);
            }
        });
    }

}