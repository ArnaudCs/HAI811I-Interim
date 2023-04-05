package com.example.interim;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import java.util.ArrayList;
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
        Button likeBtn = view.findViewById(R.id.likeBtn);
        Button validateAndSearchBtn = view.findViewById(R.id.validateAndSearchBtn);
        Spinner categoryChoice = (Spinner) view.findViewById(R.id.categoryChoice);
        Spinner labelChoice = (Spinner) view.findViewById(R.id.labelChoice);
        Spinner cityChoice = (Spinner) view.findViewById(R.id.cityChoice);
        LinearLayout filterContainer = view.findViewById(R.id.filterContainer);
        TextView areaDisplay = view.findViewById(R.id.areaDisplay);
        SeekBar areaChoice = view.findViewById(R.id.areaChoice);

        //Initialisation de la valeur par défaut du progress de la barre de sélection
        areaDisplay.setText(getResources().getString(R.string.areaFilter) + String.valueOf((areaChoice.getProgress() + 1) * 10) + " Km");

        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Chantier et BTP");
        spinnerArray.add("Nettoyage");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryChoice.setAdapter(adapter);
        labelChoice.setAdapter(adapter);

        List<String> spinnerCity =  new ArrayList<String>();
        spinnerArray.add("Marseille");
        spinnerArray.add("Montpellier");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityChoice.setAdapter(adapter1);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(filterContainer);
                filterContainer.setVisibility(view.VISIBLE);
                closeFilter.setVisibility(view.VISIBLE);
                filterBtn.setVisibility(view.GONE);
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(liked){
                    likeBtn.setBackground(getResources().getDrawable(R.drawable.baseline_favorite_border_24));
                    liked = !liked;
                } else {
                    likeBtn.setBackground(getResources().getDrawable(R.drawable.baseline_favorite_24));
                    liked = !liked;
                }
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