package com.example.interim.Admin;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.airbnb.lottie.LottieAnimationView;
import com.example.interim.R;

import org.checkerframework.checker.nullness.qual.NonNull;


public class signaledUser_ViewHolder extends RecyclerView.ViewHolder {

    TextView signalerMail, signaledMail, signaledDate, signalReason;
    LinearLayout messageUser, deleteAccount, sendWarning;

    public signaledUser_ViewHolder(@NonNull View itemView) {
        super(itemView);

        signalReason = itemView.findViewById(R.id.signalReason);
        signaledMail = itemView.findViewById(R.id.signaledMail);
        signaledDate = itemView.findViewById(R.id.signaledDate);
        signalerMail = itemView.findViewById(R.id.signalerMail);
        deleteAccount = itemView.findViewById(R.id.deleteAccount);
        messageUser = itemView.findViewById(R.id.messageUser);
        sendWarning = itemView.findViewById(R.id.sendWarning);
    }
}