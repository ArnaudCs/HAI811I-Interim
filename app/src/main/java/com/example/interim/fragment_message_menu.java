package com.example.interim;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class fragment_message_menu extends Fragment {

    public fragment_message_menu() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_menu, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button deleteConvBtn = view.findViewById(R.id.deleteMessages);
        Button makeGroupBtn = view.findViewById(R.id.groupMaking);
        Button cancelDelete = view.findViewById(R.id.cancelDelete);
        Button validateDelete = view.findViewById(R.id.validateDelete);
        LinearLayout checkDelete = view.findViewById(R.id.checkDelete);
        LinearLayout goToMessages = view.findViewById(R.id.messageBubble);
        LinearLayout conversationContainer = view.findViewById(R.id.conversationContainer);

        deleteConvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDelete.setVisibility(View.VISIBLE);
                cancelDelete.setVisibility(View.VISIBLE);
                deleteConvBtn.setVisibility(View.GONE);
                validateDelete.setVisibility(View.VISIBLE);
                makeGroupBtn.setVisibility(View.GONE);
                conversationContainer.setWeightSum(6);
            }
        });

        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDelete.setVisibility(View.GONE);
                cancelDelete.setVisibility(View.GONE);
                validateDelete.setVisibility(View.GONE);
                makeGroupBtn.setVisibility(View.VISIBLE);
                deleteConvBtn.setVisibility(View.VISIBLE);
                conversationContainer.setWeightSum(5);
            }
        });

        goToMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussion = new Intent(getActivity(), DiscussionViewActivity.class);
                startActivity(discussion);
            }
        });
    }
}