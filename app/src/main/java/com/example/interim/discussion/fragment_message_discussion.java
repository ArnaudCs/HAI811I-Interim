package com.example.interim.discussion;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.InputFilter;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.interim.Utils.DataHolder;
import com.example.interim.R;
import com.example.interim.models.Blocked;
import com.example.interim.models.Message;
import com.example.interim.models.Signal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class fragment_message_discussion extends Fragment {
    String conversationId;

    List<Message> messages;
    private boolean isRefreshing = false;


    private Handler mHandler;

    Message lastMessageUnread;
    private int numMessages;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    RecyclerView recyclerView;
    Boolean group = false;
    String senderId;

    Button deleteBtn, signalUserBtn, blockUserBtn;
    private Runnable mRunnable;
    public fragment_message_discussion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_discussion, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Arrêter le rafraîchissement si l'état est en cours de rafraîchissement
        if (isRefreshing) {
            stopRefreshingConversation();
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        conversationId = DataHolder.getInstance().getConversationId();
        TextView convName = view.findViewById(R.id.convName);
        Button infosBtn = view.findViewById(R.id.infosBtn);
        Button closeInfos = view.findViewById(R.id.closeInfos);
        Button signalUser = view.findViewById(R.id.signalUserBtn);
        Button blockUser = view.findViewById(R.id.signalUserBtn);
        Button backBtnDiscussionView = view.findViewById(R.id.backBtnDiscussionView);
        LinearLayout infosContainer = view.findViewById(R.id.infosContainer);
        recyclerView = view.findViewById(R.id.messagesContainer);
        LottieAnimationView sendMsg = view.findViewById(R.id.sendMessage);
        EditText messageText = view.findViewById(R.id.messageText);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        signalUserBtn = view.findViewById(R.id.signalUserBtn);
        blockUserBtn = view.findViewById(R.id.blockUserBtn);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Get the list of message IDs from the conversation document
        DocumentReference conversationRef = db.collection("Conversations").document(conversationId);
        convName.setText("Participant names");
        final String[] type = new String[1];
        type[0] = "Users";
        db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        type[0] = "Users";
                    } else {
                        type[0] = "Pros";
                    }

                    // Retrieve conversation using the updated type value
                    conversationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    List<String> messageIds = (List<String>) document.get("messages");
                                    List<DocumentReference> participants = (List<DocumentReference>) document.get("participants");
                                    if (participants != null) {
                                        if(participants.size() > 2) {
                                            group = true;
                                            convName.setText(document.getString("groupName"));
                                        }
                                        else {
                                            getParticipantsNames(participants, convName, userId);
                                        }

                                        List<DocumentReference> unReadRefs = (List<DocumentReference>) document.get("unRead");
                                        if (unReadRefs != null) {
                                            for (int i = unReadRefs.size() - 1; i >= 0; i--) {
                                                if (unReadRefs.get(i).equals(db.collection(type[0]).document(userId))) {
                                                    unReadRefs.remove(i);
                                                }
                                            }
                                            conversationRef.update("unRead", unReadRefs);
                                        }
                                    }

                                    // Get the list of messages using the message IDs
                                    if (messageIds != null) {
                                        CollectionReference messagesRef = db.collection("Messages");

                                        List<List<String>> messageIdChunks = chunkList(messageIds, 10);

                                        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                                        for (List<String> chunk : messageIdChunks) {
                                            Query query = messagesRef.whereIn(FieldPath.documentId(), chunk);
                                            tasks.add(query.get());
                                        }

                                        Tasks.whenAllSuccess(tasks).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                                            @Override
                                            public void onComplete(@NonNull Task<List<Object>> task) {
                                                messages = new ArrayList<>();
                                                for (Object result : task.getResult()) {
                                                    QuerySnapshot querySnapshot = (QuerySnapshot) result;
                                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                                        senderId = document.getString("sender");
                                                        String text = document.getString("text");
                                                        Date date = document.getDate("date");
                                                        Message message = new Message(senderId, date, text);
                                                        messages.add(message);
                                                    }
                                                }
                                                messages.sort(new Comparator<Message>() {
                                                    @Override
                                                    public int compare(Message m1, Message m2) {
                                                        return m1.getDate().compareTo(m2.getDate());
                                                    }
                                                });

                                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                                layoutManager.setStackFromEnd(true);
                                                recyclerView.setLayoutManager(layoutManager);
                                                recyclerView.setAdapter(new messages_ViewAdapter(getContext(), messages, group));

                                                recyclerView.smoothScrollToPosition(messages.size());
                                                startRefreshingMessages();
                                                // Do something with the list of messages
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("ERROR: " + e.getMessage());
                                            }
                                        });
                                    }
                                } else {
                                    // The conversation document does not exist
                                }
                            } else {
                                // Handle failure to retrieve the conversation document
                            }
                        }
                    });
                } else {
                    // Handle failure to retrieve the user document
                }
            }
        });

        signalUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.signalTitle));
                final EditText input = new EditText(getActivity());
                int paddingPixels = (int) (20 * getResources().getDisplayMetrics().density);
                input.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);
                input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(250) });
                builder.setView(input);
                builder.setPositiveButton(getString(R.string.nextBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String signalReason = input.getText().toString();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Date today = new Date();
                        db.collection("Users").document(senderId).get().addOnCompleteListener(
                                new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String email = document.getString("email");
                                                Signal signal = new Signal(mAuth.getCurrentUser().getUid(), senderId, mAuth.getCurrentUser().getEmail(), email, signalReason, today);
                                                db.collection("Signaled").add(signal);
                                            }
                                            else {
                                                db.collection("Pros").document(senderId).get().addOnCompleteListener(
                                                        new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.exists()) {
                                                                        String email = document.getString("email");
                                                                        Signal signal = new Signal(mAuth.getCurrentUser().getUid(), senderId, mAuth.getCurrentUser().getEmail(), email, signalReason, today);
                                                                        db.collection("Signaled").add(signal);
                                                                    }
                                                                    else {
                                                                        Log.e(TAG, "Email not found ! ", task.getException());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                    }
                                }
                        );

                        Toast.makeText(getContext(), getString(R.string.signaledUserToast), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                int dialogPadding = (int) (20 * getResources().getDisplayMetrics().density);
                dialog.getWindow().getDecorView().setPadding(dialogPadding, dialogPadding, dialogPadding, dialogPadding);
            }
        });

        blockUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.blockUserDialog));
                builder.setMessage(getString(R.string.blockDialogText));
                builder.setPositiveButton(getString(R.string.nextBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Date today = new Date();

                        db.collection("Users").document(senderId).get().addOnCompleteListener(
                                new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String email = document.getString("email");
                                                Blocked blocked = new Blocked(senderId, mAuth.getCurrentUser().getUid(), today, email, mAuth.getCurrentUser().getEmail());
                                                Toast.makeText(getContext(), getString(R.string.blockedUserToast), Toast.LENGTH_SHORT).show();
                                                db.collection("Blocked").add(blocked);
                                                db.collection("Conversations").document(conversationId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {

                                                                List<DocumentReference> messagesRef = (List<DocumentReference>) document.get("messages");
                                                                if (messagesRef != null) {
                                                                    for (DocumentReference message : messagesRef) {
                                                                        message.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "Message deleted successfully!");
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                                db.collection("Conversations").document(conversationId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Log.d(TAG, "Conversation deleted !");
                                                                        getActivity().finish();
                                                                        stopRefreshingConversation();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                });

                                            }
                                            else {
                                                db.collection("Pros").document(senderId).get().addOnCompleteListener(
                                                        new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.exists()) {
                                                                        String email = document.getString("email");
                                                                        Blocked blocked = new Blocked(senderId, mAuth.getCurrentUser().getUid(), today, email, mAuth.getCurrentUser().getEmail());
                                                                        Toast.makeText(getContext(), getString(R.string.blockedUserToast), Toast.LENGTH_SHORT).show();
                                                                        db.collection("Blocked").add(blocked);
                                                                        db.collection("Conversations").document(conversationId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentSnapshot document = task.getResult();
                                                                                    if (document.exists()) {

                                                                                        List<DocumentReference> messagesRef = (List<DocumentReference>) document.get("messages");
                                                                                        if (messagesRef != null) {
                                                                                            for (DocumentReference message : messagesRef) {
                                                                                                message.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        Log.d(TAG, "Message deleted successfully!");
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }

                                                                                        db.collection("Conversations").document(conversationId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                Log.d(TAG, "Conversation deleted !");
                                                                                                getActivity().finish();
                                                                                                stopRefreshingConversation();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                    else {
                                                                        Log.e(TAG, "Email not found ! ", task.getException());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                    }
                                }
                        );

                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infosContainer.setVisibility(View.GONE);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirmation");
                builder.setMessage(getContext().getResources().getString(R.string.deleteMessageWarning));
                builder.setPositiveButton(getContext().getResources().getString(R.string.yesBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        db.collection("Conversations").document(conversationId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        List<DocumentReference> messagesRef = (List<DocumentReference>) document.get("messages");
                                        if (messagesRef != null) {
                                            for (DocumentReference message : messagesRef) {
                                                message.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Message deleted successfully!");
                                                    }
                                                });
                                            }
                                        }

                                        db.collection("Conversations").document(conversationId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d(TAG, "Conversation deleted !");
                                                getActivity().finish();
                                                stopRefreshingConversation();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton(getContext().getResources().getString(R.string.noBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        backBtnDiscussionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                stopRefreshingConversation();
            }
        });


        infosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(infosContainer);
                infosContainer.setVisibility(View.VISIBLE);
                infosBtn.setVisibility(View.GONE);
                closeInfos.setVisibility(View.VISIBLE);
            }
        });

        closeInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(infosContainer);
                infosContainer.setVisibility(View.GONE);
                infosBtn.setVisibility(View.VISIBLE);
                closeInfos.setVisibility(View.GONE);
            }
        });

        android.app.Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.discussionContainer);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg.playAnimation();
                // Get the message text
                String text = messageText.getText().toString();

                // Create a new Message object
                Message message = new Message(userId, new Date(), text);

                // Add the message to the Messages collection
                db.collection("Messages").add(message)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference messageRef) {
                                String messageId = messageRef.getId();
                                // Add a reference to the message in the conversation document
                                DocumentReference conversationRef = db.collection("Conversations").document(conversationId);
                                conversationRef.update("lastMessage", text);
                                conversationRef.update("messages", FieldValue.arrayUnion(messageRef))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            conversationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            List<DocumentReference> participantsRefs = (List<DocumentReference>) document.get("participants");
                                                            List<DocumentReference> unReadRefs = (List<DocumentReference>) document.get("unRead");
                                                            if (unReadRefs == null) {
                                                                unReadRefs = new ArrayList<>();
                                                            }
                                                            for (DocumentReference participantRef : participantsRefs) {
                                                                String participantId = participantRef.getId();
                                                                if (!participantId.equals(userId)) {
                                                                    boolean userAlreadyRead = false;
                                                                    for (DocumentReference unReadRef : unReadRefs) {
                                                                        if (unReadRef.getId().equals(participantId)) {
                                                                            userAlreadyRead = true;
                                                                            break;
                                                                        }
                                                                    }
                                                                    if (!userAlreadyRead) {
                                                                        conversationRef.update("unRead", FieldValue.arrayUnion(participantRef));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                }
                                            });

                                            int position = recyclerView.getAdapter().getItemCount();
                                            recyclerView.getAdapter().notifyItemInserted(position);
                                            messages.add(message);
                                            recyclerView.setAdapter(new messages_ViewAdapter(getContext(), messages, group));
                                            messageText.getText().clear();
                                        }
                                    });
                            }
                        });
            }
        });
    }

    // Fonction pour initialiser le Handler et le Runnable
    private void startRefreshingMessages() {
        isRefreshing = true;
        mHandler = new Handler();
        //System.out.println("Start refresh");
        mRunnable = new Runnable() {
            @Override
            public void run() {
                getLastMessage();
                //System.out.println("refresh");
                mHandler.postDelayed(this, 3000); // Actualisation toutes les 3 secondes
            }
        };
        mHandler.post(mRunnable);
    }


    private void stopRefreshingConversation() {
        isRefreshing = false;
        //System.out.println("Stopping du refresh");
        mHandler.removeCallbacks(mRunnable);
    }

    // Fonction pour actualiser les messages depuis la base de données

    private void getLastMessage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference conversationRef = db.collection("Conversations").document(conversationId);
        conversationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> messageIds = (List<String>) document.get("messages");

                        // Get the list of messages using the message IDs
                        if (messageIds.size() != 0) {
                            CollectionReference messagesRef = db.collection("Messages");

                            Query query = messagesRef.whereIn(FieldPath.documentId(), messageIds)
                                    .orderBy("date", Query.Direction.DESCENDING)
                                    .limit(1);

                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                        // Do something with the last message
                                        if (!documents.isEmpty()) {
                                            DocumentSnapshot lastMessageDoc = documents.get(0);
                                            String senderId = lastMessageDoc.getString("sender");
                                            String text = lastMessageDoc.getString("text");
                                            Date date = lastMessageDoc.getDate("date");
                                            Message lastMessage = new Message(senderId, date, text);

                                            boolean isDuplicate = false;
                                            for (Message message : messages) {
                                                if (message.getmId() == lastMessage.getmId() &&
                                                        message.getDate().equals(lastMessage.getDate()) &&
                                                        message.getText().equals(lastMessage.getText())) {
                                                    isDuplicate = true;
                                                    break;
                                                }
                                            }

                                            if (!isDuplicate) {
                                                messages.add(lastMessage);
                                                int position = recyclerView.getAdapter().getItemCount();
                                                recyclerView.getAdapter().notifyItemInserted(position);
                                                recyclerView.smoothScrollToPosition(messages.size());
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            // There are no messages in the conversation
                        }
                    } else {
                        // The conversation document does not exist
                    }
                } else {
                    // Handle failure to retrieve the conversation document
                }
            }
        });

    }


    private void getParticipantsNames(List<DocumentReference> participants, TextView convName, String userId) {
        StringBuilder sb = new StringBuilder();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i = 0; i < participants.size(); i++) {
            DocumentReference userRef = participants.get(i);
            int finalI = i;
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String uid = documentSnapshot.getId();
                        if (!uid.equals(userId)) {
                            if (name != null) {
                                sb.append(name);
                                if (finalI < participants.size() - 1) {
                                    sb.append(", ");
                                }


                                // Remove trailing ", " if it exists
                                if (sb.length() >= 2 && sb.substring(sb.length() - 2).equals(", ")) {
                                    sb.delete(sb.length() - 2, sb.length());
                                }

                                convName.setText(sb.toString());
                            }
                        }
                    }
                }
            });
        }


    }



    private static <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        int index = 0;
        while (index < list.size()) {
            chunks.add(list.subList(index, Math.min(index + chunkSize, list.size())));
            index += chunkSize;
        }
        return chunks;
    }

}