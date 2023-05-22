package com.example.interim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.interim.Utils.fragment_notification_center;
import com.example.interim.discussion.fragment_message_menu;
import com.example.interim.models.Offer;
import com.example.interim.offers.fragment_favorite_offers;
import com.example.interim.offers.fragment_my_offers_company;
import com.example.interim.offers.fragment_post_offers;
import com.example.interim.offers.fragment_search_page;
import com.example.interim.profile.fragment_profil_user;
import com.example.interim.profile.fragment_user_company;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

        Intent intent = getIntent();
        if(intent.hasExtra("offerFilters")) {
            Offer offerForFilters = (Offer) intent.getSerializableExtra("offerFilters");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new fragment_search_page();
            Bundle bundle = new Bundle();
            bundle.putSerializable("offerForFilters", offerForFilters);
            fragment.setArguments(bundle);
            fragmentTransaction.add(R.id.navContainer, fragment);
            fragmentTransaction.commit();
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.navbar);
        Menu menu = bottomNav.getMenu();
        FragmentContainerView navContainer = findViewById(R.id.navContainer);
        pro = false;

        menu.clear();
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
                        fragment = new fragment_favorite_offers();
                        break;

                    case R.id.navOffers:
                        fragment = new fragment_my_offers_company();
                        break;
                }

                if (fragment != null) {
                    if (currentFragment != fragment) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.navContainer, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        currentFragment = fragment;
                    }
                }
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Firebase messages";
            String channelId = "fcm_channel";
            String channelDescription = "Channel for receiving firebase cloud messages";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}