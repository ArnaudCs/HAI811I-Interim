package com.example.interim.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.interim.R;

public class CompanyProfileViewer extends AppCompatActivity {

    String recruiterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile_viewer);

        Intent intent = getIntent();
        if (intent != null) {
            String userId = intent.getStringExtra("userId");
            if (userId != null) {
                recruiterId = userId;
                System.out.println("Récupération du recruiter depuis le viewer ---------------------------------" + recruiterId);

            }
        }

        fragment_user_company fragment = new fragment_user_company();

        Bundle args = new Bundle();
        args.putString("recruiterId", recruiterId);
        System.out.println("Envoi du user id depuis l'activité viewer ---------------------------------" + recruiterId);
        fragment.setArguments(args);

        // Ajouter le fragment au conteneur de fragments
        getSupportFragmentManager().beginTransaction()
                .add(R.id.companyProfileViewer, fragment)
                .commit();
    }
}