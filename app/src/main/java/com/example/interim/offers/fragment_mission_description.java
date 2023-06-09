package com.example.interim.offers;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.os.StrictMode;
import android.text.InputFilter;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interim.AppActivity;
import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.discussion.NewMessageConversationActivity;
import com.example.interim.models.Offer;
import com.example.interim.models.SignaledOffer;
import com.example.interim.profile.CompanyProfileViewer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.Constants;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class fragment_mission_description extends Fragment {

    private MapView map;
    private static final int REQUEST_LOCATION = 1;
    LocationManager lm;

    boolean pro = false;
    String offerLocation;
    String offerCategory;
    String recruiterId;
    String recruiterMail;

    FirebaseFirestore db = FirebaseFirestore.getInstance();;

    String companyNameText;
    String contactNameText;

    String firstName;

    Button applyBtnMission, contactCompanyBtn, seeProfile, signalPost;
    StorageReference mStorageRef;
    String name;
    String jobId;
    LinearLayout applyContainer, findSimilarBtnContainer, contactCompanyBtnContainer;
    public fragment_mission_description() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission_description, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Bundle bundle = getArguments();
        if (bundle != null) {
            jobId = bundle.getString("id");
        }
        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        applyContainer = view.findViewById(R.id.applyContainer);
        FirebaseFirestore db;
        FirebaseAuth mAuth;
        final String[] latitude = {"0.0"};
        final String[] longitude = {"0.0"};
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        super.onViewCreated(view, savedInstanceState);
        Button moreActions = view.findViewById(R.id.moreActions);
        Button findSimilarBtn = view.findViewById(R.id.findSimilarBtn);
        LinearLayout actionsContainer = view.findViewById(R.id.actionContainer);
        Button closeActions = view.findViewById(R.id.closeActions);
        Button backMission = view.findViewById(R.id.backMission);
        TextView companyName = view.findViewById(R.id.companyName);
        TextView jobTitle = view.findViewById(R.id.jobTitle);
        TextView missionText = view.findViewById(R.id.missionDescriptionText);
        TextView moreInfosText = view.findViewById(R.id.missionMoreInfosText);
        TextView salary = view.findViewById(R.id.salaryText);
        TextView dateText = view.findViewById(R.id.dateText);
        TextView postedDate = view.findViewById(R.id.postedDate);
        Button itinaryButtonMission = view.findViewById(R.id.itinaryButtonMission);
        Button shareBtn = view.findViewById(R.id.shareBtn);
        ImageView companyProfile = view.findViewById(R.id.companyProfile);
        findSimilarBtnContainer = view.findViewById(R.id.findSimilarContainer);
        contactCompanyBtnContainer = view.findViewById(R.id.contactCompanyBtnContainer);
        contactCompanyBtn = view.findViewById(R.id.contactCompanyBtn);
        seeProfile = view.findViewById(R.id.seeProfile);
        signalPost = view.findViewById(R.id.signalPost);


        final Offer[][] offer = {{new Offer()}};

        db = FirebaseFirestore.getInstance();
        final DocumentReference[] offerRef = {db.collection("Offers").document(jobId)};

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);
            FirebaseFirestore finalDb = db;
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        pro = false;
                        firstName = document.getString("firstName");
                        name = document.getString("name");

                    } else {
                        pro = true;
                        applyContainer.setVisibility(View.GONE);
                        findSimilarBtnContainer.setVisibility(View.GONE);
                        finalDb.collection("Pros").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                companyNameText = documentSnapshot.getString("companyName");
                                contactNameText = documentSnapshot.getString("name");
                            }
                        });
                    }
                }
            });
        }
        offerRef[0].get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Convert the document snapshot to an Offer object
                    offer[0][0] = documentSnapshot.toObject(Offer.class);

                    assert offer[0][0] != null;
                    jobTitle.setText(offer[0][0].getJobTitle());
                    companyName.setText(offer[0][0].getCompanyName());
                    dateText.setText(getResources().getString(R.string.dateIndicationsStart) + offer[0][0].getStartDate()
                            + getResources().getString(R.string.dateIndicationsEnd) + offer[0][0].getEndDate());
                    salary.setText(offer[0][0].getSalaryMax() + "€" + getResources().getString(R.string.moneyMonthIndicator));
                    postedDate.setText(getResources().getString(R.string.postedDateSuffix) + offer[0][0].getPostDate());
                    moreInfosText.setText(offer[0][0].getLabel());
                    missionText.setText(offer[0][0].getDescription());
                    offerLocation = offer[0][0].getLocation();
                    offerCategory = offer[0][0].getCategory();
                    recruiterId = offer[0][0].getRecruiter();
                    latitude[0] = offer[0][0].getPosX();
                    longitude[0] = offer[0][0].getPosY();



                    itinaryButtonMission.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+ latitude[0] + ","+ longitude[0] +"&mode=d"));
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        }
                    });
                    mStorageRef = FirebaseStorage.getInstance().getReference().child("uploads/" + recruiterId);
                    try {
                        final File localFile = File.createTempFile("profilePic", "jpg");
                        mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Uri fileUri = Uri.fromFile(localFile);
                                String imageUrl = fileUri.toString();
                                Picasso.get().load(imageUrl).fit().centerCrop().into(companyProfile);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    } catch (
                            IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    System.err.println("NO OFFER FOUND WITH THIS ID !");
                }

                org.osmdroid.config.Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

                map = view.findViewById(R.id.map);
                map.setTileSource(TileSourceFactory.MAPNIK);
                map.setBuiltInZoomControls(false);
                map.setMultiTouchControls(true);

                GeoPoint startPoint = null;
                if(latitude[0] != null && longitude[0] != null) {
                    startPoint = new GeoPoint(Double.parseDouble(offer[0][0].getPosX()), Double.parseDouble(offer[0][0].getPosY()));
                }
                else {
                    startPoint = new GeoPoint(43.63178, 3.86347);
                }

                IMapController mapController = map.getController();
                mapController.setCenter(startPoint);
                mapController.setZoom(7.0);
                ArrayList<OverlayItem> items = new ArrayList<>();
                OverlayItem position = new OverlayItem("Lieu de l'offre", "", startPoint);
                Drawable m = position.getMarker(0);
                items.add(position);
                ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<>(getContext(), items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {
                        return false;
                    }
                });

                mOverlay.setFocusItemsOnTap(true);
                map.getOverlays().add(mOverlay);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error case
            }
        });

        findSimilarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Offer offerToGive = offer[0][0];
                Intent intent = new Intent(getActivity(), AppActivity.class);
                intent.putExtra("offerFilters", offerToGive);
                getActivity().finish();
                startActivity(intent);

            }
        });

        contactCompanyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();;
                db.collection("Pros")
                        .document(recruiterId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        recruiterMail = document.getString("email");
                                        Intent newConversation = new Intent(getContext(), NewMessageConversationActivity.class);
                                        newConversation.putExtra("mail", recruiterMail);
                                        getContext().startActivity(newConversation);
                                        getActivity().finish();
                                    } else {
                                        Log.e("TAG", "Document does not exist");
                                    }
                                } else {
                                    Log.e("TAG", "Error getting document: ", task.getException());
                                }
                            }
                        });
            }
        });

        signalPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Raison du signalement");
                final EditText input = new EditText(getActivity());
                int paddingPixels = (int) (20 * getResources().getDisplayMetrics().density);
                input.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);
                input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(250) });
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String signalReason = input.getText().toString();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Date today = new Date();

                        SignaledOffer signaled = new SignaledOffer(today, mAuth.getCurrentUser().getUid(),
                                jobId, signalReason, mAuth.getCurrentUser().getEmail());
                        db.collection("SignaledOffers").add(signaled);
                        Toast.makeText(getContext(), getString(R.string.offerSignaledToast), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                int dialogPadding = (int) (20 * getResources().getDisplayMetrics().density);
                dialog.getWindow().getDecorView().setPadding(dialogPadding, dialogPadding, dialogPadding, dialogPadding);
            }
        });

        seeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recruiterId.equals(mAuth.getCurrentUser().getUid())) {
                    Toast.makeText(getContext(), "You are the owner of this offer", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), CompanyProfileViewer.class);
                    intent.putExtra("userId", recruiterId);
                    startActivity(intent);
                }

            }
        });

        applyBtnMission = view.findViewById(R.id.applyBtnMission);

        applyBtnMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser() != null) {
                    Intent loginIntent = new Intent(getContext(), ApplicationActivity.class);
                    loginIntent.putExtra("jobId",jobId);
                    getContext().startActivity(loginIntent);
                }
                else {
                    Intent loginIntent = new Intent(getContext(), MainActivity.class);
                    getContext().startActivity(loginIntent);
                }

            }
        });




        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pro){
                    String shareMessage = getContext().getString(R.string.shareofferHook) +
                            contactNameText + " " + getContext().getString(R.string.fromCompanyJoinDisplay) + " " + companyNameText +  ". " +
                            getContext().getString(R.string.shareofferTitle) + "\n" + "" + "\n" +
                            getContext().getString(R.string.shareOfferDetails) + "\n" + "" + "\n" +
                            getContext().getString(R.string.offerTitle) + " : " + jobTitle.getText().toString() + "\n" +
                            getContext().getString(R.string.companyName) + " : " + companyName.getText().toString() + "\n" +
                            getContext().getString(R.string.salaryPrice) + " : " + salary.getText().toString() + "\n" +
                            getContext().getString(R.string.dateDisplay) + ": " + dateText.getText().toString() + "\n" +
                            getContext().getString(R.string.placeInput) + " : " + offerLocation + "\n" +
                            getContext().getString(R.string.categoryOfferDisplay) + " : " + offerCategory + "\n" + "" + "\n" +
                            getContext().getString(R.string.shareofferActionCall);

                    Intent ShareIntent = new Intent();
                    ShareIntent.setAction(Intent.ACTION_SEND);
                    ShareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    ShareIntent.setType("text/plain");
                    ShareIntent = Intent.createChooser(ShareIntent, "Share with : ");
                    getContext().startActivity(ShareIntent);
                } else {
                    String shareMessage = getContext().getString(R.string.shareofferHook) +
                            firstName + "" + name +  ". " +
                            getContext().getString(R.string.shareofferTitle) + "\n" + "" + "\n" +
                            getContext().getString(R.string.shareOfferDetails) + "\n" + "" + "\n" +
                            getContext().getString(R.string.offerTitle) + " : " + jobTitle.getText().toString() + "\n" +
                            getContext().getString(R.string.companyName) + " : " + companyName.getText().toString() + "\n" +
                            getContext().getString(R.string.salaryPrice) + " : " + salary.getText().toString() + "\n" +
                            getContext().getString(R.string.dateDisplay) + ": " + dateText.getText().toString() + "\n" +
                            getContext().getString(R.string.placeInput) + " : " + offerLocation + "\n" +
                            getContext().getString(R.string.categoryOfferDisplay) + " : " + offerCategory + "\n" + "" + "\n" +
                            getContext().getString(R.string.shareofferActionCall);

                    Intent ShareIntent = new Intent();
                    ShareIntent.setAction(Intent.ACTION_SEND);
                    ShareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    ShareIntent.setType("text/plain");
                    ShareIntent = Intent.createChooser(ShareIntent, "Share with : ");
                    getContext().startActivity(ShareIntent);
                }
            }
        });


        moreActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreActions.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(actionsContainer);
                actionsContainer.setVisibility(View.VISIBLE);
                closeActions.setVisibility(View.VISIBLE);

            }
        });

        backMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        closeActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreActions.setVisibility(View.VISIBLE);
                TransitionManager.beginDelayedTransition(actionsContainer);
                actionsContainer.setVisibility(View.GONE);
                closeActions.setVisibility(View.GONE);
            }
        });



    }
}