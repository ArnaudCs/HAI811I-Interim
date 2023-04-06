package com.example.interim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.models.Conversation;

import java.util.List;

public class conversation_ViewAdapter extends RecyclerView.Adapter<conversation_ViewHolder> {

    Context context;
    List<Conversation> conversations;

    public conversation_ViewAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public conversation_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new conversation_ViewHolder(LayoutInflater.from(context).inflate(R.layout.conversation_menu_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull conversation_ViewHolder holder, int position) {
        holder.userName.setText(conversations.get(position).getContact());
        holder.lastMsg.setText(conversations.get(position).getLastMsg());
        if (conversations.get(position).isUnread()) {
            holder.unRead.setVisibility(View.VISIBLE);
            holder.openConv.setVisibility(View.GONE);
        }
        else {
            holder.unRead.setVisibility(View.GONE);
            holder.openConv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }
}
