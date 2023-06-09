package com.example.interim.offers;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.interim.R;

import org.checkerframework.checker.nullness.qual.NonNull;

public class applicationCard_ViewHolder extends RecyclerView.ViewHolder {
    String jobId;
    String applicationId;
    TextView jobTitle;
    TextView companyName;
    TextView jobDate;
    TextView jobCategory;
    TextView postDate;
    TextView jobLocation;
    TextView jobSalary;
    ImageView statusIcon;

    LinearLayout deleteBtn;

    public applicationCard_ViewHolder(@NonNull View itemView) {
        super(itemView);

        jobTitle = itemView.findViewById(R.id.jobTitle);
        companyName = itemView.findViewById(R.id.companyName);
        jobDate = itemView.findViewById(R.id.jobDate);
        jobCategory = itemView.findViewById(R.id.jobCategory);
        postDate = itemView.findViewById(R.id.postDate);
        jobLocation = itemView.findViewById(R.id.jobLocation);
        jobSalary = itemView.findViewById(R.id.jobSalary);
        deleteBtn = itemView.findViewById(R.id.deleteApplicationBtn);
        statusIcon = itemView.findViewById(R.id.statusIcon);
    }
}
