package com.example.interim;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class fragment_favorite_offers extends Fragment {
    public fragment_favorite_offers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_offers, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button favoriteFilterBtn = view.findViewById(R.id.filterBtnFavorites);
        Button closeFilterFavorite = view.findViewById(R.id.closeFilterFavorites);
        Button validateAndSearchFavoriteBtn = view.findViewById(R.id.validateAndSearchFavoriteBtn);
        LinearLayout filterFavoriteContainer = view.findViewById(R.id.filterFavoriteContainer);
        RecyclerView favoriteContainer = view.findViewById(R.id.favoriteContainer);
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.navbar);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    List<String> likedOffersIds = (List<String>) documentSnapshot.get("likedOffers");
                    if (likedOffersIds != null && likedOffersIds.size() >= 1) {
                        Query offersQuery = db.collection("Offers").whereIn(FieldPath.documentId(), likedOffersIds);
                        offersQuery
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                        List<Offer> offers = new ArrayList<>();
                                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                            Offer offer = documentSnapshot.toObject(Offer.class);
                                            offer.setId(documentSnapshot.getId());
                                            offers.add(offer);
                                        }
                                        favoriteContainer.setLayoutManager(new LinearLayoutManager(getContext()));
                                        favoriteContainer.setAdapter(new searchCard_ViewAdapter(getContext(), offers));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                }
            });
        }
        else {
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            this.getActivity().finish();
        }




        favoriteContainer.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            private int scrollThreshold = 10;
            private int scrolledDistance = 0;
            private boolean isScrollingDown = false;


            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int currentScrollPosition = scrollY;

                if (scrolledDistance > scrollThreshold && isScrollingDown) {
                    // Hide BottomNavigationView
                    bottomNav.animate().translationY(bottomNav.getHeight() + 100).setDuration(200);
                    scrolledDistance = 0;
                    isScrollingDown = false;
                } else if (scrolledDistance < -scrollThreshold && !isScrollingDown) {
                    // Show BottomNavigationView
                    bottomNav.animate().translationY(0).setDuration(200);
                    scrolledDistance = 0;
                    isScrollingDown = true;
                }

                if ((isScrollingDown && scrollY > oldScrollY) || (!isScrollingDown && scrollY < oldScrollY)) {
                    scrolledDistance += (scrollY - oldScrollY);
                }
            }
        });

        favoriteFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterFavoriteContainer);
                filterFavoriteContainer.setVisibility(View.VISIBLE);
                closeFilterFavorite.setVisibility(View.VISIBLE);
                favoriteFilterBtn.setVisibility(View.GONE);
            }
        });

        closeFilterFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterFavoriteContainer);
                filterFavoriteContainer.setVisibility(view.GONE);
                closeFilterFavorite.setVisibility(view.GONE);
                favoriteFilterBtn.setVisibility(view.VISIBLE);
            }
        });

        validateAndSearchFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterFavoriteContainer);
                filterFavoriteContainer.setVisibility(view.GONE);
                closeFilterFavorite.setVisibility(view.GONE);
                favoriteFilterBtn.setVisibility(view.VISIBLE);
            }
        });
    }
}