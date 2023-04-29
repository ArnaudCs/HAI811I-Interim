package com.example.interim.profile;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.interim.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class fragment_profile_edition extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    Upload upload;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    ImageView profilePic;
    private Uri mImageUri;
    boolean pro = false;

    private ProgressBar progressBarFile;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask muploadTask;

    public fragment_profile_edition() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_profile_edition_company, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText textNameModification = view.findViewById(R.id.text1NameModification);
        TextInputEditText textFirstNameModification = view.findViewById(R.id.textUserFirstnameModification);
        TextInputEditText textCompanyNameModification = view.findViewById(R.id.textCompanyNameModification);
        TextInputEditText textNationalNumberModification = view.findViewById(R.id.textNationalNumberModification);
        TextInputEditText textMailModification = view.findViewById(R.id.text1MailModification);
        TextInputEditText textNumberModification = view.findViewById(R.id.textContact1NumberModification);
        TextInputEditText textCompanyAdressModification = view.findViewById(R.id.textCompanyAdressModification);
        TextInputEditText textWebsiteModification = view.findViewById(R.id.textWebsiteModification);
        TextInputEditText textBirthdateModification = view.findViewById(R.id.textBirthdateModification);
        TextInputEditText textUserPhoneNumberModification = view.findViewById(R.id.textSimpleUserNumberModification);
        TextInputEditText textUserMailModification = view.findViewById(R.id.textMailAdressModification);
        TextInputEditText textUserNameModification = view.findViewById(R.id.textSimpleUserNameModification);
        TextInputEditText textContact2Name = view.findViewById(R.id.text2NameModification);
        TextInputEditText textContact2Number = view.findViewById(R.id.textContact2NumberModification);
        TextInputEditText textContact2Email = view.findViewById(R.id.textContact2MailAdressModification);
        TextInputEditText textServiceModification = view.findViewById(R.id.textServiceModification);
        TextInputEditText textSubserviceModification = view.findViewById(R.id.textSubServiceModification);


        Button backBtnProfileModification = view.findViewById(R.id.backBtnProfileModification);
        Button choosePic = view.findViewById(R.id.choosePicBtn);
        Button uploadFile = view.findViewById(R.id.uploadBtn);
        Button editProfile = view.findViewById(R.id.createAccount);
        profilePic = view.findViewById(R.id.imageVisualizer);
        progressBarFile = view.findViewById(R.id.progressBarPic);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        TextInputLayout layoutCompanyModification = view.findViewById(R.id.layoutCompanyModification);
        TextInputLayout layoutRegistrationNumberModification = view.findViewById(R.id.layoutRegistrationNumberModification);
        TextInputLayout layoutCompanyAdressModification = view.findViewById(R.id.layoutCompanyAdressModification);
        TextInputLayout layoutCompanyWebsiteModification = view.findViewById(R.id.layoutCompanyWebsiteModification);
        LinearLayout contactModificationDivider = view.findViewById(R.id.contactModificationDivider);
        LinearLayout layoutContact1MailAdressModification = view.findViewById(R.id.layoutContact1MailAdressModification);
        LinearLayout layoutContact1NameModification = view.findViewById(R.id.layoutContact1NameModification);
        TextInputLayout layoutContact2NameModification = view.findViewById(R.id.layoutContact2NameModification);
        TextInputLayout layoutContact2MailAdressModification = view.findViewById(R.id.layoutContact2MailAdressModification);
        TextInputLayout layoutContact2NumberModification = view.findViewById(R.id.layoutContact2NumberModification);
        TextInputLayout layoutServiceModification = view.findViewById(R.id.layoutServiceModification);
        TextInputLayout layoutSubServiceModification = view.findViewById(R.id.layoutSubServiceModification);
        TextInputLayout layoutContact1NumberModification = view.findViewById(R.id.layoutContact1NumberModification);

        TextInputLayout layoutBirthdateModification = view.findViewById(R.id.layoutSimpleUserBirthdateModification);
        TextInputLayout layoutUserFirstnameModification = view.findViewById(R.id.layoutSimpleUserFirstnameModification);
        TextInputLayout layoutUserNameModification = view.findViewById(R.id.layoutSimpleUserNameModification);
        TextInputLayout layoutMailAdressModification = view.findViewById(R.id.layoutMailAdressModification);
        TextInputLayout layoutNumberModification = view.findViewById(R.id.layoutSimpleUserNumberModification);

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        pro = false;
                        layoutCompanyModification.setVisibility(View.GONE);
                        layoutRegistrationNumberModification.setVisibility(View.GONE);
                        layoutCompanyAdressModification.setVisibility(View.GONE);
                        layoutCompanyWebsiteModification.setVisibility(View.GONE);
                        contactModificationDivider.setVisibility(View.GONE);
                        layoutSubServiceModification.setVisibility(View.GONE);
                        layoutContact2NameModification.setVisibility(View.GONE);
                        layoutContact2MailAdressModification.setVisibility(View.GONE);
                        layoutContact2NumberModification.setVisibility(View.GONE);
                        layoutServiceModification.setVisibility(View.GONE);
                        layoutSubServiceModification.setVisibility(View.GONE);
                        layoutContact1NumberModification.setVisibility(View.GONE);
                        layoutContact1MailAdressModification.setVisibility(View.GONE);
                        layoutContact1NameModification.setVisibility(View.GONE);


                        layoutBirthdateModification.setVisibility(View.VISIBLE);
                        layoutUserFirstnameModification.setVisibility(View.VISIBLE);
                        layoutUserNameModification.setVisibility(View.VISIBLE);
                        layoutMailAdressModification.setVisibility(View.VISIBLE);
                        layoutNumberModification.setVisibility(View.VISIBLE);
                        String name = document.getString("name");
                        String firstname = document.getString("firstName");
                        String email = document.getString("email");
                        String phoneNumber = document.getString("phoneNumber");
                        String birthdate = document.getString("birthdate");

                        textUserNameModification.setText(name);
                        textBirthdateModification.setText(birthdate);
                        textFirstNameModification.setText(firstname);
                        textUserMailModification.setText(email);
                        textUserPhoneNumberModification.setText(phoneNumber);


                    } else {
                        pro = true;
                        DocumentReference profRef = db.collection("Pros").document(userId);
                        profRef.get().addOnCompleteListener(taskPro -> {
                            if (taskPro.isSuccessful()) {
                                DocumentSnapshot documentPro = taskPro.getResult();
                                if (documentPro.exists()) {
                                    System.out.println(documentPro.toString());
                                    String companyName = documentPro.getString("companyName");
                                    String nationalNumber = documentPro.getString("nationalNumber");
                                    String contactName = documentPro.getString("name");
                                    String email = documentPro.getString("email");
                                    String contactNumber = documentPro.getString("phoneNumber");
                                    String companyAddress = documentPro.getString("companyAddress");
                                    String website = documentPro.getString("website");
                                    String contact2Name = documentPro.getString("contact2Name");
                                    String contact2Phone = documentPro.getString("contact2Phone");
                                    String contact2Email = documentPro.getString("contact2Email");
                                    String service = documentPro.getString("service");
                                    String subservice = documentPro.getString("subservice");

                                    textCompanyNameModification.setText(companyName);
                                    textNationalNumberModification.setText(nationalNumber);
                                    textNameModification.setText(contactName);
                                    textMailModification.setText(email);
                                    textNumberModification.setText(contactNumber);
                                    textCompanyAdressModification.setText(companyAddress);
                                    textWebsiteModification.setText(website);
                                    textContact2Name.setText(contact2Name);
                                    textContact2Email.setText(contact2Email);
                                    textContact2Number.setText(contact2Phone);
                                    textServiceModification.setText(service);
                                    textSubserviceModification.setText(subservice);


                                    layoutBirthdateModification.setVisibility(View.GONE);
                                    layoutUserFirstnameModification.setVisibility(View.GONE);
                                    layoutUserNameModification.setVisibility(View.GONE);
                                    layoutMailAdressModification.setVisibility(View.GONE);
                                    layoutNumberModification.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = mAuth.getCurrentUser().getUid();
                DocumentReference userRef;
                if (pro) {
                    userRef = db.collection("Pros").document(userId);
                    userRef.update(
                            "companyName", textCompanyNameModification.getText().toString(),
                            "nationalNumber", textNationalNumberModification.getText().toString(),
                            "name", textNameModification.getText().toString(),
                            "email", textMailModification.getText().toString(),
                            "phoneNumber", textNumberModification.getText().toString(),
                            "companyAddress", textCompanyAdressModification.getText().toString(),
                            "website", textWebsiteModification.getText().toString(),
                            "contact2Name", textContact2Name.getText().toString(),
                            "contact2Phone", textContact2Number.getText().toString(),
                            "contact2Email", textContact2Email.getText().toString(),
                            "service", textServiceModification.getText().toString(),
                            "subservice", textSubserviceModification.getText().toString()
                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Profile updated successfully.", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    userRef = db.collection("Users").document(userId);
                    userRef.update(
                            "name", textUserNameModification.getText().toString(),
                            "firstName", textFirstNameModification.getText().toString(),
                            "email", textUserMailModification.getText().toString(),
                            "phoneNumber", textUserPhoneNumberModification.getText().toString(),
                            "birthdate", textBirthdateModification.getText().toString()
                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Profile updated successfully.", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


        backBtnProfileModification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(muploadTask != null && muploadTask.isInProgress()){
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
                
            }
        });
    }

    private void openFileChooser() {
        mGetContent.launch("image/*");
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                mImageUri = result;
                Picasso.with(getContext()).load(mImageUri).fit().into(profilePic);
            }
        }
    });

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if(mImageUri != null){
            StorageReference fileReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            muploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarFile.setProgress(0);
                                }
                            }, 1000);
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(getContext(), "File sent", Toast.LENGTH_SHORT).show();
                                     upload = new Upload("ProfilePic", uri.toString());
                                }
                            });
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressfile = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBarFile.setProgress((int)progressfile);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

}