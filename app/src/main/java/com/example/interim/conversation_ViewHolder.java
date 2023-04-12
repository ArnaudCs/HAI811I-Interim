package com.example.interim;

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

import org.checkerframework.checker.nullness.qual.NonNull;


public class conversation_ViewHolder extends RecyclerView.ViewHolder {

    String conversationId;
    TextView userName;
    TextView lastMsg;
    LottieAnimationView unRead;

    ImageView openConv, profilePic;

    Button deleteConvBtn, makeGroupBtn, cancelDelete, validateDelete;
    LinearLayout conversationContainer, checkDelete, goToMessages;

    public conversation_ViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.userName);
        lastMsg = itemView.findViewById(R.id.lastMsg);
        unRead = itemView.findViewById(R.id.unRead);
        openConv = itemView.findViewById(R.id.openConv);
        profilePic = itemView.findViewById(R.id.profilePic);
        deleteConvBtn = itemView.findViewById(R.id.deleteMessages);
        makeGroupBtn = itemView.findViewById(R.id.groupMaking);
        cancelDelete = itemView.findViewById(R.id.cancelDelete);
        validateDelete = itemView.findViewById(R.id.validateDelete);
        checkDelete = itemView.findViewById(R.id.checkDelete);
        goToMessages = itemView.findViewById(R.id.messageBubble);
        conversationContainer = itemView.findViewById(R.id.conversationContainer);
    }
}
