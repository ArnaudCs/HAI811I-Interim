package com.example.interim.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.interim.R;

public class AdminManageUser extends AppCompatActivity {

    Button blockedUsersBtn, signaledUsersBtn, backUserManagerBtn;
    LinearLayout blockedContainer, signaledContainer;

    RecyclerView blockedUsersView, signaledUsersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_user);

        blockedUsersBtn = findViewById(R.id.blockedUsersBtn);
        signaledUsersBtn = findViewById(R.id.signaledUsersBtn);
        blockedContainer = findViewById(R.id.blockedContainer);
        signaledContainer = findViewById(R.id.signaledContainer);
        blockedUsersView = findViewById(R.id.blockedUsersView);
        signaledUsersView = findViewById(R.id.signaledUsersView);
        backUserManagerBtn = findViewById(R.id.backUserManagerBtn);

        blockedUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Changement des couleurs des boutons
                blockedUsersBtn.setBackground(getResources().getDrawable(R.drawable.redbutton));
                blockedUsersBtn.setTextColor(getResources().getColor(R.color.white));

                signaledUsersBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                signaledUsersBtn.setTextColor(getResources().getColor(R.color.grey));

                //Affichage du recyclerView correspondant
                blockedContainer.setVisibility(View.VISIBLE);
                signaledContainer.setVisibility(View.GONE);
            }
        });

        signaledUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Changement des couleurs des boutons
                signaledUsersBtn.setBackground(getResources().getDrawable(R.drawable.redbutton));
                signaledUsersBtn.setTextColor(getResources().getColor(R.color.white));

                blockedUsersBtn.setBackground(getResources().getDrawable(R.drawable.greybutton));
                blockedUsersBtn.setTextColor(getResources().getColor(R.color.grey));

                //Affichage du recyclerView correspondant
                blockedContainer.setVisibility(View.GONE);
                signaledContainer.setVisibility(View.VISIBLE);
            }
        });

        backUserManagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}