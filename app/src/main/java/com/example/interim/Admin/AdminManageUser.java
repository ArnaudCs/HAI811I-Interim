package com.example.interim.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.interim.R;
import com.example.interim.models.Blocked;
import com.example.interim.models.Signal;
import com.example.interim.models.SignaledOffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminManageUser extends AppCompatActivity {

    Button blockedUsersBtn, signaledUsersBtn, backUserManagerBtn;
    LinearLayout blockedContainer, signaledContainer;

    private signaledUser_ViewAdapter mAdapter;
    private blockedUser_ViewAdapter bAdapter;
    List<Signal> signaled;
    List<Blocked> blockedlist;
    FirebaseFirestore db = FirebaseFirestore.getInstance();;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        signaled = new ArrayList<>();

        db.collection("Signaled")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Signal signalement = document.toObject(Signal.class);
                                signaled.add(signalement);
                            }

                            mAdapter = new signaledUser_ViewAdapter(AdminManageUser.this, signaled, AdminManageUser.this);
                            signaledUsersView.setAdapter(mAdapter);
                            signaledUsersView.setLayoutManager(new LinearLayoutManager(AdminManageUser.this)); // Add this line
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("TAG", "Error getting notifications: ", task.getException());
                        }

                    }
                });

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