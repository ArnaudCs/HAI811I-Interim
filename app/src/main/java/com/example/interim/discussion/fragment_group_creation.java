package com.example.interim.discussion;

import static android.content.ContentValues.TAG;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interim.R;
import com.example.interim.onCollectionLoadedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
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
    FirebaseAuth mAuth;

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

            }
        });
    }
    void loadCollections(final onCollectionLoadedListener listener){

    }
}
