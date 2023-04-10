package com.example.interim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppActivity extends AppCompatActivity {

    private boolean pro = false;

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
        FragmentContainerView navContainer = findViewById(R.id.navContainer);
        menu.clear();

        pro = false;
        getMenuInflater().inflate(R.menu.item_menu, menu);
        bottomNav.setSelectedItemId(R.id.navHome);

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        pro = false;
                        menu.clear();
                        getMenuInflater().inflate(R.menu.item_menu, menu);
                        bottomNav.setSelectedItemId(R.id.navHome);
                    } else {
                        pro = true;
                        menu.clear();
                        getMenuInflater().inflate(R.menu.item_menu_entreprise, menu);
                        bottomNav.setSelectedItemId(R.id.navSearch);
                    }
                }
            });
        }

        ViewGroup rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int previousHeight = 0;

            @Override
            public void onGlobalLayout() {
                int newHeight = rootView.getHeight();
                if (previousHeight != 0 && previousHeight > newHeight) {
                    bottomNav.setVisibility(View.GONE);
                } else if (previousHeight != 0 && previousHeight < newHeight) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomNav.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                }
                previousHeight = newHeight;
            }
        });

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
                        if(pro){
                            fragment = new fragment_user_company();
                        } else {
                            fragment = new fragment_profil_user();
                        }
                        break;

                    case R.id.navSearch:
                        fragment = new fragment_search_page();
                        break;

                    case R.id.navAdd:
                        fragment = new fragment_post_offers();
                        break;

                    case R.id.navHome:
                        fragment = new fragment_search_page();
                        break;

                    case R.id.navNotification:
                        fragment = new fragment_notification_center();
                        break;

                    case R.id.navFavorite:
                        fragment = new fragment_loading_screen();
                        break;
                }

                if (fragment != null) {
                    if (currentFragment != fragment) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.navContainer, fragment).commit();
                        currentFragment = fragment;
                    }
                }
                return true;
            }
        });
    }
}