package com.tim1.daimler.view.common.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.DepartureDestinationDTO;
import com.tim1.daimler.dtos.ride.LocationDTO;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public static final String EVENT_DRAW_ROUTE_AND_REFRESH = "draw_route_and_refresh";
    public static final String EVENT_DRAW_ROUTE = "draw_route";
    public static final String EVENT_CLEAR_MAP = "clear_map";

    public static final String ARG_FIXED = "arg_fixed";

    public static final String EVENT_ROUTE_DRAWN = "route_drawn";
    public static final String EVENT_DRAW_VEHICLE = "draw_vehicle";
    public static String ARG_VEHICLE_LONGITUDE = "arg_vehicle_longitude";
    public static String ARG_VEHICLE_LATITUDE = "arg_vehicle_latitude";
    public static String ARG_DESTINATION = "arg_destination";
    public static String ARG_DEPARTURE = "arg_departure";

    public static final String ARGS_DISTANCE = "arg_distance";
    public static final String ARGS_TIME = "arg_time";
    public static final String ARGS_LOCATIONS = "arg_locations";

    private LocationManager locationManager;
    private SupportMapFragment mMapFragment;
    private AlertDialog dialog;
    private GoogleMap map;
    private Marker home;
    private String provider;
    private boolean sendRefreshEvent = true;

    public MapFragment() { }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    private void createMapFragmentAndInflate() {
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.map_container, mMapFragment).commit();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        getParentFragmentManager().setFragmentResultListener(EVENT_DRAW_ROUTE_AND_REFRESH, requireActivity(), (requestKey, result) -> {
            view.post(new Runnable() {
                @Override
                public void run() {
                    handleDrawRouteRequest(result);
                }
            });
        });
        getParentFragmentManager().setFragmentResultListener(EVENT_DRAW_ROUTE, requireActivity(), (requestKey, result) -> {
            sendRefreshEvent = false;
            view.post(new Runnable() {
                @Override
                public void run() {
                    handleDrawRouteRequest(result);
                }
            });
        });
        getParentFragmentManager().setFragmentResultListener(EVENT_CLEAR_MAP, requireActivity(), (requestKey, result) -> {
            view.post(() -> {
                map.clear();
                startMarker = null;
                endMarker = null;
            });
        });
        getParentFragmentManager().setFragmentResultListener(EVENT_DRAW_VEHICLE, requireActivity(), (requestKey, result) -> {
            view.post(new Runnable() {
                @Override
                public void run() {
                    drawVehiclePin(result);
                }
            });
        });
        return view;
    }
    private void drawVehiclePin(Bundle result) {
        Double longitude = result.getDouble(ARG_VEHICLE_LONGITUDE);
        Double latitude = result.getDouble(ARG_VEHICLE_LATITUDE);
        if (vehicleMarker != null) vehicleMarker.remove();
        vehicleMarker = map.addMarker(new MarkerOptions()
                .title("VEHICLE")
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromBitmap(getVehicleMarkerIcon()))
                .position(new LatLng(latitude, longitude)));
    }

    private void handleDrawRouteRequest(Bundle result) {
        LocationDTO departure = (LocationDTO) result.getSerializable(ARG_DEPARTURE);
        LocationDTO destination = (LocationDTO) result.getSerializable(ARG_DESTINATION);
        map.clear();
        if (result.getString(ARG_FIXED) != null) {
            map.getUiSettings().setAllGesturesEnabled(false);
            map.setOnMarkerClickListener((marker)->true);
            map.setOnMapClickListener((latLong)->{});
            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {
                }
                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                }
                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {
                }
            });
        }
        startMarker = map.addMarker(new MarkerOptions()
                .title("DEPARTURE")
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromBitmap(getRouteMarkerIcon()))
                .position(new LatLng(departure.getLatitude(), departure.getLongitude())));
        endMarker = map.addMarker(new MarkerOptions()
                .title("DESTINATION")
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromBitmap(getRouteMarkerIcon()))
                .position(new LatLng(destination.getLatitude(), destination.getLongitude())));
        drawRoute();
    }

    @Override
    public void onLocationChanged(Location location) {
        //if (map != null) addCurrentLocationMarker(location);
    }

    private Marker startMarker = null;
    private Marker vehicleMarker = null;
    private Marker endMarker = null;
    private static String START_MARKER_ID = "departure";
    private static String END_MARKER_ID = "destination";
    private Polyline route;

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
        Location location = null;
        if (provider == null) Log.i("MAP", "Provider is null.");
        else {
            if (checkLocationPermission()) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    location = locationManager.getLastKnownLocation(provider);
                }
                else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    location = locationManager.getLastKnownLocation(provider);
                }
            }
        }

        map.setOnMapClickListener(latLng -> {
            if (endMarker != null) return;
            Marker marker = map.addMarker(new MarkerOptions()
                    .title(startMarker == null ? "DEPARTURE" : "DESTINATION")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(getRouteMarkerIcon()))
                    .position(latLng));
            marker.setTag(startMarker == null ? START_MARKER_ID : END_MARKER_ID);
            if(startMarker == null) startMarker = marker;
            else endMarker = marker;

            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(map.getCameraPosition().zoom).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            if (endMarker != null) drawRoute();
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) { }

            @Override
            public void onMarkerDrag(Marker marker) {
                map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                try {
                    drawRoute();
                } catch (Exception e) { }
            }
        });

        if (location != null) addCurrentLocationMarker(location);
    }

    private void drawRoute() {
        List<LatLng> path = new ArrayList();
        GeoApiContext context = new GeoApiContext.Builder().apiKey("API_KEY").build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, startMarker.getPosition().latitude + "," + startMarker.getPosition().longitude,
                endMarker.getPosition().latitude + "," + endMarker.getPosition().longitude);
        long meters = 0, minutes = 0;
        LocationDTO departure = null, destination = null;
        try {
            DirectionsResult res = req.await();
            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];
                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (i == 0) departure = new LocationDTO(leg.startAddress, leg.startLocation.lat, leg.startLocation.lng);
                        if (i == route.legs.length - 1) destination = new LocationDTO(leg.endAddress, leg.endLocation.lat, leg.endLocation.lng);
                        meters += leg.distance.inMeters;
                        minutes += leg.duration.inSeconds / 60;
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e("MAP", ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            if (route != null) route.remove();
            route = map.addPolyline(new PolylineOptions().addAll(path).color(Color.BLACK).width(10));
            if(!sendRefreshEvent) {
                sendRefreshEvent = true;
                return;
            }
            packData(meters, minutes, new DepartureDestinationDTO(departure, destination));
        }

        LatLngBounds bounds = LatLngBounds.builder().include(startMarker.getPosition()).include(endMarker.getPosition()).build();
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bounds.getCenter().latitude - 0.002, bounds.getCenter().longitude), map.getCameraPosition().zoom - 0.5f));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void packData(long distance, long time, DepartureDestinationDTO dto) {
        Bundle result = new Bundle();
        result.putLong(ARGS_DISTANCE, distance);
        result.putLong(ARGS_TIME, time);
        result.putSerializable(ARGS_LOCATIONS, dto);
        getParentFragmentManager().setFragmentResult(EVENT_ROUTE_DRAWN, result);
    }

    private void addCurrentLocationMarker(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//        if (home != null) home.remove();
//        home = map.addMarker(new MarkerOptions()
//                .title("YOUR POSITION")
//                .draggable(false)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
//                .position(loc));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(14).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Allow user location")
                        .setMessage("To continue working we need your locations....Allow now?")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else return true;
    }

    private Bitmap getVehicleMarkerIcon() {
        int height = 100;
        int width = 100;
        @SuppressLint("UseCompatLoadingForDrawables")
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.green_pin);
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    private Bitmap getRouteMarkerIcon() {
        int height = 100;
        int width = 100;
        @SuppressLint("UseCompatLoadingForDrawables")
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.gray_pin);
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        startMarker = null;
        endMarker = null;
        route = null;
        createMapFragmentAndInflate();
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean wifi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.i("MAP", String.valueOf(gps));
        Log.i("MAP", String.valueOf(wifi));
        if (!gps && !wifi) {
            Log.i("MAP", "Location disabled");
            Toast.makeText(getContext(), "Location disabled...", Toast.LENGTH_LONG).show();
        } else {
            if (checkLocationPermission()) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                    locationManager.requestLocationUpdates(provider, 2000, 0, this);
                }
                else if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(provider, 2000, 0, this);
                }
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
}