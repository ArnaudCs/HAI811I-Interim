package com.example.interim.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.interim.R;

public class fragment_third_slide extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third_slide, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button nextSlideThirdBtn = view.findViewById(R.id.nextThirdSlideBtn);
        Button backSlideThirdBtn = view.findViewById(R.id.prevThirdSlideBtn);

        if (nextSlideThirdBtn != null) {
            nextSlideThirdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registration = new Intent(getActivity(), Registration.class);
                    startActivity(registration);
                }
            });
        }

        backSlideThirdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });
    }
}