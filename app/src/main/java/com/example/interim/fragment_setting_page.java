package com.example.interim;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;


public class fragment_setting_page extends Fragment {

    private Button backBtnSettings;

    boolean resume = false;
    boolean downResume = false; //permet de choisir quel fichier télécharger

    boolean admin = false;

    boolean pro = false;

    String name, firstName;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    StorageReference storageRef;
    DatabaseReference databaseRefResume, databaseRefCover;
    Button uploadResume, uploadCoverLetter, adminFormBtn, downloadCoverSettings, downloadResumeSettings;

    TextView coverLetterDisplay, resumeDisplay;
    LinearLayout resumeDisplayBox, coverDisplayBox, spontaneousSwitch;

    public fragment_setting_page() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting_page, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backBtnSettings = view.findViewById(R.id.backBtnSettings);

        backBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        uploadCoverLetter = view.findViewById(R.id.uploadCoverLetter);
        uploadResume = view.findViewById(R.id.uploadResume);
        downloadCoverSettings = view.findViewById(R.id.downloadCoverSettings);
        downloadResumeSettings = view.findViewById(R.id.downloadResumeSettings);

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRefResume = FirebaseDatabase.getInstance().getReference("uploadResume");
        databaseRefCover = FirebaseDatabase.getInstance().getReference("uploadCover");

        coverLetterDisplay = view.findViewById(R.id.coverLetterDisplay);
        resumeDisplay = view.findViewById(R.id.resumeDisplay);

        adminFormBtn = view.findViewById(R.id.adminFormBtn);

        resumeDisplayBox = view.findViewById(R.id.resumeDisplayBox);
        coverDisplayBox = view.findViewById(R.id.coverDisplayBox);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        StorageReference storageRefResume = FirebaseStorage.getInstance().getReference().child("Resume/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_Resume.pdf");
        storageRefResume.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String fileName = storageMetadata.getName();
                resumeDisplay.setText(getContext().getResources().getString(R.string.resumeInDatabase));
                // Utiliser le nom du fichier récupéré pour afficher le nom du fichier sur votre interface utilisateur
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                resumeDisplay.setText(getContext().getResources().getString(R.string.resumeNotFound));
            }
        });

        //Récupération des éventuels fichiers

        StorageReference storageRefCover = FirebaseStorage.getInstance().getReference().child("CoverLetters/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_CoverLetter.pdf");
        storageRefCover.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String fileName = storageMetadata.getName();
                coverLetterDisplay.setText(getContext().getResources().getString(R.string.coverLetterinDatabase));
                // Utiliser le nom du fichier récupéré pour afficher le nom du fichier sur votre interface utilisateur
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                coverLetterDisplay.setText(getContext().getResources().getString(R.string.coverNotFound));
            }
        });

        uploadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resume = true;
                selectFiles();
            }
        });

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);
            DocumentReference proRef = db.collection("Pros").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        admin = document.getBoolean("admin");
                        pro = false;
                        firstName = document.getString("firstName");
                        name = document.getString("name");
                        System.out.println("le user est admin ?" + admin );
                    } else {
                        pro = true;
                        spontaneousSwitch.setVisibility(View.VISIBLE);
                        resumeDisplayBox.setVisibility(View.GONE);
                        coverDisplayBox.setVisibility(View.GONE);
                    }
                }
            });

            proRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        admin = document.getBoolean("admin");
                        System.out.println("le user est admin ?" + admin );
                    } else {
                    }
                }
            });
        }

        adminFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Créer un dialogue personnalisé
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Entrer le code à 4 chiffres");

                // Récupérer le champ d'édition du code
                final EditText codeEditText = (EditText) dialog.findViewById(R.id.codeEditText);

                // Ajouter un bouton pour valider le code
                Button validerButton = (Button) dialog.findViewById(R.id.validerButton);
                validerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code = codeEditText.getText().toString();

                        // Check if the code is valid
                        if (code.equals("3400")) {
                            // Get the current user
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            // Check if the user is connected
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                DocumentReference userRef;

                                if (!pro) {
                                    userRef = db.collection("Users").document(userId);
                                } else {
                                    userRef = db.collection("Pros").document(userId);
                                }

                                userRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Boolean admin = document.getBoolean("admin");
                                            if (admin == null || !admin) {
                                                // Update the admin field of the user
                                                userRef.update("admin", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), getResources().getString(R.string.nowAdmin), Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), getResources().getString(R.string.errogettingId), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getContext(), getResources().getString(R.string.alreadyAdmin), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), getResources().getString(R.string.errogettingId), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), getResources().getString(R.string.errogettingId), Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                });
                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.errogettingId), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        } else {
                            // The code is invalid, display an error message
                            Toast.makeText(getContext(), getResources().getString(R.string.notAdminToast), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                // Afficher le dialogue
                dialog.show();
            }
        });

        uploadCoverLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downResume = false;
                selectFiles();

            }
        });

        downloadCoverSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downResume = true;
                downloadPdf();
            }
        });

        downloadResumeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadPdf();
            }
        });
    }

    private void downloadPdf() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference resumeRef = storageRef.child("Resume/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_Resume.pdf");
        StorageReference coverRef = storageRef.child("CoverLetters/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_CoverLetter.pdf");

        // Déterminer le répertoire de destination sur le téléphone
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!directory.exists()) {
            // Créer le répertoire s'il n'existe pas
            directory.mkdirs();
        }

        // Télécharger le CV
        if (downResume) {
            File localFile = new File(directory, "resume_" + name + "_" + firstName + ".pdf");
            resumeRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Le téléchargement du fichier CV est terminé
                            Toast.makeText(getActivity(), R.string.successfulFileDownload , Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Une erreur s'est produite lors du téléchargement du fichier CV
                            Toast.makeText(getActivity(), R.string.errorRetrievingFiles, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            File localFile = new File(directory,  name + "_" + firstName + "letter.pdf");
            coverRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Le téléchargement du fichier de lettre de motivation est terminé
                            Toast.makeText(getActivity(), R.string.successfulFileDownload, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Une erreur s'est produite lors du téléchargement du fichier de lettre de motivation
                            Toast.makeText(getActivity(), R.string.errorRetrievingFiles, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void selectFiles() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getContext().getResources().getString(R.string.selectPDF)), 12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==12 && resultCode==RESULT_OK && data!=null &&data.getData()!=null){
            if(resume) {
                UploadFiles(data.getData());
            } else {
                UploadFilesCover(data.getData());
            }
        }
    }

    private void UploadFiles(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading ...");
        progressDialog.show();

        StorageReference reference = storageRef.child("Resume/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_Resume.pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri url = uriTask.getResult();

                        String fileName = "Resume_" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                        PDFClass pdf = new PDFClass(fileName, url.toString());

                        databaseRefResume.child(databaseRefResume.push().getKey()).setValue(pdf);

                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.fileUploaded), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        resumeDisplay.setText(getContext().getResources().getString(R.string.resumeInDatabase));
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage(" Uploaded : " + (int)progress+"%");
                    }
                });
    }

    private void UploadFilesCover(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading ...");
        progressDialog.show();

        StorageReference reference = storageRef.child("CoverLetters/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_CoverLetter.pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri url = uriTask.getResult();

                        String fileName = "CoverLetter_" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                        PDFClass pdf = new PDFClass(fileName, url.toString());

                        databaseRefCover.child(databaseRefCover.push().getKey()).setValue(pdf);

                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.fileUploaded), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        coverLetterDisplay.setText(getContext().getResources().getString(R.string.coverLetterinDatabase));
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage(" Uploaded : " + (int)progress+"%");
                    }
                });
    }
}