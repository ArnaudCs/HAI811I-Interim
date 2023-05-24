package com.example.interim.discussion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import com.example.interim.Utils.DataHolder;
import com.example.interim.R;

public class DiscussionViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve conversation ID extra from the intent
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            setTheme(R.style.ThemeDark_Interim);
        else
            setTheme(R.style.Theme_Interim);
        String conversationId = getIntent().getStringExtra("conversationId");
        DataHolder.getInstance().setConversationId(conversationId);
        setContentView(R.layout.activity_discussion_view);
    }
}



