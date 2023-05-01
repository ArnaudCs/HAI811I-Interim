package com.example.interim.offers;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interim.AppActivity;
import com.example.interim.PDFClass;
import com.example.interim.R;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class fragment_application_form extends Fragment {
    String offerId;

    private String name, url;

    boolean resume = false;

    boolean resumeUploaded = false;
    boolean coverUploaded = false;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    StorageReference storageRef;
    DatabaseReference databaseRefResume, databaseRefCover;
    Button uploadResume, uploadCoverLetter, coverLetterGeneratorBtn;

    TextView coverLetterDisplay, resumeDisplay;

    public fragment_application_form() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_form, container, false);
        // Get the value of myValue from the arguments
        Bundle bundle = getArguments();
        assert bundle != null;
        offerId = bundle.getString("id");

        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button backBtnApplication = view.findViewById(R.id.backBtnApplication);
        Button applyConfirm = view.findViewById(R.id.applyConfirm);
        Button useLastInfos = view.findViewById(R.id.useLastInfos);
        TextInputEditText textApplicantName = view.findViewById(R.id.textApplicantName);
        TextInputEditText textApplicantFirstName = view.findViewById(R.id.textApplicantFirstName);
        TextInputEditText textApplicantPhone = view.findViewById(R.id.textApplicantPhone);
        TextInputEditText textApplicantMail = view.findViewById(R.id.textApplicantMail);
        TextInputEditText textApplicantAdress = view.findViewById(R.id.textApplicantAdress);
        TextInputEditText textApplicantBirth = view.findViewById(R.id.textApplicantBirth);
        coverLetterGeneratorBtn = view.findViewById(R.id.coverLetterGeneratorBtn);

        backBtnApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        uploadCoverLetter = view.findViewById(R.id.uploadCoverLetter);
        uploadResume = view.findViewById(R.id.uploadResume);

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRefResume = FirebaseDatabase.getInstance().getReference("uploadResume");
        databaseRefCover = FirebaseDatabase.getInstance().getReference("uploadCover");

        coverLetterDisplay = view.findViewById(R.id.coverLetterDisplay);
        resumeDisplay = view.findViewById(R.id.resumeDisplay);

        StorageReference storageRefResume = FirebaseStorage.getInstance().getReference().child("Resume/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_Resume.pdf");
        storageRefResume.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String fileName = storageMetadata.getName();
                resumeDisplay.setText(getContext().getResources().getString(R.string.resumeInDatabase));
                resumeUploaded = true;
                // Utiliser le nom du fichier récupéré pour afficher le nom du fichier sur votre interface utilisateur
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                resumeDisplay.setText(getContext().getResources().getString(R.string.resumeNotFound));
            }
        });

        StorageReference storageRefCover = FirebaseStorage.getInstance().getReference().child("CoverLetters/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_CoverLetter.pdf");
        storageRefCover.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String fileName = storageMetadata.getName();
                coverLetterDisplay.setText(getContext().getResources().getString(R.string.coverLetterinDatabase));
                coverUploaded = true;
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

        coverLetterGeneratorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LetterGeneratorActivity.class);
                startActivity(intent);
            }
        });

        useLastInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Firestore instance and reference to the current user's document
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference userRef = db.collection("Users").document(userId);

                // Retrieve the last application data from the user's document
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Object lastApplicationObj = document.get("lastApplication");
                                if (lastApplicationObj != null && lastApplicationObj instanceof Map) {
                                    Map<String, Object> lastApplication = (Map<String, Object>) lastApplicationObj;

                                    // Set the input fields with the last application data
                                    textApplicantName.setText(lastApplication.get("applicantName").toString());
                                    textApplicantFirstName.setText(lastApplication.get("applicantFirstName").toString());
                                    textApplicantPhone.setText(lastApplication.get("applicantPhone").toString());
                                    textApplicantMail.setText(lastApplication.get("applicantMail").toString());
                                    textApplicantAdress.setText(lastApplication.get("applicantAdress").toString());
                                    textApplicantBirth.setText(lastApplication.get("applicantBirth").toString());
                                }
                            }
                        } else {
                            // Handle any exceptions that may occur during the process
                        }
                    }
                });
            }
        });



        applyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(textApplicantName.getText()) &&
                        !TextUtils.isEmpty(textApplicantFirstName.getText()) &&
                        !TextUtils.isEmpty(textApplicantPhone.getText()) &&
                        !TextUtils.isEmpty(textApplicantMail.getText()) &&
                        !TextUtils.isEmpty(textApplicantAdress.getText()) &&
                        !TextUtils.isEmpty(textApplicantBirth.getText()) &&
                        resumeUploaded && coverUploaded){

                    String applicantName = textApplicantName.getText().toString();
                    String applicantFirstName = textApplicantFirstName.getText().toString();
                    String applicantPhone = textApplicantPhone.getText().toString();
                    String applicantMail = textApplicantMail.getText().toString();
                    String applicantAdress = textApplicantAdress.getText().toString();
                    String applicantBirth = textApplicantBirth.getText().toString();

                    // Get Firestore instance and reference to "Applications" collection
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference applicationsRef = db.collection("Applications");

                    // Create new document in "Applications" collection
                    DocumentReference newApplicationRef = applicationsRef.document();

                    // Set data for new document
                    Map<String, Object> data = new HashMap<>();
                    data.put("applicantName", applicantName);
                    data.put("applicantId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    data.put("applicantFirstName", applicantFirstName);
                    data.put("applicantPhone", applicantPhone);
                    data.put("applicantMail", applicantMail);
                    data.put("applicantAdress", applicantAdress);
                    data.put("applicantBirth", applicantBirth);
                    data.put("offerId", offerId);

                    newApplicationRef.set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Document has been added successfully
                                    textApplicantName.setText("");
                                    textApplicantFirstName.setText("");
                                    textApplicantPhone.setText("");
                                    textApplicantMail.setText("");
                                    textApplicantAdress.setText("");
                                    textApplicantBirth.setText("");

                                    // Get reference to the user's document
                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DocumentReference userRef = db.collection("Users").document(userId);

                                    // Add the new application data to the user's document
                                    Map<String, Object> applicationData = new HashMap<>();
                                    applicationData.put("applicantName", applicantName);
                                    applicationData.put("applicantFirstName", applicantFirstName);
                                    applicationData.put("applicantPhone", applicantPhone);
                                    applicationData.put("applicantMail", applicantMail);
                                    applicationData.put("applicantAdress", applicantAdress);
                                    applicationData.put("applicantBirth", applicantBirth);
                                    applicationData.put("offerId", offerId);

                                    userRef.update("lastApplication", applicationData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // User's document has been updated successfully
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle any exceptions that may occur during the process
                                                }
                                            });

                                    Intent applicationCelebration = new Intent(getActivity(), applicationCelebration.class);
                                    startActivity(applicationCelebration);
                                    getActivity().finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any exceptions that may occur during the process
                                }
                            });
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.missingFieldsErroToast), Toast.LENGTH_SHORT).show();
                }

            }
        });
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
                        resumeUploaded = true;
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
                        coverUploaded = true;
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