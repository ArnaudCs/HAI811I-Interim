package com.example.interim.offers;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        TextInputEditText textOfferName = view.findViewById(R.id.textOfferName);
        TextInputEditText textOfferCompanyName = view.findViewById(R.id.textOfferCompanyName);
        TextInputEditText textOfferSalary = view.findViewById(R.id.textOfferSalary);
        TextInputEditText textOfferCity = view.findViewById(R.id.textCityOffer);
        TextInputEditText textOfferWebsite = view.findViewById(R.id.textOfferWebsite);
        TextInputEditText textOfferTags = view.findViewById(R.id.textOfferTags);
        TextInputEditText textOfferLabels = view.findViewById(R.id.textOfferLabels);
        EditText textOfferDescription = view.findViewById(R.id.offerDescriptionText);
        EditText textOfferDetails = view.findViewById(R.id.offerDetailsText);
        Button addOfferButton = view.findViewById(R.id.addOfferButton);


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


        addOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Get input values from views
                String jobTitle = textOfferName.getText().toString();
                String companyName = textOfferCompanyName.getText().toString();
                String salaryStr = textOfferSalary.getText().toString();
                String location = textOfferCity.getText().toString();
                String url = textOfferWebsite.getText().toString();
                String keywords = textOfferTags.getText().toString();
                String label = textOfferLabels.getText().toString();
                String description = textOfferDescription.getText().toString();
                String details = textOfferDetails.getText().toString();
                String category = categoryOfferChoice.getSelectedItem().toString();

                // Parse salary
                int salaryMin = Integer.parseInt(salaryStr), salaryMax = Integer.parseInt(salaryStr);
                if (!TextUtils.isEmpty(salaryStr)) {
                    String[] salaryParts = salaryStr.split("-");
                    if (salaryParts.length == 2) {
                        salaryMin = Integer.parseInt(salaryParts[0].trim());
                        salaryMax = Integer.parseInt(salaryParts[1].trim());
                    }
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
                Date startDate = null, endDate = null, today = null, expDate = null;
                try {
                    startDate = sdf.parse(textDateStartOffer.getText().toString());
                    endDate = sdf.parse(textDateEndOffer.getText().toString());
                    today = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(today);
                    c.add(Calendar.DATE, 30);
                    expDate = c.getTime();

                } catch (ParseException e) {
                }


                Offer offer = new Offer(jobTitle, companyName, location, startDate, endDate, today, expDate, keywords, category, label, salaryMin, salaryMax, description, details, url);

                db.collection("Offers")
                        .add(offer)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                textOfferName.setText("");
                                textOfferCompanyName.setText("");
                                textOfferSalary.setText("");
                                textOfferCity.setText("");
                                textOfferWebsite.setText("");
                                textOfferTags.setText("");
                                textOfferLabels.setText("");
                                textOfferDescription.setText("");
                                textOfferDetails.setText("");
                                categoryOfferChoice.setSelection(0);
                                layoutDateStartOffer.setError(null);
                                layoutDateEndOffer.setError(null);
                                textDateStartOffer.setText("");
                                textDateEndOffer.setText("");

                                Intent mainActivity = new Intent(getActivity(), CelebrationActivity.class);
                                startActivity(mainActivity);
                                getActivity().finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
        });



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