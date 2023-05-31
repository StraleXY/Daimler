package com.tim1.daimler.view.passenger.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.tim1.daimler.R;
import com.tim1.daimler.util.FragmentTransition;

public class PassengerNewRouteRidePropertiesFragment extends Fragment {

    public static final String EVENT_PROPERTIES_PICKED = "properties_picked";
    public static final String ARGS_CAR_TYPE = "arg_car_type";
    public static final String ARGS_BABY = "arg_baby";
    public static final String ARGS_PET = "arg_pet";

    public static PassengerNewRouteRidePropertiesFragment newInstance() {
        return new PassengerNewRouteRidePropertiesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    CheckBox isBaby, isPet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_new_route_ride_properties, container, false);

        ((MaterialButtonToggleGroup) view.findViewById(R.id.passenger_car_type_selector)).check(R.id.passenger_car_type_standard);
        isBaby = view.findViewById(R.id.passenger_baby_transport);
        isPet = view.findViewById(R.id.passenger_pet_transport);

        (view.findViewById(R.id.passenger_route_details_next)).setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(ARGS_CAR_TYPE, ((Button) view.findViewById(((MaterialButtonToggleGroup)view.findViewById(R.id.passenger_car_type_selector)).getCheckedButtonId())).getText().toString().toLowerCase());
            result.putBoolean(ARGS_BABY, isBaby.isChecked());
            result.putBoolean(ARGS_PET, isPet.isChecked());
            getParentFragmentManager().setFragmentResult(EVENT_PROPERTIES_PICKED, result);
        });

        return view;
    }
}