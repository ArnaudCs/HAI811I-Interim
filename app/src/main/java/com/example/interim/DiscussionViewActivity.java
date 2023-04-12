package com.example.interim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class DiscussionViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve conversation ID extra from the intent
        String conversationId = getIntent().getStringExtra("conversationId");
        DataHolder.getInstance().setConversationId(conversationId);
        setContentView(R.layout.activity_discussion_view);
    }
}



