package com.example.interim;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieResult;
import com.example.interim.models.Offer;

import java.util.List;

public class searchCard_ViewAdapter extends RecyclerView.Adapter<searchCard_ViewHolder> {

    Context context;
    List<Offer> offers;

    public searchCard_ViewAdapter(Context context, List<Offer> offers) {
        this.context = context;
        this.offers = offers;
    }

    @NonNull
    @Override
    public searchCard_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new searchCard_ViewHolder(LayoutInflater.from(context).inflate(R.layout.searchcard_element_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull searchCard_ViewHolder holder, int position) {
        holder.jobTitle.setText(offers.get(position).getJobTitle());
        holder.companyName.setText(offers.get(position).getCompanyName());
        holder.jobCategory.setText(offers.get(position).getCategory());
        holder.jobDate.setText("From " + offers.get(position).getStartDate().toString() + " to " + offers.get(position).getEndDate().toString());
        holder.jobUrl.setText(offers.get(position).getUrl());
        holder.jobSalary.setText(String.valueOf(offers.get(position).getSalaryMax()) + "€");
        holder.jobLocation.setText(offers.get(position).getLocation());
        holder.postDate.setText(offers.get(position).getPostDate().toString());
        holder.jobKeywords.setText(offers.get(position).getKeywords());

        holder.likeInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.liked) {
                    holder.likeInit.setVisibility(View.GONE);
                    holder.likeBtn.setVisibility(View.VISIBLE);
                    holder.likeBtn.setAnimation(R.raw.like);
                    holder.likeBtn.playAnimation();
                    holder.liked = !holder.liked;
                }
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.liked){
                    holder.likeBtn.setAnimation(R.raw.like);
                    holder.likeBtn.playAnimation();
                    holder.liked = !holder.liked;
                } else {
                    holder.likeBtn.setAnimation(R.raw.dislike);
                    holder.likeBtn.playAnimation();
                    holder.liked = !holder.liked;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }
}
