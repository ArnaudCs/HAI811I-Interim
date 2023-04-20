package com.example.interim.authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.interim.R;

public class fragment_second_slide extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_slide, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button nextSlideSecondBtn = view.findViewById(R.id.nextSecondSlideBtn);
        Button backSlideSecondBtn = view.findViewById(R.id.prevSecondSlideBtn);

        if (nextSlideSecondBtn != null) {
            nextSlideSecondBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment_third_slide fragmentThirdSlide = new fragment_third_slide();

                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.slideContainer, fragmentThirdSlide)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        backSlideSecondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });
    }
}