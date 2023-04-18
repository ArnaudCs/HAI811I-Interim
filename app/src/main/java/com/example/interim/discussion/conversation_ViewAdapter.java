package com.example.interim.discussion;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.models.Conversation;

import java.util.List;

public class conversation_ViewAdapter extends RecyclerView.Adapter<conversation_ViewHolder> {

    Context context;
    List<Conversation> conversations;

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
        if (conversations.get(position).isUnread()) {
            holder.unRead.setVisibility(View.VISIBLE);
            holder.openConv.setVisibility(View.GONE);
        }
        else {
            holder.unRead.setVisibility(View.GONE);
            holder.openConv.setVisibility(View.VISIBLE);
        }

//        holder.deleteConvBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.checkDelete.setVisibility(View.VISIBLE);
//                holder.cancelDelete.setVisibility(View.VISIBLE);
//                holder.deleteConvBtn.setVisibility(View.GONE);
//                holder.validateDelete.setVisibility(View.VISIBLE);
//                holder.makeGroupBtn.setVisibility(View.GONE);
//                holder.conversationContainer.setWeightSum(6);
//            }
//        });


//        holder.cancelDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.checkDelete.setVisibility(View.GONE);
//                holder.cancelDelete.setVisibility(View.GONE);
//                holder.validateDelete.setVisibility(View.GONE);
//                holder.makeGroupBtn.setVisibility(View.VISIBLE);
//                holder.deleteConvBtn.setVisibility(View.VISIBLE);
//                holder.conversationContainer.setWeightSum(5);
//            }
//        });

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
