package com.tim1.daimler.view.passenger.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.dtos.ride.VehicleDTO;
import com.tim1.daimler.util.Bitmaper;
import com.tim1.daimler.view.common.activity.ChatActivity;
import com.tim1.daimler.view.driver.fragment.DriverNewRideInfoFragment;
import com.tim1.daimler.view.passenger.activity.PassengerMainActivity;

public class PassengerNewRouteDriverInfoFragment extends Fragment {

    public static final String EVENT_DRIVER_COMING = "event_driver_coming";
    public static final String EVENT_DRIVER_ARRIVED = "event_driver_arrived";
    public static final String EVENT_PANIC_RIDE = "event_panic_ride";
    public static final String ARG_RIDE = "arg_ride";
    public static final String ARG_VEHICLE = "arg_vehicle";
    private CreatedRideDTO ride;

    public PassengerNewRouteDriverInfoFragment() { }

    public static PassengerNewRouteDriverInfoFragment newInstance() {
        return new PassengerNewRouteDriverInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_new_route_driver_info, container, false);

        TextView driverName = view.findViewById(R.id.driver_coming_name);
        TextView carModel = view.findViewById(R.id.driver_coming_car_model);
        TextView carLicence = view.findViewById(R.id.driver_coming_licence);
        TextView onTheWay = view.findViewById(R.id.on_the_way);
        ImageView profile = view.findViewById(R.id.driver_coming_profile_picture);
        Button multipraktik = view.findViewById(R.id.open_inbox_passenger);

        view.findViewById(R.id.open_inbox_passenger).setOnClickListener(v -> {
            if(multipraktik.getText().equals("Inbox")) {
                Intent openChat = new Intent(getActivity(), ChatActivity.class);
                openChat.putExtra(ChatActivity.ARG_USER_FROM, ride.getPassengers().get(0));
                openChat.putExtra(ChatActivity.ARG_USER_TO, ride.getDriver());
                openChat.putExtra(ChatActivity.ARG_RIDE_ID, ride.getId());
                openChat.putExtra(ChatActivity.ARG_TYPE, "RIDE");
                openChat.putExtra(ChatActivity.ARG_ADDRESS_DESTINATION, ride.getLocations().get(0).getDestination().getAddress());
                startActivity(openChat);
            } else {
                getParentFragmentManager().setFragmentResult(PassengerNewRouteDriverInfoFragment.EVENT_PANIC_RIDE, new Bundle());
            }
        });

        getParentFragmentManager().setFragmentResultListener(EVENT_DRIVER_COMING, requireActivity(), (requestKey, result) -> {
            ride = (CreatedRideDTO) result.getSerializable(ARG_RIDE);
            VehicleDTO vehicle = (VehicleDTO) result.getSerializable(ARG_VEHICLE);
            driverName.setText(ride.getDriver().getName() + " " + ride.getDriver().getSurname());
            profile.setImageBitmap(Bitmaper.toBitmap(ride.getDriver().getProfilePicture()));
            carModel.setText(vehicle.getModel());
            carLicence.setText(vehicle.getLicenseNumber());
            multipraktik.setText("Inbox");
        });

        getParentFragmentManager().setFragmentResultListener(EVENT_DRIVER_ARRIVED, requireActivity(), (requestKey, result) -> {
            ride = (CreatedRideDTO) result.getSerializable(ARG_RIDE);
            driverName.setText(ride.getDriver().getName() + " " + ride.getDriver().getSurname());
            profile.setImageBitmap(Bitmaper.toBitmap(ride.getDriver().getProfilePicture()));
            onTheWay.setText("Ride started!");
            multipraktik.setText("Panic");

//            multipraktik.setTextColor(getActivity().getColor(R.color.));
        });


        return view;
    }
}