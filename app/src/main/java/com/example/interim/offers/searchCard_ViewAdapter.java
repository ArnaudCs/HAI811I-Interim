package com.example.interim.offers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.models.Offer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class searchCard_ViewAdapter extends RecyclerView.Adapter<searchCard_ViewHolder> {

    Context context;
    List<Offer> offers;

    boolean pro = false;

    public searchCard_ViewAdapter(Context context, List<Offer> offers) {
        this.context = context;
        this.offers = offers;
    }

    @NonNull
    @Override
    public searchCard_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new searchCard_ViewHolder(LayoutInflater.from(context).inflate(R.layout.searchcard_element_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull searchCard_ViewHolder holder, int position) {
        holder.jobTitle.setText(offers.get(position).getJobTitle());
        holder.companyName.setText(offers.get(position).getCompanyName());
        holder.jobCategory.setText(offers.get(position).getCategory());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String startDate = formatter.format(offers.get(position).getStartDate());
        String endDate = formatter.format(offers.get(position).getEndDate());
        holder.jobDate.setText(context.getString(R.string.dateIndicationsStart) + startDate + context.getString(R.string.dateIndicationsEnd) + endDate);
        holder.jobUrl.setText(offers.get(position).getUrl());
        holder.jobSalary.setText(String.valueOf(offers.get(position).getSalaryMax()) + "€");
        holder.jobLocation.setText(offers.get(position).getLocation());
        holder.postDate.setText(offers.get(position).getPostDate().toString());
        holder.jobKeywords.setText(offers.get(position).getKeywords());

        FirebaseFirestore db;
        FirebaseAuth mAuth;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        pro = false;
                        List<String> likedOffers = (List<String>) document.get("likedOffers");
                        if (likedOffers != null && likedOffers.contains(offers.get(position).getId())) {
                            holder.likeInit.setVisibility(View.GONE);
                            holder.likeBtn.setVisibility(View.VISIBLE);
                            holder.likeBtn.setAnimation(R.raw.like);
                            holder.likeBtn.playAnimation();
                            holder.liked = true;
                        } else {
                            holder.likeInit.setVisibility(View.VISIBLE);
                            holder.likeBtn.setVisibility(View.GONE);
                            holder.liked = false;
                        }
                    } else {
                        pro = true;
                        holder.applyBtn.setText(context.getString(R.string.seeMore));
                    }
                }
            });
        } else {
            holder.likeInit.setVisibility(View.VISIBLE);
            holder.likeBtn.setVisibility(View.GONE);
            holder.liked = false;
        }

        holder.likeInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.liked) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if (mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DocumentReference userRef = db.collection("Users").document(userId);
                        userRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    List<String> likedOffers = (List<String>) document.get("likedOffers");
                                    if (likedOffers == null) {
                                        likedOffers = new ArrayList<>();
                                    }
                                    likedOffers.add(offers.get(position).getId());
                                    userRef.update("likedOffers", likedOffers);
                                }
                            }
                        });
                        holder.likeInit.setVisibility(View.GONE);
                        holder.likeBtn.setVisibility(View.VISIBLE);
                        holder.likeBtn.setAnimation(R.raw.like);
                        holder.likeBtn.playAnimation();
                        holder.liked = !holder.liked;
                    }
                    else {
                        Intent loginIntent = new Intent(context, MainActivity.class);
                        context.startActivity(loginIntent);
                    }
                }
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.liked){
                    // Save the offer to the user's document
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if (mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DocumentReference userRef = db.collection("Users").document(userId);
                        userRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    List<String> likedOffers = (List<String>) document.get("likedOffers");
                                    if (likedOffers == null) {
                                        likedOffers = new ArrayList<>();
                                    }
                                    likedOffers.add(offers.get(position).getId());
                                    userRef.update("likedOffers", likedOffers);
                                }
                            }
                        });
                        holder.likeBtn.setAnimation(R.raw.like);
                        holder.likeBtn.playAnimation();
                        holder.liked = !holder.liked;
                    }
                    else {
                        Intent loginIntent = new Intent(context, MainActivity.class);
                        context.startActivity(loginIntent);
                    }

                } else {
                    // Remove the offer from the user's document
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if (mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DocumentReference userRef = db.collection("Users").document(userId);
                        userRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    List<String> likedOffers = (List<String>) document.get("likedOffers");
                                    if (likedOffers != null) {
                                        likedOffers.remove(offers.get(position).getId());
                                        userRef.update("likedOffers", likedOffers);
                                    }
                                }
                            }
                        });
                    }
                    holder.likeBtn.setAnimation(R.raw.dislike);
                    holder.likeBtn.playAnimation();
                    holder.liked = !holder.liked;
                }
            }
        });



        holder.applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussion = new Intent(context, MissionsActivity.class);
                context.startActivity(discussion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }
}
