package com.tim1.daimler.view.common.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.google.android.material.button.MaterialButton;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.view.common.activity.ChatActivity;
import com.tim1.daimler.view.driver.fragment.DriverNewRideInfoFragment;

import java.text.DecimalFormat;

public class FinishedRideFragment extends Fragment {

    public static final String EVENT_SET_FINISHED_TEXT = "set_finished";
    public static final String EVENT_SET_REJECT_TEXT = "set_rejected";
    public static final String EVENT_SET_PANIC_TEXT = "set_panic";

    public FinishedRideFragment() { }

    public static FinishedRideFragment newInstance() {
        return new FinishedRideFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_finished, container, false);
        TextView text = view.findViewById(R.id.ride_finished_card_text);

        getParentFragmentManager().setFragmentResultListener(EVENT_SET_FINISHED_TEXT, requireActivity(), (requestKey, result) -> text.setText("Ride Finished!"));
        getParentFragmentManager().setFragmentResultListener(EVENT_SET_REJECT_TEXT, requireActivity(), (requestKey, result) -> text.setText("Ride Rejected!"));
        getParentFragmentManager().setFragmentResultListener(EVENT_SET_PANIC_TEXT, requireActivity(), (requestKey, result) -> text.setText("You have panicked!!!"));

        return view;
    }
}
