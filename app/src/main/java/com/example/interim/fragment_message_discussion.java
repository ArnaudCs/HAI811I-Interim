package com.example.interim;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class fragment_message_discussion extends Fragment {

    public fragment_message_discussion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_discussion, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button infosBtn = view.findViewById(R.id.infosBtn);
        Button closeInfos = view.findViewById(R.id.closeInfos);
        Button signalUser = view.findViewById(R.id.signalUserBtn);
        Button blockUser = view.findViewById(R.id.signalUserBtn);
        LinearLayout infosContainer = view.findViewById(R.id.infosContainer);

        infosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(infosContainer);
                infosContainer.setVisibility(View.VISIBLE);
                infosBtn.setVisibility(View.GONE);
                closeInfos.setVisibility(View.VISIBLE);
            }
        });

        closeInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(infosContainer);
                infosContainer.setVisibility(View.GONE);
                infosBtn.setVisibility(View.VISIBLE);
                closeInfos.setVisibility(View.GONE);
            }
        });

    }
}