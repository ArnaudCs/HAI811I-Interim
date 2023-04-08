package com.example.interim;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;

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
    public fragment_mission_description() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mission_description, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button moreActions = view.findViewById(R.id.moreActions);
        LinearLayout actionsContainer = view.findViewById(R.id.actionContainer);
        Button closeActions = view.findViewById(R.id.closeActions);
        TextView missionText = view.findViewById(R.id.missionDescriptionText);
        TextView moreInfosText = view.findViewById(R.id.missionMoreInfosText);
        TextView salary = view.findViewById(R.id.salaryText);
        TextView dateText = view.findViewById(R.id.dateText);

        dateText.setText(getResources().getString(R.string.dateIndicationsStart) + "24/06/2022"
                + getResources().getString(R.string.dateIndicationsEnd) + "24/08/2023");

        salary.setText("1237€" + getResources().getString(R.string.moneyMonthIndicator));

        moreInfosText.setText("Bien sûr, voici un court texte avec des informations sur les avantages et " +
                        "les conditions de travail pour un poste chez Google :\n");

        missionText.setText("Vous êtes à la recherche d'une opportunité professionnelle passionnante et stimulante ? " +
                        "Vous cherchez à travailler dans une entreprise innovante et dynamique ? Si c'est le cas, alors cette annonce" +
                " d'emploi chez Google est faite pour vous !\n");

        moreActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreActions.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(actionsContainer);
                actionsContainer.setVisibility(View.VISIBLE);
                closeActions.setVisibility(View.VISIBLE);

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