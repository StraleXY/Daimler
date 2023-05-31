package com.tim1.daimler.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.model.Ride;
import com.tim1.daimler.model.Route;
import com.tim1.daimler.util.Dater;

import java.util.ArrayList;

public class RideHistoryAdapter extends ArrayAdapter<CreatedRideDTO> {

    private ArrayList<CreatedRideDTO> dataSet;
    Context context;

    private static class ViewHolder {
        TextView sourceAddress;
        TextView destinationAddress;
        TextView dateTime;
    }

    public RideHistoryAdapter(ArrayList<CreatedRideDTO> data, Context context){
        super(context, R.layout.list_ride_history, data);
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CreatedRideDTO ride = getItem(position);
        RideHistoryAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new RideHistoryAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_ride_history, parent, false);
            viewHolder.sourceAddress = (TextView) convertView.findViewById(R.id.rideSourceAddress);
            viewHolder.destinationAddress = (TextView) convertView.findViewById(R.id.rideDestinationAddress);
            viewHolder.dateTime = (TextView) convertView.findViewById(R.id.rideDateTime);
            convertView.setTag(viewHolder);
        }
        else viewHolder = (RideHistoryAdapter.ViewHolder) convertView.getTag();

        viewHolder.sourceAddress.setText(ride.getLocations().get(0).getDeparture().getAddress());
        viewHolder.destinationAddress.setText(ride.getLocations().get(0).getDestination().getAddress());
        viewHolder.dateTime.setText(Dater.toDate(ride.getScheduledTimestamp()));

        return convertView;
    }

}
