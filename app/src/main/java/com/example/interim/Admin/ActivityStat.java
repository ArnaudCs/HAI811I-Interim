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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityStat extends AppCompatActivity {

    Button backStatBtn;
    BarChart barChart, barChartSignal;

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
        barChartSignal = findViewById(R.id.barChartSignal);
        pieChart = findViewById(R.id.pieChart);
        radarChart = findViewById(R.id.radarChart);
        setupPieChart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Signaled")
                .whereGreaterThanOrEqualTo("signalDate", getOneWeekAgo()) // Méthode pour obtenir la date il y a une semaine
                .limit(50)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        HashMap<String, Integer> signalsByDay = new HashMap<>();
                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

                        // Initialiser les compteurs pour chaque jour de la semaine
                        Calendar calendar = Calendar.getInstance();
                        List<String> daysOfWeek = getDaysOfWeek();
                        for (String day : daysOfWeek) {
                            signalsByDay.put(day, 0);
                        }

                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            Date signalementDate = documentSnapshot.getDate("signalDate");
                            if (signalementDate != null) {
                                calendar.setTime(signalementDate);
                                String day = dayFormat.format(calendar.getTime());
                                if (signalsByDay.containsKey(day)) {
                                    int count = signalsByDay.get(day);
                                    signalsByDay.put(day, count + 1);
                                }
                            }
                        }

                        ArrayList<BarEntry> barEntries = new ArrayList<>();
                        int index = 0;
                        for (String day : daysOfWeek) {
                            int signalCount = signalsByDay.get(day);
                            barEntries.add(new BarEntry(index, signalCount));
                            index++;
                        }

                        BarDataSet dataSet = new BarDataSet(barEntries, "Signals by Day");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dataSet.setValueTextColor(Color.BLACK);
                        dataSet.setValueTextSize(12f);

                        BarData barData = new BarData(dataSet);
                        barData.setBarWidth(0.9f);

                        barChartSignal.setData(barData);
                        barChartSignal.invalidate();

                        // Configurer les jours de la semaine en abscisse
                        XAxis xAxis = barChartSignal.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);
                        xAxis.setLabelCount(daysOfWeek.size());
                        xAxis.setDrawGridLines(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gérer l'échec de la récupération des données
                    }
                });

        db.collection("Offers")
                .orderBy("postDate", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("postDate", getOneWeekAgo()) // Méthode pour obtenir la date il y a une semaine
                .limit(50)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        HashMap<String, Integer> offersByDay = new HashMap<>();
                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

                        // Initialiser les compteurs pour chaque jour de la semaine
                        Calendar calendar = Calendar.getInstance();
                        List<String> daysOfWeek = getDaysOfWeek();
                        for (String day : daysOfWeek) {
                            offersByDay.put(day, 0);
                        }

                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            Date postDate = documentSnapshot.getDate("postDate");
                            if (postDate != null) {
                                calendar.setTime(postDate);
                                String day = dayFormat.format(calendar.getTime());
                                if (offersByDay.containsKey(day)) {
                                    int count = offersByDay.get(day);
                                    offersByDay.put(day, count + 1);
                                }
                            }
                        }

                        ArrayList<BarEntry> barEntries = new ArrayList<>();
                        int index = 0;
                        for (String day : daysOfWeek) {
                            int offerCount = offersByDay.get(day);
                            barEntries.add(new BarEntry(index, offerCount));
                            index++;
                        }

                        BarDataSet dataSet = new BarDataSet(barEntries, "Offers by Day");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dataSet.setValueTextColor(Color.BLACK);
                        dataSet.setValueTextSize(12f);

                        BarData barData = new BarData(dataSet);
                        barData.setBarWidth(0.9f);

                        barChart.setData(barData);
                        barChart.invalidate();

                        // Configurer les jours de la semaine en abscisse
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);
                        xAxis.setLabelCount(daysOfWeek.size());
                        xAxis.setDrawGridLines(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gérer l'échec de la récupération des données
                    }
                });

        db.collection("Offers")
                .orderBy("postDate", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        HashMap<String, List<String>> recruitersMap = new HashMap<>();
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            String recruiter = documentSnapshot.getString("recruiter");
                            String companyName = documentSnapshot.getString("companyName");
                            if (recruiter != null && companyName != null) {
                                if (recruitersMap.containsKey(recruiter)) {
                                    List<String> offerInfo = recruitersMap.get(recruiter);
                                    int offerCount = Integer.parseInt(offerInfo.get(0)) + 1;
                                    offerInfo.set(0, String.valueOf(offerCount));
                                } else {
                                    List<String> offerInfo = new ArrayList<>();
                                    offerInfo.add("1"); // Initial offer count is 1
                                    offerInfo.add(companyName);
                                    recruitersMap.put(recruiter, offerInfo);
                                }
                            }
                        }

                        ArrayList<PieEntry> pieEntries = new ArrayList<>();
                        for (Map.Entry<String, List<String>> entry : recruitersMap.entrySet()) {
                            List<String> offerInfo = entry.getValue();
                            int offerCount = Integer.parseInt(offerInfo.get(0));
                            String companyName = offerInfo.get(1);
                            pieEntries.add(new PieEntry(offerCount, companyName));
                        }

                        PieDataSet dataSet = new PieDataSet(pieEntries, "Recruiters");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dataSet.setValueTextColor(Color.BLACK);
                        dataSet.setValueTextSize(12f);

                        PieData pieData = new PieData(dataSet);

                        pieChart.setData(pieData);
                        pieChart.invalidate();
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

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
    }

    private Date getOneWeekAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        return calendar.getTime();
    }

    private List<String> getDaysOfWeek() {
        List<String> daysOfWeek = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        for (int i = 0; i < 7; i++) {
            daysOfWeek.add(dayFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        Collections.reverse(daysOfWeek); // Inverser l'ordre pour afficher dans le bon sens
        return daysOfWeek;
    }
}