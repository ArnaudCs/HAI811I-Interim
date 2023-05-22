package com.example.interim.discussion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.interim.AppActivity;
import com.example.interim.R;

public class fragment_successfull_new_conversation extends Fragment {

    public fragment_successfull_new_conversation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_successfull_new_conversation, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button goToMessagesBtn = view.findViewById(R.id.goToMessagesBtn);

        goToMessagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(getActivity(), AppActivity.class);
                startActivity(mainActivity);
                getActivity().finish();
            }
        });
    }
}