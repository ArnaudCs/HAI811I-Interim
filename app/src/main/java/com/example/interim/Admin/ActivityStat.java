package com.example.interim.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.interim.R;
import com.example.interim.models.Offer;
import com.example.interim.offers.searchCard_ViewAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityStat extends AppCompatActivity {

    Button backStatBtn;
    BarChart barChart;

    PieChart pieChart;
    RadarChart radarChart;

    private DatabaseReference offersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        backStatBtn = findViewById(R.id.backStatBtn);

        backStatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        radarChart = findViewById(R.id.radarChart);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Offers")
                .orderBy("postDate", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        HashMap<String, Integer> recruitersMap = new HashMap<>();
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            String recruiter = documentSnapshot.getString("recruiter");
                            String companyName = documentSnapshot.getString("companyName");
                            if (recruiter != null) {
                                if (recruitersMap.containsKey(recruiter)) {
                                    int count = recruitersMap.get(recruiter);
                                    recruitersMap.put(recruiter, count + 1);
                                } else {
                                    recruitersMap.put(recruiter, 1);
                                }
                            }
                        }

                        for (Map.Entry<String, Integer> entry : recruitersMap.entrySet()) {
                            String recruiter = entry.getKey();
                            int offerCount = entry.getValue();
                            System.out.println("Recruteur : " + recruiter + ", Nombre d'offres : " + offerCount);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gérer l'échec de la récupération des données
                    }
                });

//        ArrayList<BarEntry> visitors = new ArrayList<>();
//        visitors.add(new BarEntry(2014, 420));
//
//        BarDataSet barDataSet = new BarDataSet(visitors, "Visiteurs");
//        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(16f);
//
//        BarData barData = new BarData(barDataSet);
//
//        barChart.setFitBars(true);
//        barChart.setData(barData);
//        barChart.getDescription().setText("Histogramme de test");
//        barChart.animateY(2000);

//        ArrayList<RadarEntry> visitorsRadar = new ArrayList<>();
//        visitorsRadar.add(new RadarEntry(420));
//
//        RadarDataSet radarDataSet = new RadarDataSet(visitorsRadar, "Visiteurs");
//        radarDataSet.setColor(Color.RED);
//        radarDataSet.setLineWidth(2f);
//        radarDataSet.setValueTextColor(Color.RED);
//        radarDataSet.setValueTextSize(14f);
//
//        RadarData radarData = new RadarData();
//        radarData.addDataSet(radarDataSet);
//        String[] labels = {"2015", "2016", "2017", "2018", "2019"};
//        XAxis xAxis = radarChart.getXAxis();
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
//
//        radarChart.setData(radarData);
    }
}