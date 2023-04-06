package com.example.interim;

import static android.content.ContentValues.TAG;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
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
    // tie : TextInputEdit
    private List<Integer> generatedTieIds;
    // til : TextInputLayout
    private List<Integer> generatedTilIds;
    private TextInputEditText memberInput;
    private List<String> memberList;
    private ConstraintLayout groupCreationCl;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    CollectionReference groupsRef;
    CollectionReference usersRef;
    CollectionReference prosRef;
    List<Map<String,Object>> users;
    List<Map<String,Object>> pros;
    public fragment_group_creation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        generatedTieIds = new ArrayList<>();
        generatedTilIds = new ArrayList<>();
        memberList = new ArrayList<>();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_creation, container, false);
        createGroupButton = v.findViewById(R.id.createGroupButton);
        newMemberButton = v.findViewById(R.id.addMemberButton);
        return v;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scale = view.getContext().getResources().getDisplayMetrics().density;
        // on récupère l'id du 1er layout pour pouvoir définir les contraintes des InputLayout plus tard
        generatedTieIds.add(R.id.textMember1);
        generatedTilIds.add(R.id.layoutUser1);

        newMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // on augmente le margin du bouton + et du bouton créer groupe
                ConstraintLayout.LayoutParams paramsButton = (ConstraintLayout.LayoutParams) newMemberButton.getLayoutParams();
                paramsButton.topMargin += (int) (50 * scale + 0.5f);
                newMemberButton.setLayoutParams(paramsButton);

                paramsButton = (ConstraintLayout.LayoutParams) createGroupButton.getLayoutParams();
                paramsButton.topMargin += (int) (50 * scale + 0.5f);
                createGroupButton.setLayoutParams(paramsButton);
                int lastUserTilId = generatedTilIds.get(userNumber);
                int lastUserTieId = generatedTieIds.get(userNumber);
                userNumber++;
                groupCreationCl = getView().findViewById(R.id.groupCreationCl);
                TextInputLayout tilNewMember = new TextInputLayout(view.getContext());
                TextInputEditText tieNewMember = new TextInputEditText(view.getContext());
                int newTieId = View.generateViewId();
                generatedTieIds.add(newTieId);
                // paramètres du textInput
                tieNewMember.setId(newTieId);
                tieNewMember.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                tieNewMember.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                tilNewMember.addView(tieNewMember);
                int newTilId = View.generateViewId();
                generatedTilIds.add(newTilId);

                // paramètres du nouveau layout
                tilNewMember.setId(newTilId);
                tilNewMember.setHint("Mail User "+String.valueOf(userNumber+1));
                tilNewMember.setBoxBackgroundColor(getResources().getColor(R.color.white));
                tilNewMember.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                tilNewMember.setBoxStrokeColor(getResources().getColor(R.color.primary_red));
                tilNewMember.setBoxStrokeErrorColor(ColorStateList.valueOf(getResources().getColor(R.color.strokeError)));
                tilNewMember.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                tilNewMember.setEndIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_red)));
                tilNewMember.setPlaceholderText("abc@df.com");
                tilNewMember.setStartIconDrawable(R.drawable.baseline_person_24);
                tilNewMember.setStartIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_red)));

                tilNewMember.setLayoutParams(new ConstraintLayout.LayoutParams(
                        0,
                        (int) (60 * scale + 0.5f))); // 60 dp convertis en pixels

                groupCreationCl.addView(tilNewMember);
                // contraintes

                ConstraintSet cs = new ConstraintSet();
                cs.clone(groupCreationCl);
                cs.connect(newTilId,ConstraintSet.TOP, lastUserTilId, ConstraintSet.TOP, (int) (50 * scale + 0.5f));
                cs.connect(newTilId, ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,(int) (70 * scale + 0.5f));
                cs.connect(newTilId, ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,(int) (90 * scale + 0.5f));
                cs.applyTo(groupCreationCl);

            }
        });

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = FirebaseFirestore.getInstance();

                groupsRef = db.collection("Groups");
                usersRef = db.collection("Users");
                prosRef = db.collection("Pros");
                users = new ArrayList<>();
                pros = new ArrayList<>();

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    Log.d(TAG, "User email: " + currentUser.getEmail());
                    Map<String, Object> data = new HashMap<>();
                    data.put("creator",currentUser.getEmail());
//                    data.put("creator","luis@rgir.eokf");
                    Date currentTime = Calendar.getInstance().getTime();
                    data.put("creationDate",currentTime);

                    // Récupération des données entrées dans les TextInputEdit, vérification des emails
                    loadCollections(new onCollectionLoadedListener() {
                        @Override
                        public void onCollectionsLoaded(List<Map<String, Object>> usersList, List<Map<String, Object>> prosList) {
                            for (int id : generatedTieIds) {
                                memberInput = getView().findViewById(id);
                                String mailInput = String.valueOf(memberInput.getText());
                                String dbMail;
                                boolean verif = true;
                                for (Map<String, Object> pro : prosList) {
                                    dbMail = (String) pro.getOrDefault("email", "");
                                    if (dbMail.equals(mailInput)) {
                                        verif = false;
                                        break;
                                    }
                                }
                                if (verif) {
                                    for (Map<String, Object> user : usersList) {
                                        dbMail = (String) user.getOrDefault("email", "");
                                        if (!dbMail.equals("") && dbMail.equals(mailInput)) {
                                            memberList.add(mailInput);
                                            break;
                                        }
                                    }
                                }
                            }
                            data.put("members", memberList);
                            groupsRef.add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "Document added");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), "User isn't authentified", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void loadCollections(final onCollectionLoadedListener listener){

        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        users.add(document.getData());
                    }
                    prosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    pros.add(document.getData());
                                }
                                listener.onCollectionsLoaded(users,pros);
                            } else {
                                Log.w(TAG, "Error getting pros documents.", task.getException());
                            }
                        }
                    });
                } else {
                    Log.w(TAG, "Error getting users documents.", task.getException());
                }
            }
        });

    }
}
