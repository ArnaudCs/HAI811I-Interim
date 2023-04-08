package com.example.interim;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class messages_ViewHolder extends RecyclerView.ViewHolder {
    TextView date;
    TextView text;

    public messages_ViewHolder(@NonNull View itemView) {
        super(itemView);

        date = itemView.findViewById(R.id.date);
        text = itemView.findViewById(R.id.text);

    }
}
