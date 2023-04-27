package com.example.interim;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class fragment_application_manager extends Fragment {

    Button pendingApplicationsBtn, acceptedApplications, rejectedApplications, backButtonApplications;

    LinearLayout pendingContainer, acceptedContainer, rejectedContainer;

    RecyclerView pendingApplications, acceptedDisplay, rejectedDisplay;

    public fragment_application_manager() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_application_manager, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pendingApplicationsBtn = view.findViewById(R.id.pendingApplicationsBtn);
        acceptedApplications = view.findViewById(R.id.acceptedApplications);
        rejectedApplications = view.findViewById(R.id.rejectedApplications);

        pendingContainer = view.findViewById(R.id.pendingContainer);
        acceptedContainer = view.findViewById(R.id.acceptedContainer);
        rejectedContainer = view.findViewById(R.id.rejectedContainer);

        pendingApplications = view.findViewById(R.id.pendingApplications);
        acceptedDisplay = view.findViewById(R.id.acceptedDisplay);
        rejectedDisplay = view.findViewById(R.id.rejectedDisplay);
        backButtonApplications = view.findViewById(R.id.backButtonApplications);

        backButtonApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        acceptedApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Changement des couleurs des boutons
                acceptedApplications.setBackground(getResources().getDrawable(R.drawable.redbutton));
                acceptedApplications.setTextColor(getResources().getColor(R.color.white));

                pendingApplicationsBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                rejectedApplications.setBackground(getResources().getDrawable(R.drawable.greybutton));
                pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.grey));
                rejectedApplications.setTextColor(getResources().getColor(R.color.grey));

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.VISIBLE);
                pendingContainer.setVisibility(View.GONE);
                rejectedContainer.setVisibility(View.GONE);
            }
        });

        pendingApplicationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Changement des couleurs des boutons
                pendingApplicationsBtn.setBackground(getResources().getDrawable(R.drawable.redbutton));
                pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.white));

                acceptedApplications.setBackground(getResources().getDrawable(R.drawable.greybutton));
                rejectedApplications.setBackground(getResources().getDrawable(R.drawable.greybutton));
                acceptedApplications.setTextColor(getResources().getColor(R.color.grey));
                rejectedApplications.setTextColor(getResources().getColor(R.color.grey));

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.GONE);
                pendingContainer.setVisibility(View.VISIBLE);
                rejectedContainer.setVisibility(View.GONE);
            }
        });

        rejectedApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Changement des couleurs des boutons
                rejectedApplications.setBackground(getResources().getDrawable(R.drawable.redbutton));
                rejectedApplications.setTextColor(getResources().getColor(R.color.white));

                pendingApplicationsBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                acceptedApplications.setBackground(getResources().getDrawable(R.drawable.greybutton));
                pendingApplicationsBtn.setTextColor(getResources().getColor(R.color.grey));
                acceptedApplications.setTextColor(getResources().getColor(R.color.grey));

                //Affichage du recyclerView correspondant
                acceptedContainer.setVisibility(View.GONE);
                pendingContainer.setVisibility(View.GONE);
                rejectedContainer.setVisibility(View.VISIBLE);
            }
        });
    }

}