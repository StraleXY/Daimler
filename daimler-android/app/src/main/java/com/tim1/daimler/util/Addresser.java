package com.tim1.daimler.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Addresser {

    public static String getAddressFromLatLng(Context context, Double latitude, Double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> list;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        Address address = list.get(0);
        return address.getAddressLine(0);
    }

    public static Address getAddressFromString(Context context, final String locationAddress) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                return (Address) addressList.get(0);
            }
        } catch (Exception e) {
            Log.d("MAP", "Unable to connect to Geocoder", e);
            return null;
        }
        return null;
    }
}
