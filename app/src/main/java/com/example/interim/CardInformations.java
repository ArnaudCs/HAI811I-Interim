package com.example.interim;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class CardInformations extends Fragment {

    public CardInformations() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_informations, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button choosePlan = view.findViewById(R.id.validateBasket);
        Button backButtonCardInfos = view.findViewById(R.id.backOrderBtn);
        TextInputEditText cardName = view.findViewById(R.id.textCardName);
        TextInputEditText cardNumber = view.findViewById(R.id.textCountry);
        TextInputEditText cardCVC = view.findViewById(R.id.textPostalCode);
        TextInputEditText cardExpiration = view.findViewById(R.id.textState);

        CheckBox rememberCheck = view.findViewById(R.id.rememberCardCheck);

        if (choosePlan != null) {
            choosePlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(cardName.getText()) && !TextUtils.isEmpty(cardNumber.getText()) && !TextUtils.isEmpty(cardCVC.getText())
                            && !TextUtils.isEmpty(cardExpiration.getText())) {
                        fragment_payment_adress fragmentPaymentAdress = new fragment_payment_adress();

                        // Remplacer le fragment actuel par le nouveau fragment
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.paymentContainer, fragmentPaymentAdress)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), R.string.missingFieldsErroToast, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        cardExpiration.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                if(str.length() == 2 && !str.contains("/")){
                    str += "/";
                    cardExpiration.setText(str);
                    cardExpiration.setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cardNumber.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private boolean deletingHyphen;
            private int hyphenStart;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (isFormatting) {
                    return;
                }

                // Check if the user is deleting a hyphen
                deletingHyphen = count == 1 && after == 0 && s.charAt(start) == '-';
                if (deletingHyphen) {hyphenStart = start;}
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) {
                    return;
                }

                // Check if the user is adding a hyphen
                boolean addingHyphen = count == 1 && before == 0 && (start == 3 || start == 8 || start == 13);
                if (addingHyphen) {
                    s = new StringBuilder(s).insert(start, "-").toString();
                    cardNumber.setText(s);
                    cardNumber.setSelection(start + 1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }

                isFormatting = true;

                // Remove hyphens that were deleted by the user
                if (deletingHyphen && hyphenStart > 0) {
                    s.delete(hyphenStart - 1, hyphenStart);
                }

                // Remove all hyphens from the string and re-insert them in the correct positions
                String unformattedText = s.toString().replaceAll("-", "");
                StringBuilder formattedText = new StringBuilder();
                for (int i = 0; i < unformattedText.length(); i++) {
                    formattedText.append(unformattedText.charAt(i));
                    if ((i + 1) % 4 == 0 && i != unformattedText.length() - 1) {
                        formattedText.append("-");
                    }
                }

                cardNumber.setText(formattedText.toString());
                cardNumber.setSelection(cardNumber.getText().length());

                isFormatting = false;
            }
        });
    }

}