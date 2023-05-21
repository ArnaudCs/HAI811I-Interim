package com.example.interim.Admin;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;

import org.checkerframework.checker.nullness.qual.NonNull;

public class signaledOffers_ViewHolder extends RecyclerView.ViewHolder {

    TextView companyName, jobTitle, signaledDate, signalerMail, signalReason;
    LinearLayout messageUser, deleteOffer, sendWarning;

    public signaledOffers_ViewHolder(@NonNull View itemView) {
        super(itemView);

        signalReason = itemView.findViewById(R.id.signalReason);
        signalerMail = itemView.findViewById(R.id.signalerMail);
        signaledDate = itemView.findViewById(R.id.signaledDate);
        jobTitle = itemView.findViewById(R.id.jobTitle);
        companyName = itemView.findViewById(R.id.companyName);
        messageUser = itemView.findViewById(R.id.messageUser);
        deleteOffer = itemView.findViewById(R.id.deleteOffer);
        sendWarning = itemView.findViewById(R.id.sendWarning);
    }
}