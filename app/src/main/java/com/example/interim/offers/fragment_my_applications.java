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
import android.widget.Toast;

import com.example.interim.R;
import com.example.interim.models.Blocked;
import com.example.interim.models.Offer;
import com.example.interim.models.Signal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class fragment_my_applications extends Fragment {
    String userId;
    Boolean isPro;
    RecyclerView recyclerViewAccepted;
    RecyclerView recyclerViewRejected;
    RecyclerView recyclerViewPending;

    private Runnable mRunnable;
    private boolean refreshing = false;

    private Handler mHandler;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    LinearLayout acceptedContainer, pendingContainer, rejectedContainer;
    ArrayList<Offer> pendingOffers;
    ArrayList<Offer> acceptedOffers;
    ArrayList<Offer> rejectedOffers;

    Button acceptedBtn, pendingBtn, rejectedBtn;
    private int nightModeFlags;


    public fragment_my_applications() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        isPro = false;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }
        else {
            Log.d(TAG, "User is not logged in !");
            return null;
        }

        return inflater.inflate(R.layout.fragment_my_applications, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button backButtonApplications = view.findViewById(R.id.backButtonApplications);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        recyclerViewAccepted = view.findViewById(R.id.acceptedDisplay);
        recyclerViewPending = view.findViewById(R.id.pendingDisplay);
        recyclerViewRejected = view.findViewById(R.id.rejectedDisplay);

        acceptedContainer = view.findViewById(R.id.acceptedContainer);
        pendingContainer = view.findViewById(R.id.pendingContainer);
        rejectedContainer = view.findViewById(R.id.rejectedContainer);

        HashMap<Offer, Integer> pendingOffers = new HashMap<>();
        HashMap<Offer, Integer> acceptedOffers = new HashMap<>();
        HashMap<Offer, Integer> rejectedOffers = new HashMap<>();
        List<String> pendingApplicationsIds = new ArrayList<>();
        List<String> rejectedApplicationsIds = new ArrayList<>();
        List<String> acceptedApplicationsIds = new ArrayList<>();

        if(userId != null) {
            db.collection("Applications")
                    .whereEqualTo("applicantId", userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            ArrayList<String> pendingOfferIds = new ArrayList<>();
                            ArrayList<String> acceptedOfferIds = new ArrayList<>();
                            ArrayList<String> rejectedOfferIds = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                if (documentSnapshot.get("status",Integer.class) == 0) {
                                    pendingOfferIds.add(documentSnapshot.getString("offerId"));
                                    pendingApplicationsIds.add(documentSnapshot.getId());
                                } else if (documentSnapshot.get("status",Integer.class) == 1) {
                                    rejectedOfferIds.add(documentSnapshot.getString("offerId"));
                                    rejectedApplicationsIds.add(documentSnapshot.getId());
                                }
                                else {
                                    acceptedOfferIds.add(documentSnapshot.getString("offerId"));
                                    acceptedApplicationsIds.add(documentSnapshot.getId());
                                }
                            }
                            if(pendingOfferIds.size() > 0) {
                                db.collection("Offers")
                                        .whereIn(FieldPath.documentId(), pendingOfferIds)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                                    Offer offer = documentSnapshot.toObject(Offer.class);
                                                    pendingOffers.put(offer, 0);
                                                }
                                                recyclerViewPending.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerViewPending.setAdapter(new applicationCard_ViewAdapter(getContext(), pendingOffers, pendingApplicationsIds));
                                            }
                                        });
                            }
                            if (acceptedOfferIds.size() > 0) {
                                db.collection("Offers")
                                        .whereIn(FieldPath.documentId(), acceptedOfferIds)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                                    Offer offer = documentSnapshot.toObject(Offer.class);
                                                    acceptedOffers.put(offer, 2);
                                                }
                                                recyclerViewAccepted.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerViewAccepted.setAdapter(new applicationCard_ViewAdapter(getContext(), acceptedOffers, acceptedApplicationsIds));
                                            }
                                        });
                            }
                            if(rejectedOfferIds.size() > 0) {
                                db.collection("Offers")
                                        .whereIn(FieldPath.documentId(), rejectedOfferIds)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {

                                                    Offer offer = documentSnapshot.toObject(Offer.class);
                                                    rejectedOffers.put(offer,1);
                                                }
                                                recyclerViewRejected.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerViewRejected.setAdapter(new applicationCard_ViewAdapter(getContext(), rejectedOffers, rejectedApplicationsIds));
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error
                        }
                    });
        }

        startRefreshing();


        backButtonApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        acceptedBtn = view.findViewById(R.id.acceptedBtn);
        pendingBtn = view.findViewById(R.id.pendingBtn);
        rejectedBtn = view.findViewById(R.id.rejectedBtn);

        acceptedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Changement des couleurs des boutons
                acceptedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.primary_red));
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    acceptedBtn.setTextColor(getResources().getColor(R.color.almost_black));
                    pendingBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    rejectedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    pendingBtn.setTextColor(getResources().getColor(R.color.white_less_opaque));
                    rejectedBtn.setTextColor(getResources().getColor(R.color.white_less_opaque));
                }
                else {
                    acceptedBtn.setTextColor(getResources().getColor(R.color.white));
                    pendingBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    rejectedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    pendingBtn.setTextColor(getResources().getColor(R.color.dark_grey));
                    rejectedBtn.setTextColor(getResources().getColor(R.color.dark_grey));
                }

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.VISIBLE);
                pendingContainer.setVisibility(View.GONE);
                rejectedContainer.setVisibility(View.GONE);
            }
        });

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Changement des couleurs des boutons
                pendingBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.primary_red));
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    pendingBtn.setTextColor(getResources().getColor(R.color.almost_black));
                    acceptedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    rejectedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    acceptedBtn.setTextColor(getResources().getColor(R.color.white_less_opaque));
                    rejectedBtn.setTextColor(getResources().getColor(R.color.white_less_opaque));
                }
                else {
                    pendingBtn.setTextColor(getResources().getColor(R.color.white));
                    acceptedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    rejectedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    acceptedBtn.setTextColor(getResources().getColor(R.color.dark_grey));
                    rejectedBtn.setTextColor(getResources().getColor(R.color.dark_grey));
                }

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.GONE);
                pendingContainer.setVisibility(View.VISIBLE);
                rejectedContainer.setVisibility(View.GONE);
            }
        });

        rejectedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Changement des couleurs des boutons
                rejectedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.primary_red));
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    rejectedBtn.setTextColor(getResources().getColor(R.color.almost_black));
                    acceptedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    pendingBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.dark_darkgrey));
                    acceptedBtn.setTextColor(getResources().getColor(R.color.white_less_opaque));
                    pendingBtn.setTextColor(getResources().getColor(R.color.white_less_opaque));
                }
                else {
                    rejectedBtn.setTextColor(getResources().getColor(R.color.white));
                    acceptedBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    pendingBtn.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(),R.color.box));
                    acceptedBtn.setTextColor(getResources().getColor(R.color.dark_grey));
                    pendingBtn.setTextColor(getResources().getColor(R.color.dark_grey));
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
                            .whereEqualTo("applicantId", userId)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot querySnapshot) {
                                    ArrayList<String> pendingOfferIds = new ArrayList<>();
                                    ArrayList<String> acceptedOfferIds = new ArrayList<>();
                                    ArrayList<String> rejectedOfferIds = new ArrayList<>();
                                    HashMap<Offer, Integer> pendingOffers = new HashMap<>();
                                    HashMap<Offer, Integer> acceptedOffers = new HashMap<>();
                                    HashMap<Offer, Integer> rejectedOffers = new HashMap<>();
                                    List<String> pendingApplicationsIds = new ArrayList<>();
                                    List<String> rejectedApplicationsIds = new ArrayList<>();
                                    List<String> acceptedApplicationsIds = new ArrayList<>();
                                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                        if (documentSnapshot.get("status",Integer.class) == 0) {
                                            pendingOfferIds.add(documentSnapshot.getString("offerId"));
                                            pendingApplicationsIds.add(documentSnapshot.getId());
                                        } else if (documentSnapshot.get("status",Integer.class) == 1) {
                                            rejectedOfferIds.add(documentSnapshot.getString("offerId"));
                                            rejectedApplicationsIds.add(documentSnapshot.getId());
                                        }
                                        else {
                                            acceptedOfferIds.add(documentSnapshot.getString("offerId"));
                                            acceptedApplicationsIds.add(documentSnapshot.getId());
                                        }
                                    }
                                    if(pendingOfferIds.size() > 0) {
                                        db.collection("Offers")
                                                .whereIn(FieldPath.documentId(), pendingOfferIds)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                                            Offer offer = documentSnapshot.toObject(Offer.class);
                                                            pendingOffers.put(offer, 0);
                                                        }
                                                        recyclerViewPending.setLayoutManager(new LinearLayoutManager(getContext()));
                                                        recyclerViewPending.setAdapter(new applicationCard_ViewAdapter(getContext(), pendingOffers, pendingApplicationsIds));
                                                    }
                                                });
                                    }
                                    if (acceptedOfferIds.size() > 0) {
                                        db.collection("Offers")
                                                .whereIn(FieldPath.documentId(), acceptedOfferIds)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                                            Offer offer = documentSnapshot.toObject(Offer.class);
                                                            acceptedOffers.put(offer, 2);
                                                        }
                                                        recyclerViewAccepted.setLayoutManager(new LinearLayoutManager(getContext()));
                                                        recyclerViewAccepted.setAdapter(new applicationCard_ViewAdapter(getContext(), acceptedOffers, acceptedApplicationsIds));
                                                    }
                                                });
                                    }
                                    if(rejectedOfferIds.size() > 0) {
                                        db.collection("Offers")
                                                .whereIn(FieldPath.documentId(), rejectedOfferIds)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {

                                                            Offer offer = documentSnapshot.toObject(Offer.class);
                                                            rejectedOffers.put(offer,1);
                                                        }
                                                        recyclerViewRejected.setLayoutManager(new LinearLayoutManager(getContext()));
                                                        recyclerViewRejected.setAdapter(new applicationCard_ViewAdapter(getContext(), rejectedOffers, rejectedApplicationsIds));
                                                    }
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle the error
                                }
                            });
                }

                mHandler.postDelayed(this, 3000); // Refresh every 3 seconds
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