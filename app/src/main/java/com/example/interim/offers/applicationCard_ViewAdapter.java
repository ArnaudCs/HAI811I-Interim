package com.example.interim.offers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class applicationCard_ViewAdapter extends RecyclerView.Adapter<applicationCard_ViewHolder> {

    Context context;
    List<Offer> offers;

    String firstName;
    String name;

    String companyNameText;
    String contactNameText;

    boolean pro = false;

    public applicationCard_ViewAdapter(Context context, List<Offer> offers) {
        this.context = context;
        this.offers = offers;
    }

    @NonNull
    @Override
    public applicationCard_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new applicationCard_ViewHolder(LayoutInflater.from(context).inflate(R.layout.application_element_cards,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull applicationCard_ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.jobId = offers.get(position).getId();
        holder.jobTitle.setText(offers.get(position).getJobTitle());
        holder.companyName.setText(offers.get(position).getCompanyName());
        holder.jobCategory.setText(offers.get(position).getCategory());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String startDate = formatter.format(offers.get(position).getStartDate());
        String endDate = formatter.format(offers.get(position).getEndDate());
        holder.jobDate.setText(context.getString(R.string.dateIndicationsStart) + startDate + context.getString(R.string.dateIndicationsEnd) + endDate);
        holder.jobSalary.setText(String.valueOf(offers.get(position).getSalaryMax()) + "€");
        holder.jobLocation.setText(offers.get(position).getLocation());
        //holder.postDate.setText(offers.get(position).getPostDate().toString());

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
                        firstName = document.getString("firstName");
                        name = document.getString("name");
                    } else {
                        pro = true;
                        DocumentReference userRef2 = db.collection("Pros").document(userId);
                        userRef2.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                DocumentSnapshot document2 = task2.getResult();
                                if (document2.exists()) {
                                    name = document2.getString("name");
                                }
                            }
                        });
                    }
                }
            });
        } else {
        }


        holder.seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discussion = new Intent(context, MissionsActivity.class);
                discussion.putExtra("id",holder.jobId);
                context.startActivity(discussion);

            }
        });
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }
}