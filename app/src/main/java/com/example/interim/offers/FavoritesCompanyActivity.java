package com.example.interim.offers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.interim.R;
import com.example.interim.profile.fragment_user_company;

public class FavoritesCompanyActivity extends AppCompatActivity {
    String pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_company);

        Intent intent = getIntent();
        if (intent != null) {
            pro = intent.getStringExtra("pro");
            if (pro != null) {
                this.pro = pro;
            }
        }

        fragment_favorite_offers fragment = new fragment_favorite_offers();

        Bundle args = new Bundle();
        args.putString("pro", this.pro);
        fragment.setArguments(args);

        // Ajouter le fragment au conteneur de fragments
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerView5, fragment)
                .commit();
    }
}