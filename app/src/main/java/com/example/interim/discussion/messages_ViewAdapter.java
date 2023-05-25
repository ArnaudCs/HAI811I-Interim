package com.example.interim.discussion;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.models.Message;
import com.example.interim.models.Signal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class messages_ViewAdapter extends RecyclerView.Adapter<messages_ViewHolder>{

    String userId;
    String senderName;
    String senderId;

    private static final int VIEW_TYPE_SENT = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final int VIEW_TYPE_RECEIVED = 1;
    Context context;
    List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

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
    public void onBindViewHolder(@NonNull messages_ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE d MMM HH'h'mm", Locale.getDefault());
        String formattedDate = formatter.format(messages.get(position).getDate());
        senderId = messages.get(position).getSender();
        holder.date.setText(formattedDate);
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
