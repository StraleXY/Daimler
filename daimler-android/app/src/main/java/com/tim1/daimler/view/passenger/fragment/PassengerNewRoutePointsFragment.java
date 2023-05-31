package com.tim1.daimler.view.passenger.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.tim1.daimler.R;
import com.tim1.daimler.util.FragmentTransition;

import java.time.LocalDateTime;
import java.util.Calendar;

public class PassengerNewRoutePointsFragment extends Fragment {

    public static final String EVENT_FULL_ADDRESS_INPUT = "event_full_address_input";
    public static final String EVENT_SCHEDULED_TIME_PICKED = "event_scheduled_time_picked";
    public static final String EVENT_POINTS_PICKED = "event_points_picked";
    public static final String ARG_DESTINATION = "arg_destination";
    public static final String ARG_DEPARTURE = "arg_departure";
    public static final String ARG_TIMESTAMP = "arg_timestamp";

    public PassengerNewRoutePointsFragment() { }

    public static PassengerNewRoutePointsFragment newInstance() {
        return new PassengerNewRoutePointsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_new_route_points, container, false);

        TextView departure = view.findViewById(R.id.departure_address_field);
        TextView destination = view.findViewById(R.id.destination_address_field);

        ((MaterialButtonToggleGroup) view.findViewById(R.id.passenger_ride_time_selector)).check(R.id.passenger_ride_time_now);
        ((MaterialButtonToggleGroup) view.findViewById(R.id.passenger_ride_time_selector)).addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            CharSequence text = ((MaterialButton) view.findViewById(checkedId)).getText();
            if (getString(R.string.button_now).equals(text) && isChecked) {
                toggleScheduler(view, View.GONE);
            } else if (getString(R.string.button_schedule).equals(text) && isChecked) {
                toggleScheduler(view, View.VISIBLE);
            }
        });

        view.findViewById(R.id.passenger_points_pick_time).setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(LocalDateTime.now().getHour())
                    .setMinute(LocalDateTime.now().getMinute())
                    .setTitleText("Pick desired time")
                    .build();
            picker.addOnPositiveButtonClickListener(v1 -> {
                ((TextView) view.findViewById(R.id.passenger_points_scheduled_time)).setText(picker.getHour() + ":" + picker.getMinute());
                Calendar scheduled = Calendar.getInstance();
                scheduled.set(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue() - 1, LocalDateTime.now().getDayOfMonth() + (picker.getHour() < LocalDateTime.now().getHour() ? 1 : 0), picker.getHour(), picker.getMinute());
                scheduled.set(Calendar.SECOND, 0);
                Calendar maxTime = Calendar.getInstance();
                maxTime.set(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), LocalDateTime.now().getHour() + 5, LocalDateTime.now().getMinute());
                if (maxTime.getTimeInMillis() < scheduled.getTimeInMillis()) {
                    Toast.makeText(getActivity(), "Can't schedule the ride for more then 5h in advance!", Toast.LENGTH_SHORT).show();
                    ((TextView) view.findViewById(R.id.passenger_points_scheduled_time)).setText(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute());
                    return;
                }
                Bundle time = new Bundle();
                time.putLong(ARG_TIMESTAMP, scheduled.getTimeInMillis());
                getParentFragmentManager().setFragmentResult(EVENT_SCHEDULED_TIME_PICKED, time);
            });
            picker.show(getParentFragmentManager(), "TIME_PICKER");
        });

//        view.findViewById(R.id.passenger_points_find).setOnClickListener(v -> getParentFragmentManager().setFragmentResult(FragmentTransition.REQUEST_NEXT_STEP, new Bundle()));

        view.findViewById(R.id.passenger_points_find).setOnClickListener(v -> {
            Bundle fullAddress = new Bundle();
            fullAddress.putString(ARG_DEPARTURE, departure.getText().toString());
            fullAddress.putString(ARG_DESTINATION, destination.getText().toString());
            getParentFragmentManager().setFragmentResult(EVENT_FULL_ADDRESS_INPUT, fullAddress);
        });

        getParentFragmentManager().setFragmentResultListener(EVENT_POINTS_PICKED, requireActivity(), (requestKey, result) -> {
            departure.setText(result.getString(ARG_DEPARTURE));
            destination.setText(result.getString(ARG_DESTINATION));
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void toggleScheduler(View view, int visibility) {
        TransitionManager.beginDelayedTransition(view.findViewById(R.id.passenger_points_root), new Fade());
        ((TextView) view.findViewById(R.id.passenger_points_scheduled_time)).setText(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute());
        if(visibility == View.GONE) {
            Bundle time = new Bundle();
            time.putLong(ARG_TIMESTAMP, System.currentTimeMillis());
            getParentFragmentManager().setFragmentResult(EVENT_SCHEDULED_TIME_PICKED, time);
        }
        view.findViewById(R.id.passenger_points_time).setVisibility(visibility);
    }
}