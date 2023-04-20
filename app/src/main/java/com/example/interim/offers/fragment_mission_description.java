package com.example.interim.offers;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import android.content.Context;
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

import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interim.R;
import com.example.interim.models.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class fragment_mission_description extends Fragment {

    private MapView map;
    private static final int REQUEST_LOCATION = 1;
    LocationManager lm;
    float latitude, longitude;
    boolean pro = false;
    String jobId;
    LinearLayout applyContainer;
    public fragment_mission_description() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission_description, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            jobId = bundle.getString("id");
            // Do something with the job ID
        }
        return view;
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        applyContainer = view.findViewById(R.id.applyContainer);
        FirebaseFirestore db;
        FirebaseAuth mAuth;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        super.onViewCreated(view, savedInstanceState);
        Button moreActions = view.findViewById(R.id.moreActions);
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


        db = FirebaseFirestore.getInstance();
        // Assuming that you have the offer ID in a variable named 'offerId'
        DocumentReference offerRef = db.collection("Offers").document(jobId);

        offerRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Convert the document snapshot to an Offer object
                    Offer offer = documentSnapshot.toObject(Offer.class);
                    jobTitle.setText(offer.getJobTitle());
                    companyName.setText(offer.getCompanyName());
                    dateText.setText(getResources().getString(R.string.dateIndicationsStart) + offer.getStartDate()
                            + getResources().getString(R.string.dateIndicationsEnd) + offer.getEndDate());
                    salary.setText( offer.getSalaryMax()+"€" + getResources().getString(R.string.moneyMonthIndicator));
                    postedDate.setText(getResources().getString(R.string.postedDateSuffix) + offer.getPostDate());
                    moreInfosText.setText(offer.getLabel());
                    missionText.setText(offer.getDescription());




                    itinaryButtonMission.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=43.63178,3.86347&mode=d"));
                            intent.setPackage("com.google.android.apps.maps");
                            if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                                startActivity(intent);
                            }
                        }
                    });
                    // Do something with the offer object
                } else {
                    // Handle the case when the offer document doesn't exist
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error case
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

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userRef = db.collection("Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        pro = false;
                    } else {
                        pro = true;
                        applyContainer.setVisibility(View.GONE);
                    }
                }
            });
        }



        closeActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreActions.setVisibility(View.VISIBLE);
                TransitionManager.beginDelayedTransition(actionsContainer);
                actionsContainer.setVisibility(View.GONE);
                closeActions.setVisibility(View.GONE);
            }
        });

        org.osmdroid.config.Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(43.63178, 3.86347);
        IMapController mapController = map.getController();
        mapController.setCenter(startPoint);
        mapController.setZoom(20.0);
        ArrayList<OverlayItem> items = new ArrayList<>();
        OverlayItem position = new OverlayItem("Votre position", "Actualisée", new GeoPoint(43.63178, 3.86347));
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
}