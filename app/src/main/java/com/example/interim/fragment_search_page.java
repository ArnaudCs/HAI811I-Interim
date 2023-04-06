package com.example.interim;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.interim.models.Offer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
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
        Button validateAndSearchBtn = view.findViewById(R.id.validateAndSearchBtn);

//        Button likeInit = view.findViewById(R.id.likeInit);
//        LottieAnimationView likeBtn = view.findViewById(R.id.likeBtn);

        Spinner categoryChoice = (Spinner) view.findViewById(R.id.categoryChoice);
        Spinner labelChoice = (Spinner) view.findViewById(R.id.labelChoice);
        Spinner cityChoice = (Spinner) view.findViewById(R.id.cityChoice);
        LinearLayout filterContainer = view.findViewById(R.id.filterContainer);
        TextView areaDisplay = view.findViewById(R.id.areaDisplay);
        SeekBar areaChoice = view.findViewById(R.id.areaChoice);
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.navbar);

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
        cityChoice.setAdapter(adapter1);

        List<Offer> mockOffers = new ArrayList<>();

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

// Offer 1
        Date startDate1 = new Date(1648873200000L); // 1st March 2022
        Date endDate1 = new Date(1670409199000L); // 5th June 2023
        Date postDate1 = new Date(1646751600000L); // 6th March 2022
        Date expDate1 = new Date(1659438000000L); // 2nd August 2022
        Offer offer1 = new Offer(
                "Software Engineer",
                "Google",
                "Mountain View, CA",
                startDate1,
                endDate1,
                postDate1,
                expDate1,
                "Java, Python, Kubernetes",
                "Software Engineering",
                "Full-time",
                120000,
                150000,
                "We are seeking a skilled software engineer to join our team at Google. The ideal candidate will have experience with Java, Python, and Kubernetes.",
                "https://www.google.com/careers/software-engineer"
        );
        mockOffers.add(offer1);

// Offer 2
        Date startDate2 = new Date(1652943600000L); // 18th May 2022
        Date endDate2 = new Date(1688141999000L); // 28th October 2023
        Date postDate2 = new Date(1646578800000L); // 5th March 2022
        Date expDate2 = new Date(1656092400000L); // 24th June 2022
        Offer offer2 = new Offer(
                "Marketing Manager",
                "Amazon",
                "Seattle, WA",
                startDate2,
                endDate2,
                postDate2,
                expDate2,
                "SEO, SEM, Social Media",
                "Marketing",
                "Full-time",
                90000,
                120000,
                "Amazon is seeking a talented marketing manager to join our team. The ideal candidate will have experience with SEO, SEM, and social media marketing.",
                "https://www.amazon.jobs/marketing-manager"
        );
        mockOffers.add(offer2);

// Offer 3
        Date startDate3 = new Date(1664540400000L); // 31st August 2022
        Date endDate3 = new Date(1695980399000L); // 28th January 2024
        Date postDate3 = new Date(1650946800000L); // 25th May 2022
        Date expDate3 = new Date(1661967600000L); // 1st September 2022
        Offer offer3 = new Offer(
                "Financial Analyst",
                "Goldman Sachs",
                "New York, NY",
                startDate3,
                endDate3,
                postDate3,
                expDate3,
                "Financial Modeling, Excel, Accounting",
                "Finance",
                "Full-time",
                85000,
                110000,
                "We are seeking a talented financial analyst to join our team at Goldman Sachs. The ideal candidate will have experience with financial modeling, Excel, and accounting.",
                "https://www.goldmansachs.com/careers/financial-analyst"
        );
        mockOffers.add(offer3);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new searchCard_ViewAdapter(getContext(), mockOffers));



        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterContainer);
                filterContainer.setVisibility(view.VISIBLE);
                closeFilter.setVisibility(view.VISIBLE);
                filterBtn.setVisibility(view.GONE);
            }
        });

//

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
            }
        });

        validateAndSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterContainer);
                filterContainer.setVisibility(view.GONE);
                closeFilter.setVisibility(view.GONE);
                filterBtn.setVisibility(view.VISIBLE);
            }
        });

    }
}