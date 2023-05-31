package com.tim1.daimler.view.passenger.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.EstimationDTO;
import com.tim1.daimler.dtos.ride.VehicleDTO;
import com.tim1.daimler.util.FragmentTransition;

import java.text.DecimalFormat;
import java.util.Objects;

public class PassengerNewRouteSuggestionFragment extends Fragment {

    public static final String EVENT_FILL_DETAILS = "fill_details";
    public static final String EVENT_CREATE_RIDE = "create_ride";
    public static final String ARGS_ESTIMATE = "arg_estimate";

    public PassengerNewRouteSuggestionFragment() { }

    public static PassengerNewRouteSuggestionFragment newInstance() {
        return new PassengerNewRouteSuggestionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_new_route_suggestion, container, false);
        view.findViewById(R.id.passenger_accept_route).setOnClickListener((v) -> getParentFragmentManager().setFragmentResult(EVENT_CREATE_RIDE, new Bundle()));

        getParentFragmentManager().setFragmentResultListener(EVENT_FILL_DETAILS, requireActivity(), (requestKey, result) -> {
            EstimationDTO dto = (EstimationDTO) result.getSerializable(ARGS_ESTIMATE);
            ((TextView) view.findViewById(R.id.estimate_price)).setText(dto.getEstimatedCost() + "din");
            ((TextView) view.findViewById(R.id.estimate_route)).setText((dto.getEstimatedTimeInMinutes() + 1) + "min | " + (new DecimalFormat("0.00 ")).format(dto.getDistance()/1000) + "km");
        });
        return view;
    }
}