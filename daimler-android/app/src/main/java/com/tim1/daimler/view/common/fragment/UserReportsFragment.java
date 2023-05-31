package com.tim1.daimler.view.common.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.GraphStatsDTO;
import com.tim1.daimler.dtos.user.UserGraphStatsDTO;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.components.charts.CharterBar;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReportsFragment extends Fragment {

    private static final int DEFAULT_ITEMS_COUNT = 31;
    private static final int DEFAULT_RANDOM_VALUE_MIN = 10;
    private static final int DEFAULT_RANDOM_VALUE_MAX = 100;

    private Context mContext;
    private TextView totalRides, totalDistance, totalCost;
    private TextView averageRides, averageDistance, averageCost;
    private TextView maxRides, maxDistance, maxCost;
    private final float[][] values = {
            fillRandomValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN),
            fillRandomValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN),
            fillRandomValues(DEFAULT_ITEMS_COUNT, DEFAULT_RANDOM_VALUE_MAX, DEFAULT_RANDOM_VALUE_MIN)
    };
    private CharterBar ridesChart, distanceChart, costChart;

    // Required empty public constructor
    public UserReportsFragment() { }

    public static UserReportsFragment newInstance() {
        return new UserReportsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_reports, container, false);
        mContext = container.getContext();

        int[] backgroundColors = new int[]{ContextCompat.getColor(container.getContext(), R.color.transparent)};

        ridesChart = view.findViewById(R.id.ridesChart);
        ridesChart.setValues(values[0]);
        ridesChart.setColors(getChartColors(container.getContext(), values[0], DEFAULT_RANDOM_VALUE_MAX/2.5f));

        distanceChart = view.findViewById(R.id.distanceChart);
        distanceChart.setValues(values[1]);
        distanceChart.setColors(getChartColors(container.getContext(), values[1], DEFAULT_RANDOM_VALUE_MAX/2.5f));

        costChart = view.findViewById(R.id.costChart);
        costChart.setValues(values[2]);
        costChart.setColors(getChartColors(container.getContext(), values[2], DEFAULT_RANDOM_VALUE_MAX/2.5f));

        totalRides = view.findViewById(R.id.totalRides);
        totalDistance = view.findViewById(R.id.totalDistance);
        totalCost = view.findViewById(R.id.totalCost);
        averageRides = view.findViewById(R.id.averageRides);
        averageDistance = view.findViewById(R.id.averageDistance);
        averageCost = view.findViewById(R.id.averageCost);
        maxRides = view.findViewById(R.id.max_rides_taken_graph);
        maxDistance = view.findViewById(R.id.max_distance_taken_graph);
        maxCost = view.findViewById(R.id.max_ride_cost_graph);

        SharedPreferences pref = getActivity().getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String role = pref.getString("userRole", "");
        if (role.equals("ROLE_DRIVER")) {
            LinearLayout costChartLayout = view.findViewById(R.id.costChartLayout);
            costChartLayout.setVisibility(View.GONE);
            LinearLayout rideCostStats = view.findViewById(R.id.rideCostStats);
            rideCostStats.setVisibility(View.GONE);
        }

        ((MaterialButton)view.findViewById(R.id.selectDateRange)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker<Pair<Long, Long>> picekr = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Pick Interval").build();
                picekr.show(getParentFragmentManager(), "date");
                picekr.addOnPositiveButtonClickListener(selection -> refresh(((Pair<Long, Long>)selection).first, ((Pair<Long, Long>)selection).second));
            }
        });

        Calendar startInterval = Calendar.getInstance();
        startInterval.setTimeInMillis(System.currentTimeMillis());
        startInterval.add(Calendar.DAY_OF_MONTH, -7);
        refresh(startInterval.getTimeInMillis(), System.currentTimeMillis());

        return view;
    }

    private void refresh(long from, long to) {
        SharedPreferences pref = getActivity().getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        Call<UserGraphStatsDTO> call = ServiceGenerator.registeredUserService.getGraphStats(pref.getInt("userId", 0), from, to);
        call.enqueue(new Callback<UserGraphStatsDTO>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<UserGraphStatsDTO> call, Response<UserGraphStatsDTO> response) {
                long days = TimeUnit.DAYS.convert(to - from, TimeUnit.MILLISECONDS);
                UserGraphStatsDTO stats = response.body();

                values[0] = toGraphData(stats.getRidesPerDay());
                ridesChart.setValues(values[0]);
                ridesChart.show();
                ridesChart.setColors(getChartColors(mContext, values[0], DEFAULT_RANDOM_VALUE_MAX/2.5f));

                values[1] = toGraphData(stats.getDistancePerDay());
                distanceChart.setValues(values[1]);
                distanceChart.show();
                distanceChart.setColors(getChartColors(mContext, values[1], DEFAULT_RANDOM_VALUE_MAX/2.5f));

                values[2] = toGraphData(stats.getAmountPerDay());
                costChart.setValues(values[2]);
                costChart.show();
                costChart.setColors(getChartColors(mContext, values[2], DEFAULT_RANDOM_VALUE_MAX/2.5f));

                totalRides.setText(String.valueOf(stats.getTotalRides()));
                totalDistance.setText(stats.getTotalDistance() + "km");
                totalCost.setText(stats.getAmount() + " RSD");
                averageRides.setText(String.valueOf(stats.getTotalRides()/days));
                averageDistance.setText(stats.getTotalDistance()/days + "km");
                averageCost.setText(stats.getAmount()/days + " RSD");
                maxRides.setText("RIDES TAKEN [max " + stats.getRidesPerDay().stream().max(Integer::compare).get() + "]");
                maxDistance.setText("DISTANCE DRIVEN [max " + stats.getDistancePerDay().stream().max(Integer::compare).get() + "km]");
                maxCost.setText("RIDE COST [max " + stats.getAmountPerDay().stream().max(Integer::compare).get() + " RSD]");
            }

            @Override
            public void onFailure(Call<UserGraphStatsDTO> call, Throwable t) {
                call.cancel();
                Toast.makeText(getActivity(), "There was a problem with a request.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float[] fillRandomValues(int length, int max, int min) {
        Random random = new Random();

        float[] newRandomValues = new float[length];
        for (int i = 0; i < newRandomValues.length; i++) {
            newRandomValues[i] = random.nextInt(max - min + 1) - min;
        }
        return newRandomValues;
    }

    private float[] toGraphData(List<Integer> items) {
        float[] newRandomValues = new float[items.size()];
        for (int i = 0; i < newRandomValues.length; i++) {
            newRandomValues[i] = items.get(i);
        }
        return newRandomValues;
    }

    private int[] getChartColors(Context context, float[] values, float threshold) {
        int[] newRandomValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] < threshold) newRandomValues[i] = ContextCompat.getColor(context, R.color.primary_light);
            else newRandomValues[i] = ContextCompat.getColor(context, R.color.primary);
        }
        return newRandomValues;
    }
}