package com.example.interim.discussion;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;

import java.util.ArrayList;
import java.util.List;

public class member_ViewAdapter extends RecyclerView.Adapter<member_ViewHolder> {

    Context context;
    private ArrayList<String> membersList;

    public member_ViewAdapter(Context context) {
        this.context = context;
        this.membersList = new ArrayList<>();
    }

    @NonNull
    @Override
    public member_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new member_ViewHolder(LayoutInflater.from(context).inflate(R.layout.member_element,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull member_ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String member = membersList.get(position);
        holder.bind(member);

        holder.deleteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.deleteMember.setVisibility(View.GONE);
                holder.deleteAnimation.setVisibility(View.VISIBLE);
                holder.deleteAnimation.setSpeed(2);
                holder.deleteAnimation.playAnimation();

                final int originalPosition = position; // Store the original position

                holder.deleteAnimation.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        // Animation start event
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        // Animation end event
                        membersList.remove(originalPosition); // Use the original position here
                        notifyItemRemoved(originalPosition);
                        notifyItemRangeChanged(originalPosition, membersList.size());
                        holder.deleteMember.setVisibility(View.VISIBLE);
                        holder.deleteAnimation.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        // Animation cancel event
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        // Animation repeat event
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return membersList.size();
    }

    // Méthode pour mettre à jour la liste des membres
    public void updateMembersList(String newMember) {
        if (newMember != null) {
            membersList.add(newMember);
            notifyDataSetChanged();
        }
    }

    public ArrayList<String> getMembersList() {return membersList;}
}
