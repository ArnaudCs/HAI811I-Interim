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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class fragment_order_summary extends Fragment {
    public fragment_order_summary() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_summary, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button backButtonOrder = view.findViewById(R.id.backOrderBtn);
        Button validateBasket = view.findViewById(R.id.validateBasket);
        TextView priceDisplay = view.findViewById(R.id.priceDisplay);
        TextView planDisplay = view.findViewById(R.id.subscriptionChoice);

        String planPrice = DataHolder.getInstance().getPlanPrice();
        String planName = DataHolder.getInstance().getPlanName();

        planDisplay.setText(planName);
        priceDisplay.setText(planPrice + " $");

        if (validateBasket != null) {
            validateBasket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        backButtonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });
    }
}