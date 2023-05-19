package com.example.interim.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.discussion.conversation_ViewHolder;

public class blockedUser_ViewAdapter extends RecyclerView.Adapter<blockedUser_ViewHolder> {

        Context context;

        blockedUser_ViewHolder holder;

        public blockedUser_ViewAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public blockedUser_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new blockedUser_ViewHolder(LayoutInflater.from(context).inflate(R.layout.element_blockeduser_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull blockedUser_ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
}
