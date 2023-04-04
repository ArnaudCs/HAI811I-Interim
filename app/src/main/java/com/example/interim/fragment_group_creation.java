package com.example.interim;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class fragment_group_creation extends Fragment {

    final float scale = getContext().getResources().getDisplayMetrics().density;
    private static int userNumber = 0;
    private Button newMemberButton;
    private Button createGroupButton;
    private List<Integer> generatedTextInputsIds;
    private TextInputEditText memberInput;
    private List<String> memberList;
    private ConstraintLayout groupCreationCl;
    public fragment_group_creation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        generatedTextInputsIds = new ArrayList<>();
        memberList = new ArrayList<>();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_creation, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // on récupère l'id du 1er membre pour pouvoir définir les contraintes des InputLayout plus tard
        generatedTextInputsIds.add(R.id.textMember1);
        newMemberButton = view.findViewById(R.id.addMemberButton);
        newMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastUserId = generatedTextInputsIds.get(userNumber);
                userNumber++;

                groupCreationCl = view.findViewById(R.id.groupCreationCl);
                TextInputLayout tilNewMember = new TextInputLayout(view.getContext());
                TextInputEditText tieNewMember = new TextInputEditText(view.getContext());
                // paramètres du textInput
                int newId = View.generateViewId();
                generatedTextInputsIds.add(newId);
                tieNewMember.setId(newId);
                tieNewMember.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                tieNewMember.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                tilNewMember.addView(tieNewMember);
                // paramètres du nouveau layout
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        0,
                        (int) (60 * scale + 0.5f)); // 60 dp convertis en pixels
                params.setMargins(
                        (int) (70 * scale + 0.5f),
                        (int) (60 * scale + 0.5f),
                        (int) (90 * scale + 0.5f),
                        0
                );

                tilNewMember.setBoxBackgroundColor(getResources().getColor(R.color.white));
                tilNewMember.setHint("Mail User "+String.valueOf(userNumber+1));
                tilNewMember.setLayoutParams(params);



                // contraintes
                ConstraintSet cs = new ConstraintSet();
                cs.clone(groupCreationCl);
                cs.connect(newId,ConstraintSet.TOP,lastUserId,ConstraintSet.BOTTOM);



            }
        });

        createGroupButton = view.findViewById(R.id.createGroupButton);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberInput = view.findViewById(R.id.textMember1);
                memberList.add(String.valueOf(memberInput.getText()));
                Log.i("Info",String.valueOf(memberInput.getText()));
                for (int id : generatedTextInputsIds){
                    memberInput = view.findViewById(id);
                    memberList.add(String.valueOf(memberInput.getText()));
                    Log.i("Info",String.valueOf(memberInput.getText()));
                }
            }
        });

    }
}
