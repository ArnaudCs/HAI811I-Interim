package com.example.interim.Admin;

import static com.google.firebase.messaging.Constants.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.discussion.NewMessageConversationActivity;
import com.example.interim.discussion.conversation_ViewHolder;
import com.example.interim.models.Notification;
import com.example.interim.models.Signal;
import com.example.interim.models.SignaledOffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class signaledUser_ViewAdapter extends RecyclerView.Adapter<signaledUser_ViewHolder> {

    Context context;
    List<Signal> signaled;

    Activity mActivity;
    String signaledId, signalerId, signaledMail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        signaledMail = signal.getSignalerMail();
        holder.signalerMail.setText(context.getString(R.string.signaledBy) + signal.getSignalerMail());
        holder.signaledMail.setText(context.getString(R.string.signaled) + signal.getSignaledMail());

        holder.deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.signaledUserDeleteAlert));
                builder.setMessage(context.getString(R.string.SignaledUserDeleteText));
                builder.setPositiveButton(context.getString(R.string.nextBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Delete the blocked item
                        db.collection("Signaled")
                                .document(signal.getSignalId())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e(TAG, "Signalment deleted");
                                    }
                                });
                    }
                });
                builder.setNegativeButton(context.getString(R.string.cancelBtn), null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        holder.sendWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String warningMessage = context.getString(R.string.signaledUser);
                Intent newConversation = new Intent(context, NewMessageConversationActivity.class);
                newConversation.putExtra("mail", signaledMail);
                newConversation.putExtra("message", warningMessage);
                context.startActivity(newConversation);
                Date today = new Date();
                Notification notification = new Notification(warningMessage, context.getString(R.string.signaledReceived), today, signaledId);
                db.collection("Notifications").add(notification);
                mActivity.finish();
            }
        });

        holder.messageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newConversation = new Intent(context, NewMessageConversationActivity.class);
                newConversation.putExtra("mail", signaledMail);
                context.startActivity(newConversation);
                mActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return signaled.size();
    }
}
