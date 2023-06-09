package com.example.interim.offers;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;

import org.checkerframework.checker.nullness.qual.NonNull;

public class applicationOverview_ViewHolder extends RecyclerView.ViewHolder {
    String appId;
    String applicantId;
    TextView applicantName;
    TextView applicantEmail;
    TextView applicantNumber;
    TextView jobTitle;
    TextView applicationDate;
    TextView applicantAdress;
    LinearLayout acceptApplicant, rejectApplicant, phoneApplicant, smsApplicant, messageApplicant;
    LinearLayout acceptContainer, rejectContainer, messageContainer, smsContainer, phoneContainer;
    Button seeMore, downloadCoverLetterBtn, downloadResumeBtn, signalApplication;



    public applicationOverview_ViewHolder(@NonNull View itemView) {
        super(itemView);
        applicantName = itemView.findViewById(R.id.applicantName);
        applicantEmail = itemView.findViewById(R.id.applicantEmail);
        applicantNumber = itemView.findViewById(R.id.applicantNumber);
        jobTitle = itemView.findViewById(R.id.jobTitle);
        applicationDate = itemView.findViewById(R.id.applicationDate);
        applicantAdress = itemView.findViewById(R.id.applicantAdress);
        acceptApplicant = itemView.findViewById(R.id.acceptApplicant);
        rejectApplicant = itemView.findViewById(R.id.rejectApplicant);
        downloadCoverLetterBtn = itemView.findViewById(R.id.downloadCoverLetterBtn);
        downloadResumeBtn = itemView.findViewById(R.id.downloadResumeBtn);
        phoneApplicant = itemView.findViewById(R.id.phoneApplicant);
        smsApplicant = itemView.findViewById(R.id.smsApplicant);
        messageApplicant = itemView.findViewById(R.id.messageApplicant);
        acceptContainer = itemView.findViewById(R.id.acceptContainer);
        rejectContainer = itemView.findViewById(R.id.rejectContainer);
        messageContainer = itemView.findViewById(R.id.messageContainer);
        smsContainer = itemView.findViewById(R.id.smsContainer);
        phoneContainer = itemView.findViewById(R.id.phoneContainer);
        signalApplication = itemView.findViewById(R.id.signalApplication);

    }
}
