package com.example.interim;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.airbnb.lottie.LottieAnimationView;

import org.checkerframework.checker.nullness.qual.NonNull;


public class conversation_ViewHolder extends RecyclerView.ViewHolder {

    TextView userName;
    TextView lastMsg;
    LottieAnimationView unRead;
    ImageView openConv;

    public conversation_ViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.userName);
        lastMsg = itemView.findViewById(R.id.lastMsg);
        unRead = itemView.findViewById(R.id.unRead);
        openConv = itemView.findViewById(R.id.openConv);
    }
}
