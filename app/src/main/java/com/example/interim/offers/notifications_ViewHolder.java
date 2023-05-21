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
import org.w3c.dom.Text;

public class notifications_ViewHolder extends RecyclerView.ViewHolder {

    TextView notifTitle, notifText, notifDate;
    LinearLayout deleteNotif;

    public notifications_ViewHolder(@NonNull View itemView) {
        super(itemView);
        notifTitle = itemView.findViewById(R.id.notifTitle);
        notifText = itemView.findViewById(R.id.notifText);
        notifDate = itemView.findViewById(R.id.notifDate);
        deleteNotif = itemView.findViewById(R.id.deleteNotif);
    }
}
