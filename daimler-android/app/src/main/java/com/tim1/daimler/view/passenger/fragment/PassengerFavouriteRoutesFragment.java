package com.tim1.daimler.view.passenger.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.passenger.FavoriteRouteDTO;
import com.tim1.daimler.model.Route;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.util.Websocketer;
import com.tim1.daimler.util.adapter.FavouriteRoutesAdapter;
import com.tim1.daimler.util.adapter.InboxAdapter;
import com.tim1.daimler.util.data.InboxItem;
import com.tim1.daimler.view.passenger.activity.PassengerMainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PassengerFavouriteRoutesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PassengerFavouriteRoutesFragment extends Fragment {

    public PassengerFavouriteRoutesFragment() {
        // Required empty public constructor
    }

    public static PassengerFavouriteRoutesFragment newInstance() {
        return new PassengerFavouriteRoutesFragment();
    }

    private ArrayList<FavoriteRouteDTO> dataModels;
    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataModels = new ArrayList<FavoriteRouteDTO>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_passenger_favourite_routes, container, false);
        Context context = container.getContext();

        SharedPreferences pref = container.getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        this.id = pref.getInt("userId", 0);
        ServiceGenerator.passengerService.getFavoriteRoutes(id);
        System.out.println(id);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Call<List<FavoriteRouteDTO>> call = ServiceGenerator.passengerService.getFavoriteRoutes(id);
                call.enqueue(new Callback<List<FavoriteRouteDTO>>() {
                    @Override
                    public void onResponse(Call<List<FavoriteRouteDTO>> call, Response<List<FavoriteRouteDTO>> response) {
                        List<FavoriteRouteDTO> routes = response.body();
                        if (routes == null) {
                            routes = new ArrayList<FavoriteRouteDTO>();
                        }
                        if (routes.size() != dataModels.size()) {
                            dataModels = new ArrayList<>(routes);
                            initList(view, context);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FavoriteRouteDTO>> call, Throwable t) {

                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 500);
        initList(view, context);
        return view;
    }

    private void initList(View view, Context context) {
        FavouriteRoutesAdapter adapter = new FavouriteRoutesAdapter(dataModels, context);
        ListView listView = (ListView) view.findViewById(R.id.favouriteRoutesList);
        //listView.addHeaderView(getLayoutInflater().inflate(R.layout.header_favourite_routes, null, false));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, v, i, l) -> orderRide(v, adapter.get(i)));
    }

    private void orderRide(View view, FavoriteRouteDTO favoriteRoute) {
        Intent intent = new Intent(getActivity(), PassengerMainActivity.class);
        intent.putExtra(PassengerMainActivity.ARG_DEPARTURE, favoriteRoute.getDeparture().getAddress());
        intent.putExtra(PassengerMainActivity.ARG_DESTINATION, favoriteRoute.getDestination().getAddress());
        startActivity(intent);
    }
}