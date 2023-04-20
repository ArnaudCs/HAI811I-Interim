package com.example.interim.discussion;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.models.Conversation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class conversation_ViewAdapter extends RecyclerView.Adapter<conversation_ViewHolder> {

    Context context;
    List<Conversation> conversations;

    String participantId;

    String participant;

    conversation_ViewHolder holder;

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
        this.holder = holder;
        holder.conversationId = conversations.get(position).getId();
        holder.userName.setText(conversations.get(position).getContact());
        holder.lastMsg.setText(conversations.get(position).getLastMsg());
        participantId = conversations.get(position).getContactUid();

        StorageReference mStorageRef;
        DatabaseReference mDatabaseRef;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserUid = mAuth.getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference().child("uploads/" + participantId);
        try {
            final File localFile = File.createTempFile("profilePic", "jpg");
            mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Uri fileUri = Uri.fromFile(localFile);
                    String imageUrl = fileUri.toString();
                    Picasso.with(context).load(imageUrl).fit().centerCrop().into(holder.profilePic);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

        if (conversations.get(position).isUnread()) {
            holder.unRead.setVisibility(View.VISIBLE);
            holder.openConv.setVisibility(View.GONE);
        }
        else {
            holder.unRead.setVisibility(View.GONE);
            holder.openConv.setVisibility(View.VISIBLE);
        }

        holder.goToMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String conversationId = holder.conversationId; // Get the conversation ID here
                Intent discussion = new Intent(context, DiscussionViewActivity.class);
                discussion.putExtra("conversationId", conversationId);
                context.startActivity(discussion);
            }
        });
    }

    public void deleteConversation() {
        holder.checkDelete.setVisibility(View.VISIBLE);
        holder.cancelDelete.setVisibility(View.VISIBLE);
        holder.deleteConvBtn.setVisibility(View.GONE);
        holder.validateDelete.setVisibility(View.VISIBLE);
        holder.makeGroupBtn.setVisibility(View.GONE);
        holder.conversationContainer.setWeightSum(6);
    }

    public void cancelDelete() {
        holder.checkDelete.setVisibility(View.GONE);
        holder.cancelDelete.setVisibility(View.GONE);
        holder.validateDelete.setVisibility(View.GONE);
        holder.makeGroupBtn.setVisibility(View.VISIBLE);
        holder.deleteConvBtn.setVisibility(View.VISIBLE);
        holder.conversationContainer.setWeightSum(5);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }
}
