package com.example.interim.discussion;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.interim.R;
import com.example.interim.authentication.fragment_third_slide;
import com.example.interim.fragment_successfull_new_conversation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_new_message_conversation extends Fragment {

    Button backBtnNewConv;
    Button sendFirstMessageBtn;

    TextInputEditText textContactMailMessage;

    EditText firstMessageText;

    String mail, message;

    public fragment_new_message_conversation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_message_conversation, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtnNewConv = view.findViewById(R.id.backBtnNewConv);
        sendFirstMessageBtn = view.findViewById(R.id.sendFirstMessageBtn);
        firstMessageText = view.findViewById(R.id.firstMessageText);
        textContactMailMessage = view.findViewById(R.id.textContactMailMessage);

        Bundle args = getArguments();
        if (args != null) {
            mail = args.getString("mail");
            message = args.getString("message");
            if (message != null) {
                firstMessageText.setText(message);
            }
            if(mail != null) {
                textContactMailMessage.setText(mail);
            }
        }
        backBtnNewConv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        sendFirstMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(firstMessageText.getText()) && !TextUtils.isEmpty(textContactMailMessage.getText())) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userId = mAuth.getCurrentUser().getUid();

                    Task<DocumentSnapshot> userFromUsersTask = db.collection("Users").document(userId).get();
                    Task<DocumentSnapshot> userFromProsTask = db.collection("Pros").document(userId).get();

                    // Combine the two tasks into a single task
                    Task<List<DocumentSnapshot>> combinedTask = Tasks.whenAllSuccess(userFromUsersTask, userFromProsTask);

                    combinedTask.addOnSuccessListener(documentSnapshots -> {
                        DocumentSnapshot userFromUsers = documentSnapshots.get(0);
                        DocumentSnapshot userFromPros = documentSnapshots.get(1);

                        DocumentReference userRef = null;

                        if (userFromUsers.exists()) {
                            userRef = db.collection("Users").document(userId);
                        } else if (userFromPros.exists()) {
                            userRef = db.collection("Pros").document(userId);
                        } else {
                            // User not found in either collection
                            Log.e(TAG, "User not found in Users or Pros collection");
                            return;
                        }

                        // Query to find user with given email
                        String contactEmail = textContactMailMessage.getText().toString();
                        Task<QuerySnapshot> usersQuery = db.collection("Users").whereEqualTo("email", contactEmail).get();
                        Task<QuerySnapshot> prosQuery = db.collection("Pros").whereEqualTo("email", contactEmail).get();

                        DocumentReference finalUserRef = userRef;
                        Tasks.whenAllSuccess(usersQuery, prosQuery).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot usersSnapshot = (QuerySnapshot) task.getResult().get(0);
                                QuerySnapshot prosSnapshot = (QuerySnapshot) task.getResult().get(1);

                                if (!usersSnapshot.isEmpty()) {
                                    // Get document ID of user with given email from Users collection
                                    String contactUserId = usersSnapshot.getDocuments().get(0).getId();
                                    DocumentReference contactRef = db.collection("Users").document(contactUserId);

                                    // Check if conversation already exists between the participants
                                    checkConversationExists(db, finalUserRef, contactRef);
                                } else if (!prosSnapshot.isEmpty()) {
                                    // Get document ID of user with given email from Pros collection
                                    String contactUserId = prosSnapshot.getDocuments().get(0).getId();
                                    DocumentReference contactRef = db.collection("Pros").document(contactUserId);

                                    // Check if conversation already exists between the participants
                                    checkConversationExists(db, finalUserRef, contactRef);
                                } else {
                                    // User with given email not found in both collections
                                    Log.e(TAG, "User with email " + contactEmail + " not found in Users or Pros collection");
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.noUserWithEmailErr), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Query failed
                                Log.e(TAG, "Error querying for user with email " + contactEmail, task.getException());
                            }
                        });

                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error querying for user", e);
                    });
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.missingFieldsErroToast), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkConversationExists(FirebaseFirestore db, DocumentReference userRef, DocumentReference contactRef) {
        CollectionReference conversationsRef = db.collection("Conversations");
        // Query for conversations involving the current user
        Query userConversationQuery = conversationsRef.whereArrayContains("participants", userRef);

        // Query for conversations involving the contact
        Query contactConversationQuery = conversationsRef.whereArrayContains("participants", contactRef);

        Task<QuerySnapshot> userConversationsTask = userConversationQuery.get();
        Task<QuerySnapshot> contactConversationsTask = contactConversationQuery.get();

        Task<List<QuerySnapshot>> combinedTask = Tasks.whenAllSuccess(userConversationsTask, contactConversationsTask);

        combinedTask.addOnSuccessListener(querySnapshots -> {
            QuerySnapshot userConversationsSnapshot = querySnapshots.get(0);
            QuerySnapshot contactConversationsSnapshot = querySnapshots.get(1);

            List<DocumentSnapshot> userConversations = userConversationsSnapshot.getDocuments();
            List<DocumentSnapshot> contactConversations = contactConversationsSnapshot.getDocuments();

            // Check if there is an intersection of conversations between the two participants
            boolean conversationExists = false;
            for (DocumentSnapshot userConversation : userConversations) {
                for (DocumentSnapshot contactConversation : contactConversations) {
                    if (userConversation.getId().equals(contactConversation.getId())) {
                        conversationExists = true;
                        break;
                    }
                }
                if (conversationExists) {
                    break;
                }
            }

            if (conversationExists) {
                // Conversation already exists between the participants
                Log.e(TAG, "Conversation already exists between the participants");
                Toast.makeText(getContext(), "Conversation already exists", Toast.LENGTH_SHORT).show();
            } else {
                // Create a new conversation
                createNewConversation(db, userRef, contactRef);
            }
        }).addOnFailureListener(e -> {
            // Query failed
            Log.e(TAG, "Error querying for conversations", e);
        });
    }


    private void createNewConversation(FirebaseFirestore db, DocumentReference userRef, DocumentReference contactRef) {
        // Create new conversation document
        Map<String, Object> conversationData = new HashMap<>();
        conversationData.put("participants", Arrays.asList(userRef, contactRef));
        conversationData.put("lastMessage", firstMessageText.getText().toString());
        db.collection("Conversations").add(conversationData)
                .addOnSuccessListener(documentReference -> {
                    // the document was successfully created
                    Log.d(TAG, "New conversation document created with ID: " + documentReference.getId());
                    // Create new message document
                    Map<String, Object> messageData = new HashMap<>();
                    messageData.put("date", new Date());
                    messageData.put("sender", userRef.getId());
                    messageData.put("text", firstMessageText.getText().toString());
                    db.collection("Messages").add(messageData)
                            .addOnSuccessListener(messageDocumentReference -> {
                                // the document was successfully created
                                Log.d(TAG, "New message document created with ID: " + messageDocumentReference.getId());
                                // Add reference to message document to Conversation document
                                documentReference.update("messages", FieldValue.arrayUnion(messageDocumentReference));
                                // Add reference to contactRef to unRead array in Conversation document
                                documentReference.update("unRead", FieldValue.arrayUnion(contactRef));
                            })
                            .addOnFailureListener(e -> {
                                // an error occurred while creating the document
                                Log.w(TAG, "Error creating new message document", e);
                            });
                })
                .addOnFailureListener(e -> {
                    // an error occurred while creating the document
                    Log.w(TAG, "Error creating new conversation document", e);
                });

        fragment_successfull_new_conversation fragmentConversationCreation = new fragment_successfull_new_conversation();

        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.newConversationContainer, fragmentConversationCreation)
                .addToBackStack(null)
                .commit();
    }

}