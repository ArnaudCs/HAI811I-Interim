package com.example.interim.Admin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.discussion.conversation_ViewHolder;
import com.example.interim.models.Blocked;
import com.example.interim.models.SignaledOffer;

import java.text.SimpleDateFormat;
import java.util.List;

public class blockedUser_ViewAdapter extends RecyclerView.Adapter<blockedUser_ViewHolder> {

        Context context;

        List<Blocked> blockedList;

        Activity mActivity;

        blockedUser_ViewHolder holder;

        public blockedUser_ViewAdapter(Context context, List<Blocked> blockedList, Activity activity) {
            this.context = context;
            this.blockedList = blockedList;
            this.mActivity = activity;
        }

        @NonNull
        @Override
        public blockedUser_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new blockedUser_ViewHolder(LayoutInflater.from(context).inflate(R.layout.element_blockeduser_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull blockedUser_ViewHolder holder, int position) {
                Blocked block = blockedList.get(position);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = sdf.format(block.getDate());
                holder.blockDate.setText(formattedDate);
                holder.blockedMail.setText(block.getBlockedMail());
                holder.blockerMail.setText(block.getBlockerMail());

                holder.UnblockUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                // Code ici pour supprimer le bloquage de la bdd comme ça les deux personnes peuvent se parler
                        }
                });
        }

        @Override
        public int getItemCount() {
            return blockedList.size();
        }
}
