package com.example.interim.Admin;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.discussion.conversation_ViewHolder;
import com.example.interim.models.Signal;
import com.example.interim.models.SignaledOffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class signaledUser_ViewAdapter extends RecyclerView.Adapter<signaledUser_ViewHolder> {

    Context context;
    List<Signal> signaled;

    Activity mActivity;
    String signaledId, signalerId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    blockedUser_ViewHolder holder;

    public signaledUser_ViewAdapter(Context context, List<Signal> signaled, Activity activity) {
        this.context = context;
        this.signaled = signaled;
        mActivity = activity;
    }

    @NonNull
    @Override
    public signaledUser_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new signaledUser_ViewHolder(LayoutInflater.from(context).inflate(R.layout.element_signaleduser_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull signaledUser_ViewHolder holder, int position) {
        Signal signal = signaled.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(signal.getSignalDate());
        holder.signaledDate.setText(formattedDate);
        holder.signalReason.setText(signal.getReason());
        signaledId = signal.getSignaledId();
        signalerId = signal.getSignalerId();
        holder.signalerMail.setText(signal.getSignalerMail());
    }

    @Override
    public int getItemCount() {
        return signaled.size();
    }
}
