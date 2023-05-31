package com.tim1.daimler.view.driver.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.driver.SimpleStatsDTO;
import com.tim1.daimler.util.ServiceGenerator;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRidesStatsFragment extends Fragment {

    public DriverRidesStatsFragment() {
        // Required empty public constructor
    }

    TextView earnings, acceptedRides, declinedRides, workHours;

    public static DriverRidesStatsFragment newInstance() {
        return new DriverRidesStatsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_rides_stats, container, false);
        // Stats Text Views
        earnings = view.findViewById(R.id.earnings);
        acceptedRides = view.findViewById(R.id.acceptedRides);
        declinedRides = view.findViewById(R.id.declinedRides);
        workHours = view.findViewById(R.id.workHours);
        refresh(getString(R.string.button_daily));
        // Period Selector
        MaterialButtonToggleGroup statsPeriodSelector = view.findViewById(R.id.statsPeriodSelector);
        statsPeriodSelector.check(R.id.statsPeriodSelector_daily);
        statsPeriodSelector.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (!isChecked) return;
                CharSequence text = ((Button) view.findViewById(checkedId)).getText();
                refresh(text);
            }
        });
        return view;
    }
    private void refresh(CharSequence text) {
        SharedPreferences pref = getActivity().getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(System.currentTimeMillis());
        if (text.equals(getString(R.string.button_monthly))) from.add(Calendar.MONTH, -1);
        else if (text.equals(getString(R.string.button_weekly))) from.add(Calendar.DAY_OF_MONTH, -7);
        else from.add(Calendar.HOUR_OF_DAY, -24);
        Call<SimpleStatsDTO> call = ServiceGenerator.driverService.getSimpleStats(pref.getInt("userId", 0), from.getTimeInMillis(), System.currentTimeMillis());
        call.enqueue(new Callback<SimpleStatsDTO>() {
            @Override
            public void onResponse(Call<SimpleStatsDTO> call, Response<SimpleStatsDTO> response) {
                SimpleStatsDTO stats = response.body();
                earnings.setText(stats.getEarned() + " RSD");
                acceptedRides.setText(String.valueOf(stats.getAccepted()));
                declinedRides.setText(String.valueOf(stats.getRejected()));
                workHours.setText(String.valueOf(stats.getWorkingHours()));
            }
            @Override
            public void onFailure(Call<SimpleStatsDTO> call, Throwable t) {
                call.cancel();
                Toast.makeText(getActivity(), "There was a problem with a request.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}