package com.example.interim;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.interim.models.Conversation;

import java.util.ArrayList;
import java.util.List;

public class fragment_message_menu extends Fragment {

    Button deleteMessages;
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
        deleteMessages = view.findViewById(R.id.deleteMessages);
        List<Conversation> conversations = new ArrayList<>();

// Add some fake conversations to the list
        conversations.add(new Conversation("John Doe", true, "Hey, how's it going?"));
        conversations.add(new Conversation("Jane Smith", true, "Did you see the game last night?"));
        conversations.add(new Conversation("Alex Johnson", false, "Thanks for the invite to the party!"));
        conversations.add(new Conversation("Emily Davis", false, "Can you send me the report by EOD?"));
        conversations.add(new Conversation("Mike Wilson", true, "What's up, man?"));

        RecyclerView recyclerView = view.findViewById(R.id.conversationsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new conversation_ViewAdapter(getContext(),conversations));


        deleteMessages.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerView.getAdapter();
                    }
                }
        );
    }



}