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

public class fragment_order_summary extends Fragment {
    public fragment_order_summary() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_summary, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button backButtonOrder = view.findViewById(R.id.backOrderBtn);
        Button validateBasket = view.findViewById(R.id.validateBasket);

        if (validateBasket != null) {
            validateBasket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*fragment_order_summary fragmentOrderSummary = new fragment_order_summary();

                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.paymentContainer, fragmentOrderSummary)
                            .addToBackStack(null)
                            .commit();*/
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