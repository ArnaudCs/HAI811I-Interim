package com.example.interim;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class fragment_order_summary extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String mUserEmail;
    private String mUserId;

    public fragment_order_summary() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUserEmail = mAuth.getCurrentUser().getEmail();
        mUserId = mAuth.getCurrentUser().getUid();
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
                    Date now = new Date();
                    Map<String, Object> subscription = new HashMap<>();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(now);

                    int durationInDays;
                    switch (planPrice) {
                        case "10":
                            durationInDays = 1;
                            break;
                        case "20":
                            durationInDays = 30;
                            break;
                        case "50":
                            durationInDays = 90;
                            break;
                        case "100":
                            durationInDays = 180;
                            break;
                        case "190":
                            durationInDays = 365;
                            break;
                        case "1000":
                            durationInDays = -1; // Unlimited
                            break;
                        default:
                            durationInDays = 0;
                            break;
                    }

                    calendar.add(Calendar.DATE, durationInDays);

                    Date endDate = calendar.getTime();
                    subscription.put("plan", planName);
                    subscription.put("price", planPrice);
                    subscription.put("startDate", now);
                    subscription.put("endDate", endDate);

                    DocumentReference docRef = mFirestore.collection("Subscriptions").document(mUserId);
                    docRef.set(subscription)
                            .addOnSuccessListener(aVoid -> {
                                // Subscription saved successfully
                                Toast.makeText(getContext(), "Subscription saved", Toast.LENGTH_SHORT).show();
                                fragment_payment_confirm fragmentPaymentConfirm = new fragment_payment_confirm();

                                // Remplacer le fragment actuel par le nouveau fragment
                                FragmentManager fragmentManager = getParentFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.paymentContainer, fragmentPaymentConfirm)
                                        .addToBackStack(null)
                                        .commit();

                            })
                            .addOnFailureListener(e -> {
                                // Error saving subscription
                                Toast.makeText(getContext(), "Error saving subscription", Toast.LENGTH_SHORT).show();
                                FragmentManager fragmentManager = getParentFragmentManager();
                                fragmentManager.popBackStackImmediate();
                            });
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
