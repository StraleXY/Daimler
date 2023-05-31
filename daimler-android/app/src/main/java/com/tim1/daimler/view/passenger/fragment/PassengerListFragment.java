package com.tim1.daimler.view.passenger.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.dtos.ride.VehicleDTO;
import com.tim1.daimler.dtos.user.UserInRideDTO;
import com.tim1.daimler.model.Route;
import com.tim1.daimler.model.User;
import com.tim1.daimler.util.Bitmaper;
import com.tim1.daimler.util.adapter.FavouriteRoutesAdapter;
import com.tim1.daimler.util.adapter.UserAdapter;

import java.util.ArrayList;

public class PassengerListFragment extends Fragment {

    public static String EVENT_SHOW_PASSENGERS = "event_show_passengers";
    public static String ARG_PASSENGER_LIST = "arg_passenger_list";

    public PassengerListFragment() {
    }

    public static PassengerListFragment newInstance() {
        return new PassengerListFragment();
    }

    private ArrayList<UserInRideDTO> passengers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        passengers = new ArrayList<UserInRideDTO>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_passenger_list, container, false);
        Context context = container.getContext();
        SharedPreferences pref = container.getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String role = pref.getString("userRole", "");

        getParentFragmentManager().setFragmentResultListener(EVENT_SHOW_PASSENGERS, requireActivity(), (requestKey, result) -> {
            if (result.getSerializable(ARG_PASSENGER_LIST) != null) {
                passengers = (ArrayList<UserInRideDTO>) result.getSerializable(ARG_PASSENGER_LIST);
            }
            showPassengers(view, context);
        });

        if (role.equals("ROLE_PASSENGER")) {
            TextView headerText = view.findViewById(R.id.passengerListHeaderText);
            headerText.setText("OTHER PASSENGERS");
        }
        return view;
    }

    private void showPassengers(View view, Context context) {
        UserAdapter adapter = new UserAdapter(passengers, context);
        ListView listView = (ListView) view.findViewById(R.id.passengerList);
        //listView.addHeaderView(getLayoutInflater().inflate(R.layout.header_passenger_list, null, false));
        listView.setAdapter(adapter);
    }
}