package com.example.interim.offers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.discussion.conversation_ViewHolder;
import com.example.interim.models.Notification;

import java.text.SimpleDateFormat;
import java.util.List;

public class notifications_ViewAdapter extends RecyclerView.Adapter<notifications_ViewHolder> {

    Context context;
    conversation_ViewHolder holder;
    List<Notification> notifications;

    public notifications_ViewAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    public void notifications_ViewHolder(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public notifications_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new notifications_ViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_element_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull notifications_ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.notifTitle.setText(notification.getNotificationTitle());
        holder.notifText.setText(notification.getNotificationText());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(notification.getNotificationDate());
        holder.notifDate.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        if (notifications != null) {
            return notifications.size();
        } else {
            return 0;
        }
    }

}
