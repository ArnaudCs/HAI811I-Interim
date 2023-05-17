package com.example.interim.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.interim.R;
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

import java.util.ArrayList;

public class ActivityStat extends AppCompatActivity {

    Button backStatBtn;
    BarChart barChart;

    PieChart pieChart;
    RadarChart radarChart;

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

        ArrayList<BarEntry> visitors = new ArrayList<>();
        visitors.add(new BarEntry(2014, 420));
        visitors.add(new BarEntry(2015, 308));
        visitors.add(new BarEntry(2016, 564));
        visitors.add(new BarEntry(2017, 570));
        visitors.add(new BarEntry(2018, 602));
        visitors.add(new BarEntry(2019, 645));
        visitors.add(new BarEntry(2020, 789));
        visitors.add(new BarEntry(2021, 648));
        visitors.add(new BarEntry(2022, 705));

        BarDataSet barDataSet = new BarDataSet(visitors, "Visiteurs");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Histogramme de test");
        barChart.animateY(2000);

        ArrayList<PieEntry> visitorsPie = new ArrayList<>();
        visitorsPie.add(new PieEntry(420, "Informatique"));
        visitorsPie.add(new PieEntry(308, "BTP"));
        visitorsPie.add(new PieEntry(789, "Hôtelerie"));
        visitorsPie.add(new PieEntry(648, "Mécanique"));
        visitorsPie.add(new PieEntry(705, "Recherche"));

        PieDataSet pieDataSet = new PieDataSet(visitorsPie, "Visiteurs");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Nombre d'offres par catégories");
        pieChart.animate();

        ArrayList<RadarEntry> visitorsRadar = new ArrayList<>();
        visitorsRadar.add(new RadarEntry(420));
        visitorsRadar.add(new RadarEntry(308));
        visitorsRadar.add(new RadarEntry(789));
        visitorsRadar.add(new RadarEntry(648));
        visitorsRadar.add(new RadarEntry(705));

        RadarDataSet radarDataSet = new RadarDataSet(visitorsRadar, "Visiteurs");
        radarDataSet.setColor(Color.RED);
        radarDataSet.setLineWidth(2f);
        radarDataSet.setValueTextColor(Color.RED);
        radarDataSet.setValueTextSize(14f);

        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSet);
        String[] labels = {"2015", "2016", "2017", "2018", "2019"};
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        radarChart.setData(radarData);
    }
}