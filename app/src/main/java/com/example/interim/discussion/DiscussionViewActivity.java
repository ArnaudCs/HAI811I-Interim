package com.example.interim.discussion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.interim.Utils.DataHolder;
import com.example.interim.R;

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



