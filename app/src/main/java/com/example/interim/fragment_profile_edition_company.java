package com.example.interim;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.icu.lang.UCharacter;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class fragment_profile_edition_company extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    Upload upload;

    ImageView profilePic;
    private Uri mImageUri;

    private ProgressBar progressBarFile;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask muploadTask;

    public fragment_profile_edition_company() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edition_company, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText textNameModification = view.findViewById(R.id.text1NameModification);
        TextInputEditText textCompanyNameModification = view.findViewById(R.id.textCompanyNameModification);
        TextInputEditText textNationalNumberModification = view.findViewById(R.id.textNationalNumberModification);
        TextInputEditText textMailModification = view.findViewById(R.id.text1MailModification);
        TextInputEditText textNumberModification = view.findViewById(R.id.textContact1NumberModification);
        TextInputEditText textCompanyAdressModification = view.findViewById(R.id.textCompanyAdressModification);
        TextInputEditText textWebsiteModification = view.findViewById(R.id.textWebsiteModification);


        TextInputLayout layoutContact2NameModification = view.findViewById(R.id.layoutContact2NameModification);
        TextInputLayout layoutContact2MailAdressModification = view.findViewById(R.id.layoutContact2MailAdressModification);
        TextInputLayout layoutContact2NumberModification = view.findViewById(R.id.layoutContact2NumberModification);
        TextInputLayout layoutServiceModification = view.findViewById(R.id.layoutServiceModification);
        TextInputLayout layoutSubModification = view.findViewById(R.id.layoutSubServiceModification);
        Button backBtnProfileModification = view.findViewById(R.id.backBtnProfileModification);
        Button choosePic = view.findViewById(R.id.choosePicBtn);
        Button uploadFile = view.findViewById(R.id.uploadBtn);
        profilePic = view.findViewById(R.id.imageVisualizer);
        progressBarFile = view.findViewById(R.id.progressBarPic);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

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