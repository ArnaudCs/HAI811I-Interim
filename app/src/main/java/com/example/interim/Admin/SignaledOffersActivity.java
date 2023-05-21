package com.example.interim.Admin;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
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

    Button backSignaledOffersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signaled_offers);
        backSignaledOffersBtn = findViewById(R.id.backSignaledOffersBtn);
        recycler = findViewById(R.id.signaledOffesView);
        signaledOffers = new ArrayList<>();

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

}