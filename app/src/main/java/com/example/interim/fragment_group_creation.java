package com.example.interim;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
//                cs.connect(newTilId,ConstraintSet.TOP,ConstraintSet.PARENT_ID, (int) (20 * scale + 0.5f));
//
//                cs.connect(newTilId, ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,(int) (70 * scale + 0.5f));
//                cs.connect(newTilId, ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,(int) (90 * scale + 0.5f));
//
//                cs.applyTo(groupCreationCl);

            }
        });

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberInput = view.findViewById(R.id.textMember1);
                memberList.add(String.valueOf(memberInput.getText()));
                Log.i("Info",String.valueOf(memberInput.getText()));
                // Récupération des données entrées dans les TextInputEdit
                for (int id : generatedTieIds){
                    memberInput = view.findViewById(id);
                    memberList.add(String.valueOf(memberInput.getText()));
                    Log.i("Info",String.valueOf(memberInput.getText()));
                }
            }
        });

    }
}
