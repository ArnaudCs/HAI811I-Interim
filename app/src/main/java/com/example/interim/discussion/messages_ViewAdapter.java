package com.example.interim.discussion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class messages_ViewAdapter extends RecyclerView.Adapter<messages_ViewHolder>{

    String userId;

    private static final int VIEW_TYPE_SENT = 0;
    private static final int VIEW_TYPE_RECEIVED = 1;
    Context context;
    List<Message> messages;

    public messages_ViewAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @NonNull
    @Override
    public messages_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.message_sent_element_layout, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_receive_element_layout, parent, false);
        }
        return new messages_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull messages_ViewHolder holder, int position) {
        holder.date.setText(messages.get(position).getDate().toString());
        holder.text.setText(messages.get(position).getText().toString());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(userId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }
}
