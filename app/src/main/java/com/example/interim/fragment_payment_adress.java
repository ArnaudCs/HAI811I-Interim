package com.example.interim;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class fragment_payment_adress extends Fragment {

    public fragment_payment_adress() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_adress, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button useAdress = view.findViewById(R.id.useAdressBtn);
        Button backButtonAdressInfos = view.findViewById(R.id.backAdressBtn);
        TextInputEditText street = view.findViewById(R.id.textStreet);
        TextInputEditText appartment = view.findViewById(R.id.textApt);
        TextInputEditText country = view.findViewById(R.id.textCountry);
        TextInputEditText state = view.findViewById(R.id.textState);
        TextInputEditText postalCode = view.findViewById(R.id.textPostalCode);

        CheckBox defaultAdress = view.findViewById(R.id.defaultAdress);

        if (useAdress != null) {
            useAdress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(street.getText()) && !TextUtils.isEmpty(appartment.getText()) && !TextUtils.isEmpty(state.getText())
                            && !TextUtils.isEmpty(country.getText()) && !TextUtils.isEmpty(postalCode.getText())) {

                        fragment_order_summary fragmentOrderSummary = new fragment_order_summary();

                        // Remplacer le fragment actuel par le nouveau fragment
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.paymentContainer, fragmentOrderSummary)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), R.string.missingFieldsErroToast, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        backButtonAdressInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });
    }
}