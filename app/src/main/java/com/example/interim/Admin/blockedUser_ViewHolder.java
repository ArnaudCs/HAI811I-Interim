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


public class blockedUser_ViewHolder extends RecyclerView.ViewHolder {

    TextView blockerMail, blockedMail, blockDate, blockReason;
    LinearLayout messageUser, UnblockUser;

    public blockedUser_ViewHolder(@NonNull View itemView) {
        super(itemView);

        blockerMail = itemView.findViewById(R.id.blockerMail);
        blockedMail = itemView.findViewById(R.id.blockedMail);
        blockDate = itemView.findViewById(R.id.blockDate);
        blockReason = itemView.findViewById(R.id.blockReason);
        UnblockUser = itemView.findViewById(R.id.UnblockUser);
        messageUser = itemView.findViewById(R.id.messageUser);

    }
}
