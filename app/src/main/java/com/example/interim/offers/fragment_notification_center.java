package com.example.interim.offers;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.discussion.conversation_ViewAdapter;
import com.example.interim.models.Conversation;
import com.example.interim.models.Notification;
import com.example.interim.offers.notifications_ViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class fragment_notification_center extends Fragment {

    private notifications_ViewAdapter mAdapter;
    List<Notification> notifications = new ArrayList<>();
    private Handler mHandler;
    private Runnable mRunnable;

    private boolean refreshing = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    RecyclerView recycler;

    public fragment_notification_center() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_notification_center, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startRefreshing();
        if(mAuth.getCurrentUser() == null){
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            this.getActivity().finish();
            return;
        }

        String userUid = mAuth.getCurrentUser().getUid();

        recycler = view.findViewById(R.id.notificationContainer);

        db.collection("Notifications")
                .whereEqualTo("userId", userUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            notifications = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String text = document.getString("notificationText");
                                String title = document.getString("notificationTitle");
                                String userId = document.getString("userId");
                                Date date = document.getDate("notificationDate");

                                Notification notification = new Notification(text, title, date, userId);
                                notification.setNotificationId(document.getId());
                                notifications.add(notification);
                            }

                            mAdapter = new notifications_ViewAdapter(getContext(), notifications);
                            recycler.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            Log.e("TAG", "Error getting notifications: ", task.getException());
                        }
                    }
                });
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void startRefreshing() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
                    String userUid = mAuth.getCurrentUser().getUid();

                    db.collection("Notifications")
                            .whereEqualTo("userId", userUid)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        notifications.clear();

                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String text = document.getString("notificationText");
                                            String title = document.getString("notificationTitle");
                                            String userId = document.getString("userId");
                                            Date date = document.getDate("notificationDate");

                                            Notification notification = new Notification(text, title, date, userId);
                                            notification.setNotificationId(document.getId());
                                            notifications.add(notification);
                                        }

                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.e("TAG", "Error getting notifications: ", task.getException());
                                    }
                                }
                            });
                }

                // Programmer la prochaine exécution du Runnable après 5 secondes
                mHandler.postDelayed(this, 2000);
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