package com.example.interim.Admin;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.models.Notification;
import com.example.interim.models.SignaledOffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class signaledOffers_ViewAdapter extends RecyclerView.Adapter<signaledOffers_ViewHolder> {

    Context context;
    List<SignaledOffer> signaledOffers;
    String offerId, companyName, jobTitle, companyMail, userMail, companyId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public signaledOffers_ViewAdapter(Context context, List<SignaledOffer> signaledOffers) {
        this.context = context;
        this.signaledOffers = signaledOffers;
    }

    public void signaledOffers_ViewHolder(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public signaledOffers_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new signaledOffers_ViewHolder(LayoutInflater.from(context).inflate(R.layout.element_signaledoffer_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull signaledOffers_ViewHolder holder, int position) {
        SignaledOffer signaledOffer = signaledOffers.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(signaledOffer.getSignalDate());
        holder.signaledDate.setText(formattedDate);
        holder.signalReason.setText(signaledOffer.getSignalText());
        offerId = signaledOffer.getApplicationId();
        holder.signalerMail.setText(signaledOffer.getUserMail());
        userMail = signaledOffer.getUserMail();

        db.collection("Offers")
                .document(offerId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                companyId = document.getString("recruiter");

                                db.collection("Pros")
                                        .document(companyId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        companyName = document.getString("companyName");
                                                    } else {
                                                        Log.e("TAG", "Document does not exist");
                                                    }
                                                } else {
                                                    Log.e("TAG", "Error getting document: ", task.getException());
                                                }
                                            }
                                        });

                                jobTitle = document.getString("jobTitle");
                                holder.companyName.setText(companyName);
                                holder.jobTitle.setText(jobTitle);
                            } else {
                                Log.e("TAG", "Document does not exist");
                            }
                        } else {
                            Log.e("TAG", "Error getting document: ", task.getException());
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return signaledOffers.size();
    }

}