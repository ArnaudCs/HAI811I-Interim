package com.example.interim.discussion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.interim.R;
import com.example.interim.authentication.fragment_third_slide;
import com.example.interim.fragment_successfull_new_conversation;
import com.google.android.material.textfield.TextInputEditText;

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

                    fragment_successfull_new_conversation fragmentConversationCreation = new fragment_successfull_new_conversation();

                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.newConversationContainer, fragmentConversationCreation)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.missingFieldsErroToast), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}