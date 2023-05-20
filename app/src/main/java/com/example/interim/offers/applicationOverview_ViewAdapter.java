package com.example.interim.offers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.Admin.ActivityStat;
import com.example.interim.R;
import com.example.interim.discussion.NewMessageConversationActivity;
import com.example.interim.models.Application;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class applicationOverview_ViewAdapter extends RecyclerView.Adapter<applicationOverview_ViewHolder> {
    Context context;

    int status;
    String phone;

    String mail;
    List<Application> applications;
    String applicantName, applicantId;
    Activity mActivity;


    boolean downResume = false; //permet de choisir quel fichier télécharger


    public applicationOverview_ViewAdapter(Context context, List<Application> applications, Activity activity) {
        this.context = context;
        this.applications = applications;
        this.mActivity = activity;
    }


    @NonNull
    @Override
    public applicationOverview_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new applicationOverview_ViewHolder(LayoutInflater.from(context).inflate(R.layout.application_overview_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull applicationOverview_ViewHolder holder, int position) {
        holder.applicantName.setText(applications.get(position).getApplicantName());
        holder.applicantEmail.setText(applications.get(position).getApplicantMail());
        holder.applicantNumber.setText(applications.get(position).getApplicantPhone());
        holder.applicationDate.setText(applications.get(position).getApplicantBirth());
        holder.applicantAdress.setText(applications.get(position).getApplicantAdress());
        holder.applicantId = applications.get(position).getApplicantId();
        holder.appId = applications.get(position).getId();
        status = applications.get(position).getStatus();
        phone = applications.get(position).getApplicantPhone();
        mail = applications.get(position).getApplicantMail();

        if (status == 2){
            holder.messageContainer.setVisibility(View.VISIBLE);
            holder.acceptContainer.setVisibility(View.GONE);
            holder.rejectContainer.setVisibility(View.VISIBLE);
            holder.smsContainer.setVisibility(View.VISIBLE);
            holder.phoneContainer.setVisibility(View.VISIBLE);
        } else if (status == 1) {
            holder.messageContainer.setVisibility(View.VISIBLE);
            holder.acceptContainer.setVisibility(View.VISIBLE);
            holder.rejectContainer.setVisibility(View.GONE);
            holder.smsContainer.setVisibility(View.GONE);
            holder.phoneContainer.setVisibility(View.GONE);
        } else if (status == 0) {
            holder.messageContainer.setVisibility(View.VISIBLE);
            holder.acceptContainer.setVisibility(View.VISIBLE);
            holder.rejectContainer.setVisibility(View.VISIBLE);
            holder.smsContainer.setVisibility(View.GONE);
            holder.phoneContainer.setVisibility(View.GONE);
        }

        holder.downloadResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downResume = true;
                applicantName = applications.get(holder.getAdapterPosition()).getApplicantName();
                applicantId = applications.get(holder.getAdapterPosition()).getApplicantId();
                downloadPdf();
            }
        });

        holder.downloadCoverLetterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downResume = false;
                applicantName = applications.get(holder.getAdapterPosition()).getApplicantName();
                applicantId = applications.get(holder.getAdapterPosition()).getApplicantId();
                downloadPdf();
            }
        });

        holder.smsApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri smsUri = Uri.parse("smsto:" + phone);
                Intent sms = new Intent(Intent.ACTION_SENDTO, smsUri);
                context.startActivity(sms);
            }
        });

        holder.phoneApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri callUri = Uri.parse("tel:" + phone);
                Intent intent = new Intent(Intent.ACTION_DIAL, callUri);
                context.startActivity(intent);
            }
        });

        holder.acceptApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String applicationId = applications.get(holder.getAdapterPosition()).getId();
                updateOfferStatus(applicationId, 2);
            }
        });

        holder.rejectApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the offer ID from the applications list at the given position
                String applicationId = applications.get(holder.getAdapterPosition()).getId();

                // Update the status of the offer in the database to 2
                updateOfferStatus(applicationId, 1);
            }
        });

        holder.messageApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newConversation = new Intent(context, NewMessageConversationActivity.class);
                newConversation.putExtra("mail", mail);
                context.startActivity(newConversation);
                mActivity.finish();
            }
        });

    }

    private void downloadPdf() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference resumeRef = storageRef.child("Resume/" + applicantId + "_Resume.pdf");
        StorageReference coverRef = storageRef.child("CoverLetters/" + applicantId + "_CoverLetter.pdf");

        // Déterminer le répertoire de destination sur le téléphone
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!directory.exists()) {
            // Créer le répertoire s'il n'existe pas
            directory.mkdirs();
        }

        // Télécharger le CV
        if (downResume) {
            File localFile = new File(directory, "resume_" + applicantName + ".pdf");
            resumeRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Le téléchargement du fichier CV est terminé
                            Toast.makeText(context, R.string.successfulFileDownload , Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Une erreur s'est produite lors du téléchargement du fichier CV
                            Toast.makeText(context, R.string.errorRetrievingFiles, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            File localFile = new File(directory,  applicantName + "letter.pdf");
            coverRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Le téléchargement du fichier de lettre de motivation est terminé
                            Toast.makeText(context, R.string.successfulFileDownload, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Une erreur s'est produite lors du téléchargement du fichier de lettre de motivation
                            Toast.makeText(context, R.string.errorRetrievingFiles, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateOfferStatus(String applicationId, int newStatus) {
        // Access the database and update the offer status
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println("APP ID "+ applicationId);
        DocumentReference offerRef = db.collection("Applications").document(applicationId);

        // Fetch the current offer document
        offerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int currentStatus = document.getLong("status").intValue();

                        // Check if the offer already has the new status value
                        if (currentStatus == newStatus) {
                            Toast.makeText(context, "Application already has the new status", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update the offer status in the database
                        offerRef.update("status", newStatus)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Offer status updated successfully
                                        Toast.makeText(context, "Application status updated", Toast.LENGTH_SHORT).show();

                                        // Refresh the layout
                                        notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error occurred while updating offer status
                                        Toast.makeText(context, "Failed to update application status", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    // Error occurred while fetching offer document
                    Toast.makeText(context, "Failed to fetch offer details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return applications.size();
    }
}
