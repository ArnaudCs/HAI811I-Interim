package com.example.interim.discussion;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieAnimationView;
import com.example.interim.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;


public class member_ViewHolder extends RecyclerView.ViewHolder {
    private TextView memberMail;
    LottieAnimationView deleteAnimation;
    LinearLayout deleteMember;

    public member_ViewHolder(@NonNull View itemView) {
        super(itemView);
        memberMail = itemView.findViewById(R.id.memberMail);
        deleteMember = itemView.findViewById(R.id.deleteMember);
        deleteAnimation = itemView.findViewById(R.id.deleteAnimation);
    }

    public void bind(String memberName) {
        memberMail.setText(memberName);
    }
}
