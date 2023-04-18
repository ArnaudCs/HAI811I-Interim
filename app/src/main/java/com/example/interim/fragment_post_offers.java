package com.example.interim;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class fragment_post_offers extends Fragment {
    public fragment_post_offers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_offers, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout layoutDateStartOffer = view.findViewById(R.id.dateOfferStartLayout);
        TextInputLayout layoutDateEndOffer = view.findViewById(R.id.dateOfferEndLayout);

        TextInputEditText textDateStartOffer = view.findViewById(R.id.textOfferStartDate);
        TextInputEditText textDateEndOffer = view.findViewById(R.id.textOfferEndDate);

        BottomNavigationView bottomNav = getActivity().findViewById(R.id.navbar);
        DatePickerDialog.OnDateSetListener setListener;
        Spinner categoryOfferChoice = view.findViewById(R.id.categoryOfferChoice);
        ScrollView formScrollContainer = view.findViewById(R.id.formScrollContainer);


        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add(getResources().getString(R.string.chooseCat));
        spinnerArray.add("Chantier et BTP");
        spinnerArray.add("Nettoyage");
        spinnerArray.add("Informatique");
        spinnerArray.add("Nettoyage");
        spinnerArray.add("Manutention");
        spinnerArray.add("Automobile");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryOfferChoice.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DATE);

        formScrollContainer.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            private int scrollThreshold = 10;
            private int scrolledDistance = 0;
            private boolean isScrollingDown = false;

            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int currentScrollPosition = scrollY;

                if (scrolledDistance > scrollThreshold && isScrollingDown ) {
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

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = day+"/"+month+"/"+year;
            }
        };

        textDateStartOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        textDateStartOffer.setText(date);
                    }
                }, year,month,day);
                datePickerDialog.show();
            }
        });

        textDateEndOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        textDateEndOffer.setText(date);
                    }
                }, year,month,day);
                datePickerDialog.show();
            }
        });
    }
}