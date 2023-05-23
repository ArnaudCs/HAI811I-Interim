package com.example.interim.offers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class applicationCard_ViewAdapter extends RecyclerView.Adapter<applicationCard_ViewHolder> {

    Context context;
    List<Offer> offers;
    HashMap<Offer, Integer> offersMap;
    List<String> applicationIds;
    String firstName;

    String name;

    String companyNameText;
    String contactNameText;

    boolean pro = false;


    public applicationCard_ViewAdapter(Context context, HashMap<Offer, Integer> offersMap, List<String> appIds) {
        this.context = context;
        this.offersMap = offersMap;
        applicationIds = appIds;
        offers = new ArrayList<Offer>(offersMap.keySet());
    }

    @NonNull
    @Override
    public applicationCard_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new applicationCard_ViewHolder(LayoutInflater.from(context).inflate(R.layout.application_element_cards,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull applicationCard_ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.jobId = offers.get(position).getId();
        holder.applicationId = applicationIds.get(position);
        System.out.println("ID " + holder.applicationId);
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

        if (offersMap.get(offersMap.keySet().toArray()[position]) == Integer.valueOf(2)) {
            holder.statusIcon.setImageResource(R.drawable.baseline_check_circle_24);
            holder.statusIcon.setColorFilter(Color.GREEN);
        } else if (offersMap.get(offersMap.keySet().toArray()[position]) == Integer.valueOf(1)) {
            holder.statusIcon.setImageResource(R.drawable.baseline_cancel_24);
            holder.statusIcon.setColorFilter(Color.RED);
        }
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


        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.deleteApplicationTitle));
                builder.setMessage(context.getString(R.string.deleteApplication));
                builder.setPositiveButton(context.getString(R.string.nextBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String applicationId = holder.applicationId;

                        // Delete the application from the Applications collection
                        db.collection("Applications").document(applicationId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Application deleted successfully
                                        // Handle any additional logic or UI updates after successful deletion
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to delete application
                                        // Handle the failure scenario if needed
                                    }
                                });
                    }
                });
                builder.setNegativeButton(context.getString(R.string.cancelBtn), null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }


        @Override
    public int getItemCount() {
        return offers.size();
    }



}