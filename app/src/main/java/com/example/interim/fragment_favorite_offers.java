package com.example.interim;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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