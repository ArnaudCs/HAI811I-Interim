package com.example.interim.offers;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.Utils.CategoryRepository;
import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class fragment_favorite_offers extends Fragment {
    RecyclerView favoriteContainer;
    FirebaseFirestore db;

    Boolean isPro = false;
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            this.getActivity().finish();
            return;
        }

        Bundle args = getArguments();
        if (args != null) {
            String pro = args.getString("pro");
            if(pro.equals("pro")){
                isPro = true;
            }
        }

        Button favoriteFilterBtn = view.findViewById(R.id.filterBtnFavorites);
        Button closeFilterFavorite = view.findViewById(R.id.closeFilterFavorites);
        Button filtersSearchBtn = view.findViewById(R.id.validateAndSearchFavoriteBtn);
        Button backBtnFavoritePro = view.findViewById(R.id.backBtnFavoritePro);
        LinearLayout filterFavoriteContainer = view.findViewById(R.id.filterFavoriteContainer);
        LinearLayout topDeckContainer = view.findViewById(R.id.topDeckContainer);
        LinearLayout backBtnProContainer = view.findViewById(R.id.backBtnProContainer);
        favoriteContainer = view.findViewById(R.id.favoriteContainer);
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.navbar);
        TextInputEditText cityChoice = view.findViewById(R.id.textCityInput);
        TextInputEditText startPrice = view.findViewById(R.id.textStartPrice);
        TextInputEditText endPrice = view.findViewById(R.id.textEndPrice);
        TextInputEditText startDate = view.findViewById(R.id.textStartDate);
        TextInputEditText endDate = view.findViewById(R.id.textEndDate);
        Spinner categoryChoice = view.findViewById(R.id.categoryChoice);

        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getResources().getString(R.string.chooseCat));

        String deviceLanguage = Locale.getDefault().getLanguage();
        CategoryRepository categoryMapInstance = new CategoryRepository();
        Map<Integer, List<String>> categories = categoryMapInstance.getCategoryMap();

        backBtnFavoritePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        if (categories != null) {
            List<String> frenchCategories = new ArrayList<>();
            List<String> englishCategories = new ArrayList<>();

            for (Map.Entry<Integer, List<String>> entry : categories.entrySet()) {
                List<String> translations = entry.getValue();
                if (translations.size() >= 2) {
                    frenchCategories.add(translations.get(0));
                    englishCategories.add(translations.get(1));
                }
            }

            if (frenchCategories != null && deviceLanguage.equals("fr")) {
                spinnerArray.addAll(frenchCategories);
            } else if (englishCategories != null) {
                spinnerArray.addAll(englishCategories);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryChoice.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();

        if(isPro){
            topDeckContainer.setWeightSum(7);
            backBtnProContainer.setVisibility(View.VISIBLE);
        }


        String userId = mAuth.getCurrentUser().getUid();
        if(mAuth.getCurrentUser() != null) {


            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final List<String>[] likedOffersIds = new List[]{null};
                    if (documentSnapshot.exists()) {
                        likedOffersIds[0] = (List<String>) documentSnapshot.get("likedOffers");
                    } else {
                        // user document does not exist, query likedOffers from Pros collection
                        DocumentReference prosRef = db.collection("Pros").document(userId);
                        prosRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    likedOffersIds[0] = (List<String>) documentSnapshot.get("likedOffers");
                                    if (likedOffersIds[0] != null && likedOffersIds[0].size() >= 1) {
                                        Query offersQuery = db.collection("Offers").whereIn(FieldPath.documentId(), likedOffersIds[0]);
                                        offersQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                                                        // handle failure
                                                    }
                                                });
                                    }
                                }
                            }
                        });
                    }
                    if (likedOffersIds[0] != null && likedOffersIds[0].size() >= 1) {
                        Query offersQuery = db.collection("Offers").whereIn(FieldPath.documentId(), likedOffersIds[0]);
                        offersQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                                        // handle failure
                                    }
                                });
                    }
                }
            });
        } else {
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
                    if(!isPro){
                        bottomNav.animate().translationY(bottomNav.getHeight() + 100).setDuration(200);
                        scrolledDistance = 0;
                        isScrollingDown = false;
                    }

                } else if (scrolledDistance < -scrollThreshold && !isScrollingDown) {
                    // Show BottomNavigationView
                    if(!isPro){
                        bottomNav.animate().translationY(0).setDuration(200);
                        scrolledDistance = 0;
                        isScrollingDown = true;
                    }
                }
                if ((isScrollingDown && scrollY > oldScrollY) || (!isScrollingDown && scrollY < oldScrollY)) {
                    if(!isPro){
                        scrolledDistance += (scrollY - oldScrollY);
                    }
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

        filtersSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterFavoriteContainer);
                filterFavoriteContainer.setVisibility(View.GONE);
                closeFilterFavorite.setVisibility(View.GONE);
                favoriteFilterBtn.setVisibility(View.VISIBLE);

                String city = cityChoice.getText().toString();
                String minSalaryString = startPrice.getText().toString();
                String maxSalaryString = endPrice.getText().toString();
                String catFilter = categoryChoice.getSelectedItem().toString();

                final List<String>[] likedOffersIds = new List[]{null};
                DocumentReference userRef = db.collection("Users").document(userId);
                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            likedOffersIds[0] = (List<String>) documentSnapshot.get("likedOffers");
                        } else {
                            // user document does not exist, query likedOffers from Pros collection
                            DocumentReference prosRef = db.collection("Pros").document(userId);
                            prosRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        likedOffersIds[0] = (List<String>) documentSnapshot.get("likedOffers");
                                        filterOffers(city, minSalaryString, maxSalaryString, likedOffersIds[0], catFilter);
                                    }
                                }
                            });
                        }

                        // Call the filtering function with the retrieved likedOffersIds
                        if (likedOffersIds[0] != null && likedOffersIds[0].size() >= 1) {
                            filterOffers(city, minSalaryString, maxSalaryString, likedOffersIds[0], catFilter);
                        }
                    }
                });
            }
        });

    }


    private void filterOffers(String city, String minSalaryString, String maxSalaryString, List<String> likedOffersIds, String categoryFilter) {
        db.collection("Offers")
                .whereIn(FieldPath.documentId(), likedOffersIds)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        ArrayList<Offer> offers = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            Offer offer = documentSnapshot.toObject(Offer.class);
                            offer.setId(documentSnapshot.getId());


                            // Apply filtering locally
                            boolean matchesFilter = true;

                            if (!city.isEmpty() && !offer.getLocation().equals(city)) {
                                matchesFilter = false;
                            }

                            if (!minSalaryString.isEmpty() && offer.getSalaryMin() < Float.parseFloat(minSalaryString)) {
                                matchesFilter = false;
                            }

                            if (!maxSalaryString.isEmpty() && offer.getSalaryMax() > Float.parseFloat(maxSalaryString)) {
                                matchesFilter = false;
                            }

                            if (!offer.getCategory().equals(categoryFilter)) {
                                matchesFilter = false;
                            }

                            if (matchesFilter) {
                                offers.add(offer);
                            }
                        }

                        favoriteContainer.setLayoutManager(new LinearLayoutManager(getContext()));
                        favoriteContainer.setAdapter(new searchCard_ViewAdapter(getContext(), offers));
                        favoriteContainer.getAdapter().notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }
}