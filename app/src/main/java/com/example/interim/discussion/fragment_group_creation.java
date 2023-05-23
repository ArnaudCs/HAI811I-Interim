package com.example.interim.discussion;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.Utils.onCollectionLoadedListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_group_creation extends Fragment {
    private float scale;
    private static int userNumber = 0;
    private Button newMemberButton;
    private Button createGroupButton;
    ArrayList<String> members;
    private member_ViewAdapter memberAdapter;

    RecyclerView userRecycler;

    TextInputEditText textMember1;
    private ConstraintLayout groupCreationCl;
    FirebaseFirestore db;

    Button backBtnGroupCreation;
    public fragment_group_creation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_creation, container, false);
        createGroupButton = v.findViewById(R.id.createGroupButton);
        newMemberButton = v.findViewById(R.id.addMemberButton);
        return v;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scale = view.getContext().getResources().getDisplayMetrics().density;
        backBtnGroupCreation = view.findViewById(R.id.backBtnGroupCreation);
        userRecycler = view.findViewById(R.id.userRecycler);
        textMember1 = view.findViewById(R.id.textMember1);

        memberAdapter = new member_ViewAdapter(requireContext());
        memberAdapter.updateMembersList(null);

        // Définissez l'adaptateur sur votre RecyclerView
        userRecycler.setAdapter(memberAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        userRecycler.setLayoutManager(layoutManager);
        members = new ArrayList<>();

        backBtnGroupCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        newMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(textMember1.getText())) {
                    String newMember = textMember1.getText().toString();
                    // Regex pattern to validate email address
                    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

                    if (newMember.matches(emailRegex)) {
                        if (memberAdapter != null) {
                            memberAdapter.updateMembersList(newMember);
                        }
                        textMember1.setText("");
                    } else {
                        Toast.makeText(getContext(), R.string.invalidEmailError, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.addMemberError, Toast.LENGTH_SHORT).show();
                }
            }
        });

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> members = memberAdapter.getMembersList();
                if (members.size() == 0) {
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersCollection = db.collection("Users");
                CollectionReference prosCollection = db.collection("Pros");

                // Query Users collection
                Query usersQuery = usersCollection.whereIn("email", members);
                Task<QuerySnapshot> usersTask = usersQuery.get();

                // Query Pros collection
                Query prosQuery = prosCollection.whereIn("email", members);
                Task<QuerySnapshot> prosTask = prosQuery.get();

                // Combine both tasks
                Task<List<QuerySnapshot>> combinedTask = Tasks.whenAllSuccess(usersTask, prosTask);

                combinedTask.addOnSuccessListener(querySnapshots -> {
                    QuerySnapshot usersSnapshot = querySnapshots.get(0);
                    QuerySnapshot prosSnapshot = querySnapshots.get(1);
                    ArrayList<DocumentReference> memberReferences = new ArrayList<>();

                    // Process Users collection results
                    for (QueryDocumentSnapshot userDocument : usersSnapshot) {
                        memberReferences.add(userDocument.getReference());
                    }

                    // Process Pros collection results
                    for (QueryDocumentSnapshot proDocument : prosSnapshot) {
                        memberReferences.add(proDocument.getReference());
                    }

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    memberReferences.add(db.collection("Users").document(mAuth.getCurrentUser().getUid()));

                    Map<String, Object> groupData = new HashMap<>();
                    groupData.put("members", memberReferences);
                    groupData.put("creationDate", new Date());
                    groupData.put("messages", new ArrayList<>());

                    Map<String, Object> conversationData = new HashMap<>();
                    conversationData.put("participants", memberReferences);
                    conversationData.put("lastMessage", "");

                    db.collection("Conversations").add(conversationData).addOnSuccessListener(documentReference -> {
                        // Conversation document was successfully created
                        String conversationId = documentReference.getId();
                        documentReference.update("unRead", new ArrayList<>());
                        documentReference.update("messages", new ArrayList<>());
                        //documentReference.update("groupName", groupName.getText().toString());

                        groupData.put("conversationId", conversationId);

                        db.collection("Groups").add(groupData).addOnSuccessListener(groupDocumentReference -> {
                            // Group document was successfully created
                            String groupId = groupDocumentReference.getId();
                            // Perform further operations with the group ID and conversation ID
                            // ...
                            Log.d(TAG, "New group created with ID: " + groupId + ", Conversation ID: " + conversationId);
                        }).addOnFailureListener(e -> {
                            // Failed to create the group document
                            Log.e(TAG, "Error creating group document: " + e.getMessage());
                        });
                    }).addOnFailureListener(e -> {
                        // Failed to create the conversation document
                        Log.e(TAG, "Error creating conversation document: " + e.getMessage());
                    });
                }).addOnFailureListener(e -> {
                    // Failed to retrieve Users or Pros collection
                    Log.e("TAG", "Error retrieving collections: " + e.getMessage());
                });
            }

        });
    }
    void loadCollections(final onCollectionLoadedListener listener){

    }
}
