package com.example.interim.offers;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.interim.R;

import org.checkerframework.checker.nullness.qual.NonNull;

public class searchCard_ViewHolder extends RecyclerView.ViewHolder {
    LottieAnimationView likeBtn;
    TextView jobTitle;
    TextView companyName;
    TextView jobDate;
    TextView jobCategory;
    TextView postDate;
    TextView jobKeywords;
    TextView jobLocation;
    TextView jobSalary;
    TextView jobUrl;
    Button likeInit, applyBtn, shareBtn;

    Boolean liked = false;

    public searchCard_ViewHolder(@NonNull View itemView) {
        super(itemView);

        jobTitle = itemView.findViewById(R.id.jobTitle);
        companyName = itemView.findViewById(R.id.companyName);
        jobDate = itemView.findViewById(R.id.jobDate);
        jobCategory = itemView.findViewById(R.id.jobCategory);
        postDate = itemView.findViewById(R.id.postDate);
        jobKeywords = itemView.findViewById(R.id.jobKeywords);
        jobLocation = itemView.findViewById(R.id.jobLocation);
        jobSalary = itemView.findViewById(R.id.jobSalary);
        jobUrl = itemView.findViewById(R.id.jobUrl);
        likeBtn = itemView.findViewById(R.id.likeBtn);
        likeInit = itemView.findViewById(R.id.likeInit);
        applyBtn = itemView.findViewById(R.id.applyBtn);
        shareBtn = itemView.findViewById(R.id.shareBtn);
    }
}
