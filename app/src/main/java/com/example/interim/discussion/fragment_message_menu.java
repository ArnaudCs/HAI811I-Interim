package com.example.interim.discussion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.interim.R;
import com.example.interim.models.Conversation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class fragment_message_menu extends Fragment {

    private conversation_ViewAdapter mAdapter;
    Button deleteMessages, cancelDelete;

    public fragment_message_menu() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_menu, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deleteMessages = view.findViewById(R.id.deleteMessages);
        cancelDelete = view.findViewById(R.id.cancelDelete);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        List<Conversation> conversations = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.conversationsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new conversation_ViewAdapter(getContext(), conversations);
        recyclerView.setAdapter(mAdapter);


        db.collection("Conversations")
                .whereArrayContains("participants", db.collection("Users").document(userId))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final String[] participantName = new String[1];
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extract the Conversation data from the Firestore document
                                String otherParticipant = "";
                                String lastMessage = "";
                                Conversation conversation = new Conversation(document.getId(), otherParticipant, false, lastMessage);
                                List<DocumentReference> participantsRefs = (List<DocumentReference>) document.get("participants");

                                for (DocumentReference participantRef : participantsRefs) {
                                    String participantId = participantRef.getId();
                                    if (!participantId.equals(userId)) {
                                        // Get the other participant's name from the Users or Pros collection
                                        String participantType = participantRef.getParent().getId();

                                        if (participantType.equals("Users")) {
                                            DocumentReference participantDocRef = db.collection("Users").document(participantId);
                                            participantDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot participantDoc = task.getResult();
                                                        participantName[0] = participantDoc.getString("firstName");
                                                        conversation.setContact(participantName[0]);

                                                        // Set the RecyclerView adapter with the updated list of Conversations
                                                        mAdapter.notifyDataSetChanged();
                                                        recyclerView.setAdapter(mAdapter);

                                                    }
                                                }
                                            });
                                        } else if (participantType.equals("Pros")) {
                                            DocumentReference participantDocRef = db.collection("Pros").document(participantId);
                                            participantDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot participantDoc = task.getResult();
                                                        participantName[0] = participantDoc.getString("name");
                                                        conversation.setContact(participantName[0]);

                                                        // Set the RecyclerView adapter with the updated list of Conversations
                                                        mAdapter.notifyDataSetChanged();
                                                        recyclerView.setAdapter(mAdapter);

                                                    }
                                                }
                                            });
                                        }

//                                        conversation.setContact(participantName[0]);
                                        break;
                                    }
                                }

                                List<DocumentReference> unReadRef = (List<DocumentReference>) document.get("unRead");
                                for (DocumentReference userUnReadRef : unReadRef) {
                                    String userUnReadId = userUnReadRef.getId();
                                    if (userUnReadId.equals(userId)) {
                                        conversation.setUnread(true);
                                        break;
                                    }
                                }
                                if (document.contains("lastMessage")) {
                                    lastMessage = document.getString("lastMessage");
                                    conversation.setLastMsg(lastMessage);
                                }
                                conversations.add(conversation);
                            }
                            // Set the RecyclerView adapter with the list of Conversations
                            RecyclerView recyclerView = view.findViewById(R.id.conversationsRecycler);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new conversation_ViewAdapter(getContext(), conversations));
                        } else {
                            Log.e("TAG", "No conversations to retrieve");
                        }
                    }
                });


        deleteMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.deleteConversation();
            }
        });

        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.cancelDelete();
            }
        });
    }



}