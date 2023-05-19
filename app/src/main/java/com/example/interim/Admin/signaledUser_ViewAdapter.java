package com.example.interim.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.discussion.conversation_ViewHolder;

public class signaledUser_ViewAdapter extends RecyclerView.Adapter<signaledUser_ViewHolder> {

    Context context;

    blockedUser_ViewHolder holder;

    public signaledUser_ViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public signaledUser_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new signaledUser_ViewHolder(LayoutInflater.from(context).inflate(R.layout.element_signaleduser_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull signaledUser_ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
