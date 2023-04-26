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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_new_message_conversation extends Fragment {

    Button backBtnNewConv;
    Button sendFirstMessageBtn;

    TextInputEditText textContactMailMessage;

    EditText firstMessageText;

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

        backBtnNewConv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        sendFirstMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(firstMessageText.getText()) && !TextUtils.isEmpty(textContactMailMessage.getText())){
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

                        Map<String, Object> conversationData = new HashMap<>();
                        conversationData.put("participants", Arrays.asList(userRef));
                        conversationData.put("lastMessage", firstMessageText.getText());
                        db.collection("Conversations").add(conversationData)
                                .addOnSuccessListener(documentReference -> {
                                    // the document was successfully created
                                    Log.d(TAG, "New conversation document created with ID: " + documentReference.getId());
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
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error querying for user", e);
                    });
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.missingFieldsErroToast), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}