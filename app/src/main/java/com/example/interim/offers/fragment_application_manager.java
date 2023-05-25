package com.example.interim.offers;

import static android.content.ContentValues.TAG;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.interim.R;
import com.example.interim.models.Application;
import com.example.interim.models.Notification;
import com.example.interim.models.Offer;
import com.example.interim.offers.applicationCard_ViewAdapter;
import com.example.interim.offers.applicationOverview_ViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

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
    private int nightModeFlags;

    private Handler mHandler;
    private Runnable mRunnable;
    private boolean refreshing = false;
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
        startRefreshing();

        nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

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
                                app.setId(documentSnapshot.getId());
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
                            pendingDisplay.setAdapter(new applicationOverview_ViewAdapter(getContext(), pendingApp, getActivity()));
                            rejectedDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
                            rejectedDisplay.setAdapter(new applicationOverview_ViewAdapter(getContext(), rejectedApp, getActivity()));
                            acceptedDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
                            acceptedDisplay.setAdapter(new applicationOverview_ViewAdapter(getContext(), acceptedApp, getActivity()));
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
                acceptedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.primary_red));
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    acceptedApplications.setTextColor(getResources().getColor(R.color.almost_black));
                    pendingApplicationsBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    rejectedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.white_less_opaque));
                    rejectedApplications.setTextColor(getResources().getColor(R.color.white_less_opaque));
                }
                else {
                    acceptedApplications.setTextColor(getResources().getColor(R.color.white));
                    rejectedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    pendingApplicationsBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.dark_grey));
                    rejectedApplications.setTextColor(getResources().getColor(R.color.dark_grey));
                }

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
                pendingApplicationsBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.primary_red));
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.almost_black));
                    acceptedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    rejectedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    acceptedApplications.setTextColor(getResources().getColor(R.color.white_less_opaque));
                    rejectedApplications.setTextColor(getResources().getColor(R.color.white_less_opaque));
                }
                else {
                    pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.white));
                    acceptedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    rejectedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    acceptedApplications.setTextColor(getResources().getColor(R.color.dark_grey));
                    rejectedApplications.setTextColor(getResources().getColor(R.color.dark_grey));
                }

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
                rejectedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.primary_red));
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    rejectedApplications.setTextColor(getResources().getColor(R.color.almost_black));
                    pendingApplicationsBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    acceptedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.white_less_opaque));
                    acceptedApplications.setTextColor(getResources().getColor(R.color.white_less_opaque));
                }
                else {
                    rejectedApplications.setTextColor(getResources().getColor(R.color.white));
                    pendingApplicationsBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    acceptedApplications.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.dark_grey));
                    acceptedApplications.setTextColor(getResources().getColor(R.color.dark_grey));
                }

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.GONE);
                pendingContainer.setVisibility(View.GONE);
                rejectedContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startRefreshing() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
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
                                        app.setId(documentSnapshot.getId());
                                        int status = app.getStatus();
                                        if (status == 0) {
                                            pendingApp.add(app);
                                        } else if (status == 1) {
                                            rejectedApp.add(app);
                                        } else if (status == 2) {
                                            acceptedApp.add(app);
                                        }
                                    }
                                    pendingDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
                                    pendingDisplay.setAdapter(new applicationOverview_ViewAdapter(getContext(), pendingApp, getActivity()));
                                    rejectedDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
                                    rejectedDisplay.setAdapter(new applicationOverview_ViewAdapter(getContext(), rejectedApp, getActivity()));
                                    acceptedDisplay.setLayoutManager(new LinearLayoutManager(getContext()));
                                    acceptedDisplay.setAdapter(new applicationOverview_ViewAdapter(getContext(), acceptedApp, getActivity()));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                }
                            });
                }

                // Programmer la prochaine exécution du Runnable après 2 secondes
                mHandler.postDelayed(this, 2000);
            }
        };

        refreshing = true;
        mHandler.post(mRunnable);
    }



    @Override
    public void onPause() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            super.onPause();
            refreshing = false;
            System.out.println("Arrêt du refresh des conversations");
            mHandler.removeCallbacks(mRunnable);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            super.onResume();
            refreshing = true;
            System.out.println("Reprise du refresh des conversations");
            mHandler.post(mRunnable);
        }
        super.onResume();
    }

}