package com.example.interim.Admin;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.interim.R;
import com.example.interim.models.Notification;
import com.example.interim.models.SignaledOffer;
import com.example.interim.offers.notifications_ViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignaledOffersActivity extends AppCompatActivity {

    private signaledOffers_ViewAdapter mAdapter;
    List<SignaledOffer> signaledOffers;
    FirebaseFirestore db = FirebaseFirestore.getInstance();;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    RecyclerView recycler;

    private Runnable mRunnable;
    private boolean refreshing = false;

    private Handler mHandler;

    Button backSignaledOffersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            setTheme(R.style.ThemeDark_Interim);
        else
            setTheme(R.style.Theme_Interim);
        setContentView(R.layout.activity_signaled_offers);
        backSignaledOffersBtn = findViewById(R.id.backSignaledOffersBtn);
        recycler = findViewById(R.id.signaledOffesView);
        signaledOffers = new ArrayList<>();
        startRefreshing();

        backSignaledOffersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recycler = findViewById(R.id.signaledOffesView);
        db.collection("SignaledOffers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SignaledOffer signOffer = document.toObject(SignaledOffer.class);
                                signaledOffers.add(signOffer);
                            }

                            mAdapter = new signaledOffers_ViewAdapter(SignaledOffersActivity.this, signaledOffers, SignaledOffersActivity.this);
                            recycler.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("TAG", "Error getting notifications: ", task.getException());
                        }

                    }
                });

        recycler.setLayoutManager(new LinearLayoutManager(SignaledOffersActivity.this));
    }

    private void startRefreshing() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                // Récupérer les offres signalées dans la table "SignaledOffers"
                db.collection("SignaledOffers")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    signaledOffers.clear(); // Effacer la liste actuelle d'offres signalées
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        SignaledOffer signOffer = document.toObject(SignaledOffer.class);
                                        signaledOffers.add(signOffer);
                                    }
                                    mAdapter.notifyDataSetChanged(); // Mettre à jour le RecyclerView avec les nouvelles offres signalées
                                } else {
                                    Log.e("TAG", "Error getting signaled offers: ", task.getException());
                                }
                            }
                        });

                mHandler.postDelayed(this, 3000); // Programmer la prochaine exécution du Runnable après 3 secondes
            }
        };

        refreshing = true;
        mHandler.post(mRunnable);
    }

    @Override
    public void onPause() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            super.onPause();
            refreshing = false;
            System.out.println("Arrêt du refresh des conversations");
            mHandler.removeCallbacks(mRunnable);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            super.onResume();
            refreshing = true;
            System.out.println("Reprise du refresh des conversations");
            mHandler.post(mRunnable);
        }
        super.onResume();
    }

}