package com.example.interim.offers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.interim.R;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class fragment_my_applications extends Fragment {
    String userId;
    RecyclerView recyclerViewAccepted;
    RecyclerView recyclerViewRejected;
    RecyclerView recyclerViewPending;

    LinearLayout acceptedContainer, pendingContainer, rejectedContainer;
    ArrayList<Offer> offers;

    Button acceptedBtn, pendingBtn, rejectedBtn;

    public fragment_my_applications() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_applications, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button backButtonApplications = view.findViewById(R.id.backButtonApplications);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }


        recyclerViewAccepted = view.findViewById(R.id.acceptedDisplay);
        recyclerViewPending = view.findViewById(R.id.pendingDisplay);
        recyclerViewRejected = view.findViewById(R.id.rejectedDisplay);

        acceptedContainer = view.findViewById(R.id.acceptedContainer);
        pendingContainer = view.findViewById(R.id.pendingContainer);
        rejectedContainer = view.findViewById(R.id.rejectedContainer);

        if(userId != null) {
            db.collection("Applications")
                    .whereEqualTo("applicantId", userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            ArrayList<String> offerIds = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                offerIds.add(documentSnapshot.getString("offerId"));
                            }
                            db.collection("Offers")
                                    .whereIn(FieldPath.documentId(), offerIds)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot querySnapshot) {
                                            offers = new ArrayList<>();
                                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                                Offer offer = documentSnapshot.toObject(Offer.class);
                                                offer.setId(documentSnapshot.getId());
                                                offers.add(offer);
                                            }
                                            recyclerViewAccepted.setLayoutManager(new LinearLayoutManager(getContext()));
                                            recyclerViewAccepted.setAdapter(new applicationCard_ViewAdapter(getContext(), offers));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the error
                                        }
                                    });
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

        acceptedBtn = view.findViewById(R.id.acceptedBtn);
        pendingBtn = view.findViewById(R.id.pendingBtn);
        rejectedBtn = view.findViewById(R.id.rejectedBtn);

        acceptedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Changement des couleurs des boutons
                acceptedBtn.setBackground(getResources().getDrawable(R.drawable.redbutton));
                acceptedBtn.setTextColor(getResources().getColor(R.color.white));

                pendingBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                rejectedBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                pendingBtn.setTextColor(getResources().getColor(R.color.grey));
                rejectedBtn.setTextColor(getResources().getColor(R.color.grey));

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
                pendingBtn.setBackground(getResources().getDrawable(R.drawable.redbutton));
                pendingBtn.setTextColor(getResources().getColor(R.color.white));

                acceptedBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                rejectedBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                acceptedBtn.setTextColor(getResources().getColor(R.color.grey));
                rejectedBtn.setTextColor(getResources().getColor(R.color.grey));

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
                rejectedBtn.setBackground(getResources().getDrawable(R.drawable.redbutton));
                rejectedBtn.setTextColor(getResources().getColor(R.color.white));

                pendingBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                acceptedBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                pendingBtn.setTextColor(getResources().getColor(R.color.grey));
                acceptedBtn.setTextColor(getResources().getColor(R.color.grey));

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.GONE);
                pendingContainer.setVisibility(View.GONE);
                rejectedContainer.setVisibility(View.VISIBLE);
            }
        });
    }
}