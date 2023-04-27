package com.example.interim.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interim.PDFClass;
import com.example.interim.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class uploadFilesRegistrationUser extends AppCompatActivity {

    boolean resume = false, resumeUploaded = false, coverUploaded = false;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    StorageReference storageRef;
    DatabaseReference databaseRefResume, databaseRefCover;
    Button uploadResume, uploadCoverLetter, nextButton;

    TextView coverLetterDisplay, resumeDisplay;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files_registration_user);

        uploadCoverLetter = findViewById(R.id.uploadCoverLetter);
        uploadResume = findViewById(R.id.uploadResume);
        nextButton = findViewById(R.id.nextButton);

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRefResume = FirebaseDatabase.getInstance().getReference("uploadResume");
        databaseRefCover = FirebaseDatabase.getInstance().getReference("uploadCover");

        coverLetterDisplay = findViewById(R.id.coverLetterDisplay);
        resumeDisplay = findViewById(R.id.resumeDisplay);

        System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid() + "    ---- id du compte ");

        uploadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();
                resume = true;
            }
        });

        uploadCoverLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();
                resume = false;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!resumeUploaded && !coverUploaded) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(uploadFilesRegistrationUser.this);
                    builder.setMessage(getApplicationContext().getResources().getString(R.string.continueWithoutUploading));
                    builder.setPositiveButton(getApplicationContext().getResources().getString(R.string.continueBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            Intent phone = new Intent(uploadFilesRegistrationUser.this, PhoneValidation.class);
                            startActivity(phone);
                        }
                    });
                    builder.setNegativeButton(getApplicationContext().getResources().getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    finish();
                    Intent phone = new Intent(uploadFilesRegistrationUser.this, PhoneValidation.class);
                    startActivity(phone);
                }
            }
        });
    }

    private void selectFiles() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getApplicationContext().getResources().getString(R.string.selectPDF)), 12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==12 && resultCode==RESULT_OK && data!=null &&data.getData()!=null){
            if(resume) {
                UploadFiles(data.getData());
                System.out.println("Choix CV");
            } else {
                UploadFilesCover(data.getData());
                System.out.println("Choix LM");
            }
        }
    }

    private void UploadFiles(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
//        progressDialog.setTitle("Uploading ...");
//        progressDialog.show();

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

                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.fileUploaded), Toast.LENGTH_SHORT).show();

                        //progressDialog.dismiss();
                        resumeDisplay.setText(getApplicationContext().getResources().getString(R.string.resumeInDatabase));
                        resumeUploaded = true;
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        //progressDialog.setMessage(" Uploaded : " + (int)progress+"%");
                    }
                });
    }

    private void UploadFilesCover(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
//        progressDialog.setTitle("Uploading ...");
//        progressDialog.show();

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

                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.fileUploaded), Toast.LENGTH_SHORT).show();

                        //progressDialog.dismiss();
                        coverLetterDisplay.setText(getApplicationContext().getResources().getString(R.string.coverLetterinDatabase));
                        coverUploaded = true;
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        //progressDialog.setMessage(" Uploaded : " + (int)progress+"%");
                    }
                });
    }
}