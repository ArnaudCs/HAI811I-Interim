package com.example.interim.offers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.interim.R;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class fragment_search_page extends Fragment {


    boolean liked = false;
    public fragment_search_page() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_page, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button filterBtn = view.findViewById(R.id.filterBtn);
        Button closeFilter = view.findViewById(R.id.closeFilter);
        Button filtersSearchBtn = view.findViewById(R.id.validateAndSearchBtn);

//        Button likeInit = view.findViewById(R.id.likeInit);
//        LottieAnimationView likeBtn = view.findViewById(R.id.likeBtn);
        Button searchBtn = view.findViewById(R.id.searchBtn);
        Button clearFiltersButton = view.findViewById(R.id.clearFilterBtn);
        Spinner categoryChoice = (Spinner) view.findViewById(R.id.categoryChoice);
        Spinner labelChoice = (Spinner) view.findViewById(R.id.labelChoice);
        TextInputEditText cityChoice = view.findViewById(R.id.textCityInput);
        TextInputEditText startPrice = view.findViewById(R.id.textStartPrice);
        TextInputEditText endPrice = view.findViewById(R.id.textEndPrice);
        TextInputEditText startDate = view.findViewById(R.id.textStartDate);
        TextInputEditText endDate = view.findViewById(R.id.textEndDate);
        ScrollView filterContainer = view.findViewById(R.id.filterContainer);
        TextView areaDisplay = view.findViewById(R.id.areaDisplay);
        SeekBar areaChoice = view.findViewById(R.id.areaChoice);
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.navbar);
        TextView searchText = view.findViewById(R.id.searchText);
        //Initialisation de la valeur par défaut du progress de la barre de sélection
        areaDisplay.setText(getResources().getString(R.string.areaFilter) + String.valueOf((areaChoice.getProgress() + 1) * 10) + " Km");

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add(getResources().getString(R.string.chooseCat));
        spinnerArray.add("Chantier et BTP");
        spinnerArray.add("Nettoyage");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryChoice.setAdapter(adapter);
        labelChoice.setAdapter(adapter);

        List<String> spinnerCity =  new ArrayList<String>();
        spinnerArray.add("Marseille");
        spinnerArray.add("Montpellier");
        RecyclerView recyclerView = view.findViewById(R.id.cardContainer);


        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        List<Offer> mockOffers = new ArrayList<>();

        clearFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryChoice.setSelection(0);
                labelChoice.setSelection(0);
                cityChoice.setText("");
                startPrice.setText("");
                endPrice.setText("");
                startDate.setText("");
                endDate.setText("");

                areaChoice.setProgress(1);
                areaDisplay.setText(getResources().getString(R.string.areaFilter) + String.valueOf((areaChoice.getProgress() + 1) * 10) + " Km");

            }
        });

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Offers")
                .orderBy("postDate", Query.Direction.DESCENDING)
                .limit(50)
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
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new searchCard_ViewAdapter(getContext(), offers));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterContainer);
                filterContainer.setVisibility(View.VISIBLE);
                closeFilter.setVisibility(View.VISIBLE);
                filterBtn.setVisibility(View.GONE);
                bottomNav.animate().translationY(bottomNav.getHeight() + 100).setDuration(200);
            }
        });

        areaChoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                i = (i+1)*10;
                areaDisplay.setText(getResources().getString(R.string.areaFilter) + String.valueOf(i) + " Km");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterContainer);
                filterContainer.setVisibility(view.GONE);
                closeFilter.setVisibility(view.GONE);
                filterBtn.setVisibility(view.VISIBLE);
                bottomNav.animate().translationY(0).setDuration(200);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchText.getText().toString().toLowerCase();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("Offers")
                        .orderBy("postDate", Query.Direction.DESCENDING)
                        .limit(50)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                List<Offer> offers = new ArrayList<>();
                                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                    Offer offer = documentSnapshot.toObject(Offer.class);
                                    offer.setId(documentSnapshot.getId());
                                    if (offer.getKeywords().contains(query) ||
                                            offer.getJobTitle().toLowerCase().contains(query) ||
                                            offer.getLabel().toLowerCase().contains(query) ||
                                            offer.getCompanyName().toLowerCase().contains(query)) {
                                        offers.add(offer);
                                    }
                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(new searchCard_ViewAdapter(getContext(), offers));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                            }
                        });
            }
        });



        filtersSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterContainer);
                filterContainer.setVisibility(view.GONE);
                closeFilter.setVisibility(view.GONE);
                filterBtn.setVisibility(view.VISIBLE);
                bottomNav.animate().translationY(0).setDuration(200);

//                String category = categoryChoice.getSelectedItem().toString();
//                String label = labelChoice.getSelectedItem().toString();
//                String city = cityChoice.getSelectedItem().toString();
//
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                CollectionReference offersRef = db.collection("Offers");
//
//                Query query = offersRef;
//
//                if (city != null) {
//                    query = query.whereEqualTo("location", city);
//                }
//
//                if (startPriceValue != null && endPriceValue != null) {
//                    Query priceQuery = offersRef.whereGreaterThanOrEqualTo("priceMax", startPriceValue)
//                            .whereLessThanOrEqualTo("priceMin", endPriceValue);
//                    queries.add(priceQuery);
//                }
//
//                Task<List<QuerySnapshot>> task = Tasks.whenAllSuccess(queries);
//                task.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
//                    @Override
//                    public void onSuccess(List<QuerySnapshot> querySnapshots) {
//                        List<Offer> offers = new ArrayList<>();
//                        for (QuerySnapshot querySnapshot : querySnapshots) {
//                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
//                                Offer offer = documentSnapshot.toObject(Offer.class);
//                                offer.setId(documentSnapshot.getId());
//                                offers.add(offer);
//                            }
//                        }
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        recyclerView.setAdapter(new searchCard_ViewAdapter(getContext(), offers));
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("Error", "Failed to search for offers with filters");
//                    }
//                });
//
            }

            });
    }
}