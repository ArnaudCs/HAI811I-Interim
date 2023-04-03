package com.example.interim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        FirebaseFirestore db;
        FirebaseAuth mAuth;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.navbar);
        Menu menu = bottomNav.getMenu();
        menu.clear();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        getMenuInflater().inflate(R.menu.item_menu, menu);
                    } else {
                        getMenuInflater().inflate(R.menu.item_menu_entreprise, menu);
                    }
                }
            });
        }

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            Fragment currentFragment = null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()){
                    case R.id.navMessage:
                        fragment = new fragment_message_menu();
                        break;

                    case R.id.navProfile:
                        fragment = new fragment_profil_user();
                        break;

                    case R.id.navAdd:
                        fragment = new fragment_second_slide();
                        break;
                }

                if (fragment != null) {
                    if (currentFragment != fragment) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.navContainer, fragment).commit();
                        currentFragment = fragment;
                    }
                }

                System.out.println(bottomNav.getItemActiveIndicatorColor());
                System.out.println(bottomNav.getSelectedItemId());

                return true;
            }
        });
    }
}