package com.example.interim.discussion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.interim.R;
import com.example.interim.authentication.MainActivity;
import com.example.interim.models.Conversation;
import com.example.interim.offers.ApplicationActivity;
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
import java.util.Objects;

public class fragment_message_menu extends Fragment {

    private conversation_ViewAdapter mAdapter;
    private Runnable mRunnable;
    private boolean refreshing = false;

    private Handler mHandler;
    RecyclerView recyclerView;
    Button deleteMessages, cancelDelete, newConvBtn, groupMaking;
    String type, userId;
    FirebaseFirestore db;
    List<Conversation> conversations;

    public fragment_message_menu() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            Intent mainActivity = new Intent(getActivity(), MainActivity.class);
            startActivity(mainActivity);
            this.getActivity().finish();
            return null;
        }
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        return inflater.inflate(R.layout.fragment_message_menu, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deleteMessages = view.findViewById(R.id.deleteMessages);
        cancelDelete = view.findViewById(R.id.cancelDelete);
        groupMaking = view.findViewById(R.id.groupMaking);
        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();



        conversations = new ArrayList<>();
        recyclerView = view.findViewById(R.id.conversationsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new conversation_ViewAdapter(getContext(), conversations);
        recyclerView.setAdapter(mAdapter);
        startRefreshingConversation();
        type = "Pros";

        db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        type = "Users";
                    } else {
                        type = "Pros";
                    }

                    db.collection("Conversations")
                            .whereArrayContains("participants", db.collection(type).document(userId))
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

                                            assert participantsRefs != null;
                                            for (DocumentReference participantRef : participantsRefs) {
                                                String participantId = participantRef.getId();
                                                conversation.setContactUid(participantId);
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
                                                                    conversation.setContactUid(participantId);
                                                                    System.out.println(participantId + "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");

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

                                            if (document.contains("unRead")) {
                                                List<DocumentReference> unReadRefs = (List<DocumentReference>) document.get("unRead");

                                                if (unReadRefs != null && unReadRefs.contains(db.collection(type).document(userId))) {
                                                    // User has unread messages in this conversation
                                                    conversation.setUnread(true);
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
                }
            }
        });

        newConvBtn = view.findViewById(R.id.newConvBtn);

        newConvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newConversation = new Intent(getContext(), NewMessageConversationActivity.class);
                getContext().startActivity(newConversation);
            }
        });

        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.cancelDelete();
            }
        });

        groupMaking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent groupCreation = new Intent(getContext(), GroupActivity.class);
                getContext().startActivity(groupCreation);
            }
        });
    }

    private void startRefreshingConversation() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                // Mettre à jour immédiatement le RecyclerView avant d'entrer dans la boucle de rafraîchissement
                mAdapter.notifyDataSetChanged();

                // Récupérer à nouveau les conversations de la base de données
                db.collection("Conversations")
                        .whereArrayContains("participants", db.collection(type).document(userId))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Vider la liste des conversations
                                    conversations.clear();

                                    // Parcourir les documents de conversations récupérés
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Extraire les données de la conversation du document Firestore
                                        String otherParticipant = "";
                                        String lastMessage = "";
                                        Conversation conversation = new Conversation(document.getId(), otherParticipant, false, lastMessage);
                                        List<DocumentReference> participantsRefs = (List<DocumentReference>) document.get("participants");

                                        assert participantsRefs != null;
                                        for (DocumentReference participantRef : participantsRefs) {
                                            String participantId = participantRef.getId();
                                            conversation.setContactUid(participantId);
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
                                                                String participantName = participantDoc.getString("firstName");
                                                                conversation.setContact(participantName);
                                                                conversation.setContactUid(participantId);

                                                                // Mettre à jour l'adaptateur et le RecyclerView
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
                                                                String participantName = participantDoc.getString("name");
                                                                conversation.setContact(participantName);

                                                                // Mettre à jour l'adaptateur et le RecyclerView
                                                                mAdapter.notifyDataSetChanged();
                                                                recyclerView.setAdapter(mAdapter);
                                                            }
                                                        }
                                                    });
                                                }
                                                break;
                                            }
                                        }

                                        if (document.contains("unRead")) {
                                            List<DocumentReference> unReadRefs = (List<DocumentReference>) document.get("unRead");

                                            if (unReadRefs != null && unReadRefs.contains(db.collection(type).document(userId))) {
                                                // User has unread messages in this conversation
                                                conversation.setUnread(true);
                                            }
                                        }

                                        if (document.contains("lastMessage")) {
                                            lastMessage = document.getString("lastMessage");
                                            conversation.setLastMsg(lastMessage);
                                        }
                                        conversations.add(conversation);
                                    }
                                    // Mettre à jour l'adaptateur et le RecyclerView avec la liste de conversations mise à jour
                                    mAdapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(mAdapter);
                                } else {
                                    Log.e("TAG", "No conversations to retrieve");
                                }
                            }
                        });

                // Programmer la prochaine exécution du Runnable après 3 secondes
                mHandler.postDelayed(this, 6000);
            }
        };
        refreshing = true;
        mHandler.post(mRunnable);
    }

    @Override
    public void onPause() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            super.onPause();
            refreshing = false;
            System.out.println("Arrêt du refresh des conversations");
            mHandler.removeCallbacks(mRunnable);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            super.onResume();
            refreshing = true;
            System.out.println("Reprise du refresh des conversations");
            mHandler.post(mRunnable);
        }
        super.onResume();
    }
}