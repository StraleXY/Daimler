package com.tim1.daimler.view.passenger.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.dtos.ride.RidesDTO;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.model.Ride;
import com.tim1.daimler.model.Route;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.util.adapter.InboxAdapter;
import com.tim1.daimler.util.adapter.RideHistoryAdapter;
import com.tim1.daimler.util.data.InboxItem;
import com.tim1.daimler.view.common.activity.ChatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideHistoryActivity extends AppCompatActivity {

    private ArrayList<CreatedRideDTO> dataModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_ride_history);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.makeFullScreen(this);

        dataModels = new ArrayList<CreatedRideDTO>();

        ListView listView = (ListView) findViewById(R.id.rideHistoryList);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.header_ride_history, null, false));

        SharedPreferences pref = getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int id = pref.getInt("userId", 0);

        Call<RidesDTO> call = ServiceGenerator.passengerService.getRides(id, 1, 10000, "startTime", "", "");
        call.enqueue(new Callback<RidesDTO>() {
            @Override
            public void onResponse(Call<RidesDTO> call, Response<RidesDTO> response) {
                RidesDTO rides = response.body();
                if (rides != null && rides.getResults() != null) {
                    dataModels = new ArrayList<CreatedRideDTO>(rides.getResults());
                    dataModels.sort(Comparator.comparingLong(CreatedRideDTO::getScheduledTimestamp).reversed());
                }
                initList();
            }

            @Override
            public void onFailure(Call<RidesDTO> call, Throwable t) {

            }
        });
        initList();
    }

    private void initList() {
        RideHistoryAdapter adapter = new RideHistoryAdapter(dataModels, this);
        ListView listView = (ListView) findViewById(R.id.rideHistoryList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> openRideDetails(view, dataModels.get(i-1)));
    }

    private void openRideDetails(View view, CreatedRideDTO ride) {
        Intent transition = new Intent(PassengerRideHistoryActivity.this, PassengerRideActivity.class);
        transition.putExtra(PassengerRideActivity.ARG_RIDE, ride);
        //transition.putExtra("DATE", ride.getDate());
        startActivity(transition);
    }

    public void backClicked(View view) {
        finish();
    }
}