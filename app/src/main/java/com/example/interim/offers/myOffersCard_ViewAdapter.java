package com.example.interim.offers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class myOffersCard_ViewAdapter extends RecyclerView.Adapter<myOffersCard_ViewHolder> {

    Context context;
    List<Offer> offers;

    String firstName;
    String name;

    String companyNameText;
    String contactNameText;

    boolean pro = false;

    public myOffersCard_ViewAdapter(Context context, List<Offer> offers) {
        this.context = context;
        this.offers = offers;
    }

    @NonNull
    @Override
    public myOffersCard_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new myOffersCard_ViewHolder(LayoutInflater.from(context).inflate(R.layout.myoffers_card_element,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull myOffersCard_ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.jobId = offers.get(position).getId();
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
                        firstName = document.getString("firstName");
                        name = document.getString("name");
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
                        DocumentReference userRef2 = db.collection("Pros").document(userId);
                        userRef2.get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                DocumentSnapshot document2 = task2.getResult();
                                if (document2.exists()) {
                                    name = document2.getString("name");
                                    List<String> likedOffers = (List<String>) document2.get("likedOffers");
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
                                }
                            }
                        });
                    }
                }
            });
        } else {
            holder.likeInit.setVisibility(View.VISIBLE);
            holder.likeBtn.setVisibility(View.GONE);
            holder.liked = false;
        }


        holder.deleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jobId = holder.jobId;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete this offer?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Delete the offer from the Offers collection
                                db.collection("Offers").document(jobId)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Offer deleted successfully, now delete the associated applications

                                                // Delete the applications from the Applications collection
                                                db.collection("Applications")
                                                        .whereEqualTo("offerId", jobId)
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                List<DocumentSnapshot> applicationSnapshots = queryDocumentSnapshots.getDocuments();
                                                                List<Task<Void>> deleteTasks = new ArrayList<>();

                                                                // Create a batch delete operation for the applications
                                                                WriteBatch batch = db.batch();
                                                                for (DocumentSnapshot applicationSnapshot : applicationSnapshots) {
                                                                    batch.delete(applicationSnapshot.getReference());
                                                                }

                                                                // Execute the batch delete operation
                                                                batch.commit()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                notifyDataSetChanged();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // Failed to delete applications
                                                                                // Handle the failure scenario if needed
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Failed to fetch applications
                                                                // Handle the failure scenario if needed
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Failed to delete offer
                                                // Handle the failure scenario if needed
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Cancelled, do nothing
                            }
                        })
                        .show();
            }
        });



        if(mAuth.getCurrentUser() != null){
            db.collection("Pros").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    companyNameText = documentSnapshot.getString("companyName");
                    contactNameText = documentSnapshot.getString("name");
                }
            });
        }

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pro){
                    String shareMessage = context.getString(R.string.shareofferHook) +
                            contactNameText + " " + context.getString(R.string.fromCompanyJoinDisplay) + " " + companyNameText + ". " +
                            context.getString(R.string.shareofferTitle) + "\n" + "" + "\n" +
                            context.getString(R.string.shareOfferDetails) + "\n" + "" + "\n" +
                            context.getString(R.string.offerTitle) + " : " + holder.jobTitle.getText().toString() + "\n" +
                            context.getString(R.string.companyName) + " : " + holder.companyName.getText().toString() + "\n" +
                            context.getString(R.string.salaryPrice) + " : " + holder.jobSalary.getText().toString() + "\n" +
                            context.getString(R.string.dateDisplay) + ": " + holder.jobDate.getText().toString() + "\n" +
                            context.getString(R.string.placeInput) + " : " + holder.jobLocation.getText().toString() + "\n" +
                            context.getString(R.string.categoryOfferDisplay) + " : " + holder.jobCategory.getText().toString() + "\n" + "" + "\n" +
                            context.getString(R.string.shareofferActionCall);

                    Intent ShareIntent = new Intent();
                    ShareIntent.setAction(Intent.ACTION_SEND);
                    ShareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    ShareIntent.setType("text/plain");
                    ShareIntent = Intent.createChooser(ShareIntent, "Share with : ");
                    context.startActivity(ShareIntent);
                } else if (!pro) {
                    String shareMessage = context.getString(R.string.shareofferHook) +
                            firstName + " " + name +  ". " +
                            context.getString(R.string.shareofferTitle) + "\n" + "" + "\n" +
                            context.getString(R.string.shareOfferDetails) + "\n" + "" + "\n" +
                            context.getString(R.string.offerTitle) + " : " + holder.jobTitle.getText().toString() + "\n" +
                            context.getString(R.string.companyName) + " : " + holder.companyName.getText().toString() + "\n" +
                            context.getString(R.string.salaryPrice) + " : " + holder.jobSalary.getText().toString() + "\n" +
                            context.getString(R.string.dateDisplay) + ": " + holder.jobDate.getText().toString() + "\n" +
                            context.getString(R.string.placeInput) + " : " + holder.jobLocation + "\n" +
                            context.getString(R.string.categoryOfferDisplay) + " : " + holder.jobCategory + "\n" + "" + "\n" +
                            context.getString(R.string.shareofferActionCall);

                    Intent ShareIntent = new Intent();
                    ShareIntent.setAction(Intent.ACTION_SEND);
                    ShareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    ShareIntent.setType("text/plain");
                    ShareIntent = Intent.createChooser(ShareIntent, "Share with : ");
                    context.startActivity(ShareIntent);
                } else {
                    Toast.makeText(context, context.getString(R.string.connectionWarning), Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.likeInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.liked) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if (mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DocumentReference  userRef = db.collection("Users").document(userId);
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
                                else {
                                    DocumentReference userRef2 = db.collection("Pros").document(userId);
                                    userRef2.get().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentSnapshot document2 = task2.getResult();
                                            if (document2.exists()) {
                                                List<String> likedOffers = (List<String>) document2.get("likedOffers");
                                                if (likedOffers == null) {
                                                    likedOffers = new ArrayList<>();
                                                }
                                                likedOffers.add(offers.get(position).getId());
                                                userRef2.update("likedOffers", likedOffers);
                                            }
                                        }
                                    });
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
                                else {
                                    DocumentReference userRef2 = db.collection("Pros").document(userId);
                                    userRef2.get().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentSnapshot document2 = task2.getResult();
                                            if (document2.exists()) {
                                                List<String> likedOffers = (List<String>) document2.get("likedOffers");
                                                if (likedOffers == null) {
                                                    likedOffers = new ArrayList<>();
                                                }
                                                likedOffers.add(offers.get(position).getId());
                                                userRef2.update("likedOffers", likedOffers);
                                            }
                                        }
                                    });
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
                        DocumentReference userRef =  db.collection("Users").document(userId);
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
                                else {
                                    DocumentReference userRef2 =  db.collection("Pros").document(userId);
                                    userRef2.get().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentSnapshot document2 = task2.getResult();
                                            if (document2.exists()) {
                                                List<String> likedOffers = (List<String>) document2.get("likedOffers");
                                                if (likedOffers != null) {
                                                    likedOffers.remove(offers.get(position).getId());
                                                    userRef2.update("likedOffers", likedOffers);
                                                }
                                            }

                                        }
                                    });
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



        holder.manageAplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent manageApplications = new Intent(context, ApplicationManagerActivity.class);
                manageApplications.putExtra("id",holder.jobId);
                context.startActivity(manageApplications);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }
}
