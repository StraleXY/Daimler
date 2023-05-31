package com.tim1.daimler.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.user.UserInRideDTO;
import com.tim1.daimler.model.Ride;
import com.tim1.daimler.model.User;
import com.tim1.daimler.util.Bitmaper;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<UserInRideDTO> {

    private ArrayList<UserInRideDTO> dataSet;
    Context context;

    private static class ViewHolder {
        TextView name;
        TextView surname;
        ImageView profileImage;
    }

    public UserAdapter(ArrayList<UserInRideDTO> data, Context context){
        super(context, R.layout.list_ride_history, data);
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserInRideDTO user = getItem(position);
        UserAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new UserAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_passengers, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.passengerName);
            viewHolder.surname = (TextView) convertView.findViewById(R.id.passengerSurname);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.passengerProfileImage);
            convertView.setTag(viewHolder);
        }
        else viewHolder = (UserAdapter.ViewHolder) convertView.getTag();
        viewHolder.name.setText(user.getName());
        viewHolder.surname.setText(user.getSurname());
        viewHolder.profileImage.setImageBitmap(Bitmaper.toBitmap(user.getProfilePicture()));
        return convertView;
    }
}
