package com.tim1.daimler.util.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.message.InboxDTO;
import com.tim1.daimler.dtos.passenger.FavoriteRouteDTO;
import com.tim1.daimler.model.Route;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.data.InboxItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteRoutesAdapter extends ArrayAdapter<FavoriteRouteDTO> {

    private ArrayList<FavoriteRouteDTO> dataSet;
    Context context;

    private static class ViewHolder {
        TextView sourceAddress;
        TextView destinationAddress;
        MaterialButton removeButton;
    }

    public FavouriteRoutesAdapter(ArrayList<FavoriteRouteDTO> data, Context context){
        super(context, R.layout.list_favourite_route, data);
        this.dataSet = data;
        this.context = context;
    }

    public FavoriteRouteDTO get(Integer id) {
        return (FavoriteRouteDTO) dataSet.get(id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FavoriteRouteDTO route = getItem(position);
        FavouriteRoutesAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_favourite_route, parent, false);
            viewHolder.sourceAddress = (TextView) convertView.findViewById(R.id.routeSourceAddress);
            viewHolder.destinationAddress = (TextView) convertView.findViewById(R.id.routeDestinationAddress);
            viewHolder.removeButton = (MaterialButton) convertView.findViewById(R.id.favoriteRouteRemove);
            convertView.setTag(viewHolder);
        }
        else viewHolder = (FavouriteRoutesAdapter.ViewHolder) convertView.getTag();

        viewHolder.sourceAddress.setText(route.getDeparture().getAddress());
        viewHolder.destinationAddress.setText(route.getDestination().getAddress());
        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = parent.getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                int id = pref.getInt("userId", 0);
                ServiceGenerator.passengerService.deleteFavoriteRoute(id, route.getId()).enqueue(new Callback<FavoriteRouteDTO>() {
                    @Override
                    public void onResponse(Call<FavoriteRouteDTO> call, Response<FavoriteRouteDTO> response) {
                        Toast.makeText(parent.getContext(), "Route successfully removed from favorites!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<FavoriteRouteDTO> call, Throwable t) {
                        Toast.makeText(parent.getContext(), "Failed to remove route from favorites!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return convertView;
    }
}
