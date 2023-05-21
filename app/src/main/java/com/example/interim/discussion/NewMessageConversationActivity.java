package com.example.interim.discussion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.interim.R;
import com.example.interim.profile.fragment_user_company;

public class NewMessageConversationActivity extends AppCompatActivity {

    String mail, message;
    Boolean hasMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message_conversation);

        Intent intent = getIntent();
        if (intent != null) {
            mail = intent.getStringExtra("mail");
            if (intent.hasExtra("message")) {
                message = intent.getStringExtra("message");
                hasMessage = true;
            }
        }

        fragment_new_message_conversation fragment = new fragment_new_message_conversation();

        Bundle args = new Bundle();
        args.putString("mail", mail);
        if (hasMessage){
            args.putString("message", message);
        }
        fragment.setArguments(args);

        // Ajouter le fragment au conteneur de fragments
        getSupportFragmentManager().beginTransaction()
                .add(R.id.newConversationContainer, fragment)
                .commit();
    }
}