package com.example.interim.offers;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.interim.AppActivity;
import com.example.interim.R;
import com.example.interim.authentication.MainActivity;

public class fragment_offer_post_celebration extends Fragment {

    public fragment_offer_post_celebration() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offer_post_celebration, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button myOffersBtn = view.findViewById(R.id.myOffersBtn);

        myOffersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(getActivity(), AppActivity.class);
                startActivity(mainActivity);
                getActivity().finish();
            }
        });
    }
}