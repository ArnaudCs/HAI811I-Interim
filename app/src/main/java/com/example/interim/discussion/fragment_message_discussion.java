package com.example.interim.discussion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.interim.DataHolder;
import com.example.interim.R;
import com.example.interim.models.Message;
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
    public fragment_message_discussion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_discussion, container, false);
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
        RecyclerView recyclerView = view.findViewById(R.id.messagesContainer);
        Button sendMsg = view.findViewById(R.id.sendMessage);
        EditText messageText = view.findViewById(R.id.messageText);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
// Get the list of message IDs from the conversation document
        DocumentReference conversationRef = db.collection("Conversations").document(conversationId);
        convName.setText("Participant names");
        conversationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> messageIds = (List<String>) document.get("messages");
                        List<DocumentReference> participants = (List<DocumentReference>) document.get("participants");
                        if (participants != null) {
                            getParticipantsNames(participants, convName, userId);
                        }

                        // Get the list of messages using the message IDs
                        if(messageIds != null) {
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
                                    List<Message> messages = new ArrayList<>();
                                    for (Object result : task.getResult()) {
                                        QuerySnapshot querySnapshot = (QuerySnapshot) result;
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            String senderId = document.getString("sender");
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
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    recyclerView.setAdapter(new messages_ViewAdapter(getContext(), messages));
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
                    }
                } else {
                }
            }
        });

        backBtnDiscussionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
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


        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                                // Add a reference to the message in the unRead array for each participant
                                                conversationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                List<DocumentReference> participantsRefs = (List<DocumentReference>) document.get("participants");
                                                                for (DocumentReference participantRef : participantsRefs) {
                                                                    String participantId = participantRef.getId();
                                                                    if (!participantId.equals(userId)) {
                                                                        participantRef.update("unRead", FieldValue.arrayUnion(conversationRef));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                });

                                                // Refresh the layout to display the new message
                                                recyclerView.getAdapter().notifyDataSetChanged();
                                                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                                messageText.getText().clear();
                                            }
                                        });
                            }
                        });
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
                        if(!uid.equals(userId)) {
                            if (name != null) {
                                sb.append(name);
                                if (finalI < participants.size() - 1) {
                                    sb.append(", ");
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