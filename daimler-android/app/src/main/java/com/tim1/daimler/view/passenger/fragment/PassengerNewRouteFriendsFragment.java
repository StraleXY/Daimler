package com.tim1.daimler.view.passenger.fragment;

import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.tim1.daimler.R;
import com.tim1.daimler.util.FragmentTransition;

public class PassengerNewRouteFriendsFragment extends Fragment {

    public PassengerNewRouteFriendsFragment() {
        // Required empty public constructor
    }

    public static PassengerNewRouteFriendsFragment newInstance() {
        return new PassengerNewRouteFriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_new_route_friends, container, false);

        (view.findViewById(R.id.passenger_friends_next)).setOnClickListener(v -> getParentFragmentManager().setFragmentResult(FragmentTransition.REQUEST_NEXT_STEP, new Bundle()));

        TextInputLayout inputLayout = view.findViewById(R.id.i_input_v);
        EditText editText = inputLayout.getEditText();
        ChipGroup chipGroup = view.findViewById(R.id.i_flex_box);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                if (editText.getText().toString().equals(" ")) {
                    editText.setText("");
                }
            } else {
                if (editText.getText().equals("") && chipGroup.getChildCount() > 0){
                    editText.setText(" ");
                }
            }
        });

        editText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (chipGroup.getChildCount() <= 0 || !editText.getText().toString().isEmpty()) return false;
                Chip lastChip = (Chip) chipGroup.getChildAt(chipGroup.getChildCount() - 1);
                editText.append(lastChip.getText());
                chipGroup.removeView(lastChip);
            }
            return false;
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if(text.endsWith(" ")){
                    addNewChip(text.substring(0, text.length() - 1), chipGroup);
                    s.clear();
                }
            }
        });

        return view;
    }

    private void addNewChip(String text, ChipGroup group) {
        Chip chip = (Chip) LayoutInflater.from(getActivity()).inflate(R.layout.widget_input_chip, group, false);
        chip.setId(ViewCompat.generateViewId());
        chip.setText(text);
        chip.setOnCloseIconClickListener(v -> group.removeView(chip));
        group.addView(chip);
    }
}