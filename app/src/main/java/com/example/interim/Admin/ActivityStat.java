package com.example.interim.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.res.Configuration;
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityStat extends AppCompatActivity {

    Button backStatBtn;
    BarChart barChart, barChartSignal, barChartBlocked, barChartSignalOffers;

    PieChart pieChart;
    RadarChart radarChart;

    private DatabaseReference offersRef;
    private int nightModeFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            setTheme(R.style.ThemeDark_Interim);
        else
            setTheme(R.style.Theme_Interim);
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
        barChartBlocked = findViewById(R.id.barChartBlocked);
        barChartSignalOffers = findViewById(R.id.barChartSignalOffers);
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
                        barChartSignal.getDescription().setEnabled(false);
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                            dataSet.setValueTextColor(Color.WHITE);
                            barChartSignal.getLegend().setTextColor(Color.WHITE);
                            barChartSignal.getAxisLeft().setTextColor(Color.WHITE);
                            barChartSignal.getXAxis().setTextColor(Color.WHITE);
                            barChartSignal.getAxisRight().setTextColor(Color.WHITE);
                        }
                        else {
                            dataSet.setValueTextColor(Color.BLACK);
                            barChartSignal.getLegend().setTextColor(Color.BLACK);
                            barChartSignal.getAxisLeft().setTextColor(Color.BLACK);
                            barChartSignal.getXAxis().setTextColor(Color.BLACK);
                            barChartSignal.getAxisRight().setTextColor(Color.BLACK);
                        }
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

        db.collection("Blocked")
                .whereGreaterThanOrEqualTo("date", getOneWeekAgo()) // Méthode pour obtenir la date il y a une semaine
                .limit(50)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        HashMap<String, Integer> blockedByDay = new HashMap<>();
                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

                        // Initialiser les compteurs pour chaque jour de la semaine
                        Calendar calendar = Calendar.getInstance();
                        List<String> daysOfWeek = getDaysOfWeek();
                        for (String day : daysOfWeek) {
                            blockedByDay.put(day, 0);
                        }

                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            Date blockDate = documentSnapshot.getDate("date");
                            if (blockDate != null) {
                                calendar.setTime(blockDate);
                                String day = dayFormat.format(calendar.getTime());
                                if (blockedByDay.containsKey(day)) {
                                    int count = blockedByDay.get(day);
                                    blockedByDay.put(day, count + 1);
                                }
                            }
                        }

                        ArrayList<BarEntry> barEntries = new ArrayList<>();
                        int index = 0;
                        for (String day : daysOfWeek) {
                            int blockedCount = blockedByDay.get(day);
                            barEntries.add(new BarEntry(index, blockedCount));
                            index++;
                        }

                        BarDataSet dataSet = new BarDataSet(barEntries, "Blocked by Day");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barChartBlocked.getDescription().setEnabled(false);
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                            dataSet.setValueTextColor(Color.WHITE);
                            barChartBlocked.getLegend().setTextColor(Color.WHITE);
                            barChartBlocked.getAxisLeft().setTextColor(Color.WHITE);
                            barChartBlocked.getXAxis().setTextColor(Color.WHITE);
                            barChartBlocked.getAxisRight().setTextColor(Color.WHITE);
                        }
                        else {
                            dataSet.setValueTextColor(Color.BLACK);
                            barChartBlocked.getLegend().setTextColor(Color.BLACK);
                            barChartBlocked.getAxisLeft().setTextColor(Color.BLACK);
                            barChartBlocked.getXAxis().setTextColor(Color.BLACK);
                            barChartBlocked.getAxisRight().setTextColor(Color.BLACK);
                        }
                        dataSet.setValueTextSize(12f);

                        BarData barData = new BarData(dataSet);
                        barData.setBarWidth(0.9f);

                        barChartBlocked.setData(barData);
                        barChartBlocked.invalidate();

                        // Configurer les jours de la semaine en abscisse
                        XAxis xAxis = barChartBlocked.getXAxis();
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
                    }
                });

        db.collection("SignaledOffers")
                .whereGreaterThanOrEqualTo("signalDate", getOneWeekAgo())
                .limit(50)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        HashMap<String, Integer> signaledOffersByDay = new HashMap<>();
                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

                        // Initialiser les compteurs pour chaque jour de la semaine
                        Calendar calendar = Calendar.getInstance();
                        List<String> daysOfWeek = getDaysOfWeek();
                        for (String day : daysOfWeek) {
                            signaledOffersByDay.put(day, 0);
                        }

                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            Date signaledDate = documentSnapshot.getDate("signalDate");
                            if (signaledDate != null) {
                                calendar.setTime(signaledDate);
                                String day = dayFormat.format(calendar.getTime());
                                if (signaledOffersByDay.containsKey(day)) {
                                    int count = signaledOffersByDay.get(day);
                                    signaledOffersByDay.put(day, count + 1);
                                }
                            }
                        }

                        ArrayList<BarEntry> barEntries = new ArrayList<>();
                        int index = 0;
                        for (String day : daysOfWeek) {
                            int signaledCount = signaledOffersByDay.get(day);
                            barEntries.add(new BarEntry(index, signaledCount));
                            index++;
                        }

                        BarDataSet dataSet = new BarDataSet(barEntries, "Signaled Offers by Day");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barChartSignalOffers.getDescription().setEnabled(false);
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                            dataSet.setValueTextColor(Color.WHITE);
                            barChartSignalOffers.getLegend().setTextColor(Color.WHITE);
                            barChartSignalOffers.getAxisLeft().setTextColor(Color.WHITE);
                            barChartSignalOffers.getXAxis().setTextColor(Color.WHITE);
                            barChartSignalOffers.getAxisRight().setTextColor(Color.WHITE);
                        }
                        else {
                            dataSet.setValueTextColor(Color.BLACK);
                            barChartSignalOffers.getLegend().setTextColor(Color.BLACK);
                            barChartSignalOffers.getAxisLeft().setTextColor(Color.BLACK);
                            barChartSignalOffers.getXAxis().setTextColor(Color.BLACK);
                            barChartSignalOffers.getAxisRight().setTextColor(Color.BLACK);
                        }
                        dataSet.setValueTextSize(12f);

                        BarData barData = new BarData(dataSet);
                        barData.setBarWidth(0.9f);

                        barChartSignalOffers.setData(barData);
                        barChartSignalOffers.invalidate();

                        // Configurer les jours de la semaine en abscisse
                        XAxis xAxis = barChartSignalOffers.getXAxis();
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
                        barChart.getDescription().setEnabled(false);
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                            dataSet.setValueTextColor(Color.WHITE);
                            barChart.getLegend().setTextColor(Color.WHITE);
                            barChart.getAxisLeft().setTextColor(Color.WHITE);
                            barChart.getXAxis().setTextColor(Color.WHITE);
                            barChart.getAxisRight().setTextColor(Color.WHITE);
                        }
                        else {
                            dataSet.setValueTextColor(Color.BLACK);
                            barChart.getLegend().setTextColor(Color.BLACK);
                            barChart.getAxisLeft().setTextColor(Color.BLACK);
                            barChart.getXAxis().setTextColor(Color.BLACK);
                            barChart.getAxisRight().setTextColor(Color.BLACK);
                        }
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

        db.collection("Messages")
                .whereGreaterThanOrEqualTo("date", getOneWeekAgo()) // Méthode pour obtenir la date il y a une semaine
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        HashMap<String, Integer> messagesByDay = new HashMap<>();
                        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

                        // Initialiser les compteurs pour chaque jour de la semaine
                        List<String> daysOfWeek = getDaysOfWeek();
                        for (String day : daysOfWeek) {
                            messagesByDay.put(day, 0);
                        }

                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            Date messageDate = documentSnapshot.getDate("date");
                            if (messageDate != null) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(messageDate);
                                String day = dayFormat.format(calendar.getTime());
                                if (messagesByDay.containsKey(day)) {
                                    int count = messagesByDay.get(day);
                                    messagesByDay.put(day, count + 1);
                                }
                            }
                        }

                        ArrayList<RadarEntry> radarEntries = new ArrayList<>();
                        ArrayList<String> labels = new ArrayList<>();

                        for (String day : daysOfWeek) {
                            int messageCount = messagesByDay.get(day);
                            radarEntries.add(new RadarEntry(messageCount));
                            labels.add(day);
                        }

                        RadarDataSet dataSet = new RadarDataSet(radarEntries, "Messages by Day");
                        dataSet.setColor(Color.BLUE);
                        dataSet.setDrawFilled(true);

                        RadarData radarData = new RadarData(dataSet);
                        radarData.setLabels(labels);
                        radarChart.getDescription().setEnabled(false);
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                            dataSet.setValueTextColor(Color.WHITE);
                            radarChart.getLegend().setTextColor(Color.WHITE);
                            radarChart.getXAxis().setTextColor(Color.WHITE);
                            radarChart.getYAxis().setTextColor(Color.WHITE);
                        }
                        else {
                            dataSet.setValueTextColor(Color.BLACK);
                            radarChart.getLegend().setTextColor(Color.BLACK);
                            radarChart.getXAxis().setTextColor(Color.BLACK);
                            radarChart.getYAxis().setTextColor(Color.BLACK);
                        }

                        radarChart.setData(radarData);
                        radarChart.invalidate();

                        // Configurer les labels avec le nom des jours de la semaine
                        XAxis xAxis = radarChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
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
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
                            dataSet.setValueTextColor(Color.WHITE);
                        else
                            dataSet.setValueTextColor(Color.BLACK);
                        dataSet.setValueTextSize(12f);

                        PieData pieData = new PieData(dataSet);
                        pieData.setValueTextColor(Color.WHITE);
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
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            pieChart.setEntryLabelColor(Color.WHITE);
            pieChart.getLegend().setTextColor(Color.WHITE);
        }
        else {
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.getLegend().setTextColor(Color.BLACK);
        }
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