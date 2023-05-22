package com.example.interim.Admin;

import static com.google.firebase.messaging.Constants.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.discussion.conversation_ViewHolder;
import com.example.interim.models.Blocked;
import com.example.interim.models.SignaledOffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

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
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                holder.UnblockUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Confirmation");
                                builder.setMessage("Are you sure you want to delete this blocking?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                // Delete the blocked item
                                                db.collection("Blocked")
                                                        .document(block.getBlockId())
                                                        .delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                        Log.e(TAG, "Block deleted");
                                                                }
                                                        });
                                        }
                                });
                                builder.setNegativeButton("No", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();

                        }
                });
        }

        @Override
        public int getItemCount() {
            return blockedList.size();
        }
}
