package com.example.interim.Admin;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;
import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.discussion.NewMessageConversationActivity;
import com.example.interim.models.Notification;
import com.example.interim.models.SignaledOffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    Activity mActivity;

    List<SignaledOffer> signaledOffers;
    String offerId, companyName, jobTitle, companyMail, userMail, companyId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public signaledOffers_ViewAdapter(Context context, List<SignaledOffer> signaledOffers, Activity activity) {
        this.context = context;
        this.signaledOffers = signaledOffers;
        this.mActivity = activity;
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
                                                        holder.companyName.setText(companyName);
                                                        companyMail = document.getString("email");
                                                    } else {
                                                        Log.e("TAG", "Document does not exist");
                                                    }
                                                } else {
                                                    Log.e("TAG", "Error getting document: ", task.getException());
                                                }
                                            }
                                        });

                                jobTitle = document.getString("jobTitle");
                                holder.jobTitle.setText(jobTitle);
                            } else {
                                Log.e("TAG", "Document does not exist");
                            }
                        } else {
                            Log.e("TAG", "Error getting document: ", task.getException());
                        }
                    }
                });

        holder.sendWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String warningMessage = context.getString(R.string.warningOfferTitle) + jobTitle + context.getString(R.string.warningOfferText);
                Intent newConversation = new Intent(context, NewMessageConversationActivity.class);
                newConversation.putExtra("mail", companyMail);
                newConversation.putExtra("message", warningMessage);
                context.startActivity(newConversation);
                mActivity.finish();
            }
        });

        holder.messageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newConversation = new Intent(context, NewMessageConversationActivity.class);
                newConversation.putExtra("mail", companyMail);
                context.startActivity(newConversation);
                mActivity.finish();
            }
        });

        holder.deleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(context.getString(R.string.adminDeleteOfferTitle));
                builder.setMessage(context.getString(R.string.deleteOfferAdmin));

                builder.setPositiveButton(context.getString(R.string.validateButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        db.collection("Offers")
                                .document(offerId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        db.collection("Applications")
                                                .whereEqualTo("offerId", offerId)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                                        List<DocumentSnapshot> applicationDocs = querySnapshot.getDocuments();
                                                        for (DocumentSnapshot applicationDoc : applicationDocs) {
                                                            applicationDoc.getReference().delete();
                                                        }
                                                    }
                                                });
                                        db.collection("SignaledOffers")
                                                .whereEqualTo("offerId", offerId)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                                        List<DocumentSnapshot> applicationDocs = querySnapshot.getDocuments();
                                                        for (DocumentSnapshot applicationDoc : applicationDocs) {
                                                            applicationDoc.getReference().delete();
                                                        }
                                                    }
                                                });
                                        Log.e(TAG,"Offer deleted");

                                    }
                                });
                    }
                });

                builder.setNegativeButton(context.getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return signaledOffers.size();
    }

}