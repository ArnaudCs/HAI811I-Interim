package com.example.interim.offers;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.models.Application;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class applicationOverview_ViewAdapter extends RecyclerView.Adapter<applicationOverview_ViewHolder> {
    Context context;
    List<Application> applications;
    String applicantName, applicantId;

    boolean downResume = false; //permet de choisir quel fichier télécharger


    public applicationOverview_ViewAdapter(Context context, List<Application> applications) {
        this.context = context;
        this.applications = applications;
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
//        holder.jobTitle.setText(applications.get(position).getOffer().getJobTitle());
        holder.applicationDate.setText(applications.get(position).getApplicantBirth());
        holder.applicantAdress.setText(applications.get(position).getApplicantAdress());
        holder.applicantId = applications.get(position).getApplicantId();
        holder.appId = applications.get(position).getId();

        holder.downloadResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downResume = true;
                applicantName = applications.get(position).getApplicantName();
                applicantId = applications.get(position).getApplicantId();
                downloadPdf();
            }
        });

        holder.downloadCoverLetterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downResume = false;
                applicantName = applications.get(position).getApplicantName();
                applicantId = applications.get(position).getApplicantId();
                downloadPdf();
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

    @Override
    public int getItemCount() {
        return applications.size();
    }
}
