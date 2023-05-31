package com.tim1.daimler.view.driver.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.view.common.activity.ChatActivity;

import java.text.DecimalFormat;

public class DriverNewRideInfoFragment extends Fragment {

    public static final String EVENT_DRIVE_TO_DEPARTURE = "event_drive_to_destination";
    public static final String EVENT_ACCEPT_RIDE = "event_accept_ride";
    public static final String EVENT_FINISH_RIDE = "event_finish_ride";
    public static final String EVENT_REJECT_RIDE = "event_reject_ride";
    public static final String EVENT_PANIC_RIDE = "event_panic_ride";
    public static final String EVENT_SET_RIDE_BUTTONS = "event_set_buttons";
    public static final String ARG_RIDE = "arg_ride";
    public static final String ARG_STATUS = "arg_status";
    private static final DecimalFormat df = new DecimalFormat("0.0");

    private CreatedRideDTO ride;

    public DriverNewRideInfoFragment() { }

    public static DriverNewRideInfoFragment newInstance() {
        return new DriverNewRideInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_new_ride_info, container, false);

        TextView routePrice = view.findViewById(R.id.driver_new_route_price);
        TextView routeDetails = view.findViewById(R.id.driver_new_route_details);
        TextView passengerName = view.findViewById(R.id.driver_new_route_name);

        view.findViewById(R.id.open_inbox_driver).setOnClickListener(v -> {
            Intent openChat = new Intent(getActivity(), ChatActivity.class);
            openChat.putExtra(ChatActivity.ARG_USER_TO, ride.getPassengers().get(0));
            openChat.putExtra(ChatActivity.ARG_USER_FROM, ride.getDriver());
            openChat.putExtra(ChatActivity.ARG_RIDE_ID, ride.getId());
            openChat.putExtra(ChatActivity.ARG_TYPE, "RIDE");
            openChat.putExtra(ChatActivity.ARG_ADDRESS_DESTINATION, ride.getLocations().get(0).getDestination().getAddress());
            startActivity(openChat);
        });

        MaterialButton startRideButton = (MaterialButton) view.findViewById(R.id.startRideButton);
        MaterialButton rejectRideButton = (MaterialButton) view.findViewById(R.id.rejectRideButton);

        getParentFragmentManager().setFragmentResultListener(EVENT_DRIVE_TO_DEPARTURE, requireActivity(), (requestKey, result) -> {
            ride = (CreatedRideDTO) result.getSerializable(ARG_RIDE);
            startRideButton.setText("START");
            rejectRideButton.setText("REJECT");
            routePrice.setText(String.valueOf(ride.getTotalCost()) + "din");
            if (ride.getDistance() < 1000)
                routeDetails.setText(String.valueOf(ride.getEstimatedTimeInMinutes()) + "min | " + ride.getDistance() + "m");
            else
                routeDetails.setText(String.valueOf(ride.getEstimatedTimeInMinutes()) + "min | " + df.format(ride.getDistance()/1000) +"km");
            passengerName.setText(ride.getPassengers().get(0).getName() + " " + ride.getPassengers().get(0).getSurname());
        });

        getParentFragmentManager().setFragmentResultListener(EVENT_SET_RIDE_BUTTONS, requireActivity(), (requestKey, result) -> {
            String status = result.getString(ARG_STATUS);
            if(status.equals("FINISHED") || status.equals("PANIC") || status.equals("ACCEPTED")){
                startRideButton.setText("START");
                rejectRideButton.setText("REJECT");
            } else {
                startRideButton.setText("FINISH");
                rejectRideButton.setText("PANIC");
            }
        });


        startRideButton.setOnClickListener(v -> {
            if (startRideButton.getText().equals("FINISH")) {
                getParentFragmentManager().setFragmentResult(DriverNewRideInfoFragment.EVENT_FINISH_RIDE, new Bundle());
                startRideButton.setText("START");
                rejectRideButton.setText("REJECT");
            }
            else {
                getParentFragmentManager().setFragmentResult(DriverNewRideInfoFragment.EVENT_ACCEPT_RIDE, new Bundle());
                startRideButton.setText("FINISH");
                rejectRideButton.setText("PANIC");
            }
        });

        rejectRideButton.setOnClickListener(v -> {
            if (rejectRideButton.getText().equals("REJECT")) {
                getParentFragmentManager().setFragmentResult(DriverNewRideInfoFragment.EVENT_REJECT_RIDE, new Bundle());
            }
            else {
                getParentFragmentManager().setFragmentResult(DriverNewRideInfoFragment.EVENT_PANIC_RIDE, new Bundle());
                startRideButton.setText("START");
                rejectRideButton.setText("REJECT");
            }

        });

        return view;
    }
}