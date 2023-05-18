package com.example.interim.offers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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

import com.example.interim.CategoryRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class fragment_search_page extends Fragment {


    boolean liked = false;
    ScrollView filterContainer;
    TextInputEditText cityChoice;
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


        Bundle bundle = getArguments();


        Button filterBtn = view.findViewById(R.id.filterBtn);
        Button closeFilter = view.findViewById(R.id.closeFilter);
        Button filtersSearchBtn = view.findViewById(R.id.validateAndSearchBtn);

        Button searchBtn = view.findViewById(R.id.searchBtn);
        Button clearFiltersButton = view.findViewById(R.id.clearFilterBtn);
        Spinner categoryChoice = (Spinner) view.findViewById(R.id.categoryChoice);
        Spinner labelChoice = (Spinner) view.findViewById(R.id.labelChoice);
        cityChoice = view.findViewById(R.id.textCityInput);
        TextInputEditText startPrice = view.findViewById(R.id.textStartPrice);
        TextInputEditText endPrice = view.findViewById(R.id.textEndPrice);
        TextInputEditText startDate = view.findViewById(R.id.textStartDate);
        TextInputEditText endDate = view.findViewById(R.id.textEndDate);
        filterContainer = view.findViewById(R.id.filterContainer);
        TextView areaDisplay = view.findViewById(R.id.areaDisplay);
        SeekBar areaChoice = view.findViewById(R.id.areaChoice);
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.navbar);
        TextView searchText = view.findViewById(R.id.searchText);
        //Initialisation de la valeur par défaut du progress de la barre de sélection
        areaDisplay.setText(getResources().getString(R.string.areaFilter) + String.valueOf((areaChoice.getProgress() + 1) * 10) + " Km");

        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(getResources().getString(R.string.chooseCat));

        String deviceLanguage = Locale.getDefault().getLanguage();
        CategoryRepository categoryMapInstance = new CategoryRepository();
        Map<Integer, List<String>> categories = categoryMapInstance.getCategoryMap();

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

        labelChoice.setAdapter(adapter);
        RecyclerView recyclerView = view.findViewById(R.id.cardContainer);
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
                        ArrayList<Offer> offers = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            Offer offer = documentSnapshot.toObject(Offer.class);
                            offer.setId(documentSnapshot.getId());
                            offers.add(offer);
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new searchCard_ViewAdapter(getContext(), offers));
                        recyclerView.getAdapter().notifyDataSetChanged();
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

        if (bundle != null && bundle.getSerializable("offerForFilters") != null) {
            Offer offerForFilter = (Offer) bundle.getSerializable("offerForFilters");
            categoryChoice.setSelection(0);
            labelChoice.setSelection(0);
            cityChoice.setText(offerForFilter.getLocation());
            startPrice.setText(String.valueOf(offerForFilter.getSalaryMin()));
//            endPrice.setText(String.valueOf(offerForFilter.getSalaryMax()));
            System.out.println(offerForFilter.toString());
//            startDate.setText(offerForFilter.getStartDate().toString());
//            endDate.setText(offerForFilter.getEndDate().toString());
            TransitionManager.beginDelayedTransition(filterContainer);
            filterContainer.setVisibility(View.VISIBLE);
            closeFilter.setVisibility(View.VISIBLE);
            filterBtn.setVisibility(View.GONE);
            bottomNav.animate().translationY(bottomNav.getHeight() + 100).setDuration(200);
        }



        filtersSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterContainer);
                filterContainer.setVisibility(View.GONE);
                closeFilter.setVisibility(View.GONE);
                filterBtn.setVisibility(View.VISIBLE);
                bottomNav.animate().translationY(0).setDuration(200);

                String city = cityChoice.getText().toString();
                String minSalaryString = startPrice.getText().toString();
                String maxSalaryString = endPrice.getText().toString();

                db.collection("Offers")
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

                                    if (matchesFilter) {
                                        offers.add(offer);
                                    }
                                }

                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(new searchCard_ViewAdapter(getContext(), offers));
                                recyclerView.getAdapter().notifyDataSetChanged();
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

    }

//    private void loadFiltersFromDataHolder(filterDataHolder dataHolder) {
//
//        if (dataHolder != null) {
//            System.out.println("DATAHOLDER IS NOT NULL");
//            System.out.println(dataHolder.toString());
//
//            // Vérifier si les champs du dataholder ne sont pas vides
//            if (!TextUtils.isEmpty(dataHolder.getCategory()) && !TextUtils.isEmpty(dataHolder.getJobTitle())) {
//                cityChoice.setText(dataHolder.getCityText());
//                dataHolder.setCityText("");
//                filterContainer.setVisibility(View.VISIBLE);
//            }
//        }
//        else {
//            System.out.println("DATAHOLDER IS NULL");
//        }
//    }
}