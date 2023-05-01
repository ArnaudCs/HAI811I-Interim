package com.example.interim.offers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.models.Application;
import com.example.interim.models.Offer;

import java.util.List;

public class applicationOverview_ViewAdapter extends RecyclerView.Adapter<applicationOverview_ViewHolder> {
    Context context;
    List<Application> applications;

    public applicationOverview_ViewAdapter(Context context, List<Application> applications) {
        this.context = context;
        this.applications = applications;
    }


    @NonNull
    @Override
    public applicationOverview_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new applicationOverview_ViewHolder(LayoutInflater.from(context).inflate(R.layout.application_overview_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull applicationOverview_ViewHolder holder, int position) {
        holder.applicantName.setText(applications.get(position).getApplicantName());
        holder.applicantEmail.setText(applications.get(position).getApplicantMail());
        holder.applicantNumber.setText(applications.get(position).getApplicantPhone());
//        holder.jobTitle.setText(applications.get(position).getOffer().getJobTitle());
        holder.applicationDate.setText(applications.get(position).getApplicantBirth());
        holder.applicantAdress.setText(applications.get(position).getApplicantAdress());
        holder.applicantId = applications.get(position).getApplicantId();
        holder.appId = applications.get(position).getId();

    }

    @Override
    public int getItemCount() {
        return applications.size();
    }
}
