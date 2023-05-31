package com.tim1.daimler.view.driver.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.dtos.ride.LocationDTO;
import com.tim1.daimler.dtos.ride.ReasonDTO;
import com.tim1.daimler.dtos.ride.VehicleDTO;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.util.FragmentTransition;
import com.tim1.daimler.util.MessageReceiver;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.util.Websocketer;
import com.tim1.daimler.view.common.activity.LoginActivity;
import com.tim1.daimler.view.common.fragment.FinishedRideFragment;
import com.tim1.daimler.view.common.fragment.MapFragment;
import com.tim1.daimler.view.driver.fragment.DriverNewRideInfoFragment;
import com.tim1.daimler.view.common.activity.InboxActivity;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverMainActivity extends AppCompatActivity {

    BottomAppBar appBar;

    CreatedRideDTO ride;
    VehicleDTO vehicle;
    Integer id;
    UserDTO driver;
    private RelativeLayout infoCard;
    private TextView rideTimer;
    private MaterialCardView rideTimerCard;
    private Timer rideTimeCounter = null;

    private RelativeLayout finishedCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.hideStatusBar(this, findViewById(R.id.rootDriverMain));

        MaterialButtonToggleGroup stateGroup = findViewById(R.id.toggleDriverState);
        stateGroup.check(R.id.driverStateActive);
        infoCard = findViewById(R.id.driver_new_route_container);
        infoCard.setVisibility(View.GONE);
        finishedCard = findViewById(R.id.finished_ride_fragment_driver);
        finishedCard.setVisibility(View.GONE);

        rideTimer = findViewById(R.id.driver_ride_timer);
        rideTimerCard = findViewById(R.id.driver_ride_timer_card);

        appBar = findViewById(R.id.bottomAppBarDriverMain);
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Navigation", Toast.LENGTH_SHORT).show();
            }
        });
        appBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.inboxAppBar) {
                Intent showInbox = new Intent(DriverMainActivity.this, InboxActivity.class);
                showInbox.putExtra(InboxActivity.ARG_USER, driver);
                startActivity(showInbox);
            } else if (item.getItemId() == R.id.accountAppBar) {
                startActivity(new Intent(DriverMainActivity.this, DriverAccountActivity.class));
            } else if (item.getItemId() == R.id.logoutAppBar) {
                SharedPreferences pref = getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                pref.edit().clear().apply();
                startActivity(new Intent(DriverMainActivity.this, LoginActivity.class));
                finish();
            } else if (item.getItemId() == R.id.historyAppBar) {
                startActivity(new Intent(DriverMainActivity.this, DriverRideHistoryActivity.class));
            }
            return false;
        });

        FragmentTransition.to(MapFragment.newInstance(), this, false, R.id.driver_map_fragment_container);
        FragmentTransition.to(DriverNewRideInfoFragment.newInstance(), this, false, R.id.driver_new_route_container);
        FragmentTransition.to(FinishedRideFragment.newInstance(), this, false, R.id.finished_ride_fragment_driver);

        SharedPreferences pref = getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        this.id = pref.getInt("userId", 0);
        ServiceGenerator.initDriverService(pref.getString("authToken", ""));
        Websocketer.createWebSocketClient(String.valueOf(id), new MessageReceiver() {
            @Override
            public void receive(String message) {
                if (message.split(",")[0].equals("ride")) {
                    currentRide(message);
                } else if (message.split(",")[0].equals("vehicle")) {
                    updateVehicleLocation(message);
                }
            }
        });
        ServiceGenerator.driverService.getById(this.id).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                driver = response.body();
                tryResumeRide();
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                call.cancel();
            }
        });

        getSupportFragmentManager().setFragmentResultListener(DriverNewRideInfoFragment.EVENT_ACCEPT_RIDE, this, (requestKey, result) -> {
            acceptanceRide();
        });
        getSupportFragmentManager().setFragmentResultListener(DriverNewRideInfoFragment.EVENT_FINISH_RIDE, this, (requestKey, result) -> {
            Call<CreatedRideDTO> call = ServiceGenerator.rideService.endRide(ride.getId());
            call.enqueue(new Callback<CreatedRideDTO>() {
                @Override
                public void onResponse(Call<CreatedRideDTO> call, Response<CreatedRideDTO> response) {
                    finishedRide(FinishedRideFragment.EVENT_SET_FINISHED_TEXT);
                }
                @Override
                public void onFailure(Call<CreatedRideDTO> call, Throwable t) {
                    call.cancel();
                }
            });
        });
        getSupportFragmentManager().setFragmentResultListener(DriverNewRideInfoFragment.EVENT_REJECT_RIDE, this, (requestKey, result) -> {
            Call<CreatedRideDTO> call = ServiceGenerator.rideService.cancelRide(ride.getId(), new ReasonDTO("Driver rejected the ride!"));
            call.enqueue(new Callback<CreatedRideDTO>() {
                @Override
                public void onResponse(Call<CreatedRideDTO> call, Response<CreatedRideDTO> response) {
                    finishedRide(FinishedRideFragment.EVENT_SET_REJECT_TEXT);
                }
                @Override
                public void onFailure(Call<CreatedRideDTO> call, Throwable t) {
                    call.cancel();
                }
            });
        });
        getSupportFragmentManager().setFragmentResultListener(DriverNewRideInfoFragment.EVENT_PANIC_RIDE, this, (requestKey, result) -> {
            Call<CreatedRideDTO> call = ServiceGenerator.rideService.panicRide(ride.getId(), new ReasonDTO("Driver panic!!!"));
            call.enqueue(new Callback<CreatedRideDTO>() {
                @Override
                public void onResponse(Call<CreatedRideDTO> call, Response<CreatedRideDTO> response) {
                    finishedRide(FinishedRideFragment.EVENT_SET_PANIC_TEXT);
                }
                @Override
                public void onFailure(Call<CreatedRideDTO> call, Throwable t) {
                    call.cancel();
                }
            });
        });
        this.tryResumeRide();
    }

    public void acceptanceRide() {

        Call<CreatedRideDTO> call = ServiceGenerator.rideService.acceptRide(ride.getId());
        call.enqueue(new Callback<CreatedRideDTO>() {
            @Override
            public void onResponse(Call<CreatedRideDTO> call, Response<CreatedRideDTO> response) {
                activeRide();
            }
            @Override
            public void onFailure(Call<CreatedRideDTO> call, Throwable t) {
                call.cancel();
                Toast toast = Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void updateVehicleLocation(String message) {
        drawVehiclePin(Double.parseDouble(message.split(",")[1]), Double.parseDouble(message.split(",")[2]));
    }

    public void drawVehiclePin(Double longitude, Double latitude) {
        Bundle drawPin = new Bundle();
        drawPin.putSerializable(MapFragment.ARG_VEHICLE_LONGITUDE, longitude);
        drawPin.putSerializable(MapFragment.ARG_VEHICLE_LATITUDE, latitude);
        getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_DRAW_VEHICLE, drawPin);
    }

    public void activeRide() {
        Bundle drawRoute = new Bundle();
        drawRoute.putSerializable(MapFragment.ARG_DEPARTURE, new LocationDTO(Objects.requireNonNull(ride.getLocations().get(0).getDeparture()).getAddress(), ride.getLocations().get(0).getDeparture().getLatitude(), ride.getLocations().get(0).getDeparture().getLongitude()));
        drawRoute.putSerializable(MapFragment.ARG_DESTINATION, new LocationDTO(Objects.requireNonNull(ride.getLocations().get(0).getDestination()).getAddress(), ride.getLocations().get(0).getDestination().getLatitude(), ride.getLocations().get(0).getDestination().getLongitude()));
        getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_DRAW_ROUTE, drawRoute);
        infoCard = findViewById(R.id.driver_new_route_container);
        infoCard.setVisibility(View.VISIBLE);

        rideTimerCard.post(new Runnable() {
            @Override
            public void run() {
                rideTimerCard.setVisibility(View.VISIBLE);
            }
        });

        if (rideTimeCounter == null) {
            rideTimeCounter = new Timer();
            long startTime = System.currentTimeMillis();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - startTime;
                    long seconds = (elapsedTime / 1000) % 60;
                    long minutes = (elapsedTime / 1000 / 60) % 60;
                    long hours = (elapsedTime / 1000 / 60 / 60);
                    String h = (hours < 10 ? "0" : "") + hours;
                    String m = (minutes < 10 ? "0" : "") + minutes;
                    String s = (seconds < 10 ? "0" : "") + seconds;
                    rideTimer.post(new Runnable() {
                        @Override
                        public void run() {
                            rideTimer.setText(h + ":" + m + ":" + s);
                        }
                    });
                }
            };
            rideTimeCounter.schedule(task, 0, 500);
        }
    }

    public void currentRide(String message) {
        Call<CreatedRideDTO> call = ServiceGenerator.rideService.getRide(Integer.parseInt(message.split(",")[1]));
        call.enqueue(new Callback<CreatedRideDTO>() {
            @Override
            public void onResponse(Call<CreatedRideDTO> call, Response<CreatedRideDTO> response) {
                ride = response.body();
                startRide();
            }
            @Override
            public void onFailure(Call<CreatedRideDTO> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private boolean isActive = true;
    public void changeActivityStatus(View view) {
        isActive = !isActive;
        ((MaterialButton)view).setText(getString(isActive ? R.string.button_active : R.string.button_inactive));
    }

    public void startRide() {
        Call<VehicleDTO> call = ServiceGenerator.rideService.getDriversVehicle(this.id);
        call.enqueue(new Callback<VehicleDTO>() {
            @Override
            public void onResponse(Call<VehicleDTO> call, Response<VehicleDTO> response) {
                VehicleDTO vehicle = response.body();
                if (ride.getStatus().equals("ACCEPTED")) {
                    Bundle drawRoute = new Bundle();
                    drawRoute.putSerializable(MapFragment.ARG_DEPARTURE, new LocationDTO("", Objects.requireNonNull(vehicle).getCurrentLocation().getLatitude(), vehicle.getCurrentLocation().getLongitude()));
                    drawRoute.putSerializable(MapFragment.ARG_DESTINATION, new LocationDTO(Objects.requireNonNull(ride.getLocations().get(0).getDeparture()).getAddress(), ride.getLocations().get(0).getDeparture().getLatitude(), ride.getLocations().get(0).getDeparture().getLongitude()));
                    getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_DRAW_ROUTE, drawRoute);
                    infoCard.setVisibility(View.VISIBLE);
                    Bundle routeDetails = new Bundle();
                    routeDetails.putSerializable(DriverNewRideInfoFragment.ARG_RIDE, ride);
                    getSupportFragmentManager().setFragmentResult(DriverNewRideInfoFragment.EVENT_DRIVE_TO_DEPARTURE, routeDetails);
                } else if (ride.getStatus().equals("ACTIVE")) {
                    activeRide();
                } else if (ride.getStatus().equals("FINISHED") || ride.getStatus().equals("PANIC")) {
                    finishedRide(ride.getStatus().equals("FINISHED") ? FinishedRideFragment.EVENT_SET_FINISHED_TEXT : FinishedRideFragment.EVENT_SET_PANIC_TEXT);
                }
                drawVehiclePin(vehicle.getCurrentLocation().getLongitude(), vehicle.getCurrentLocation().getLatitude());
            }

            @Override
            public void onFailure(Call<VehicleDTO> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void finishedRide(String event) {
        infoCard = findViewById(R.id.driver_new_route_container);
        infoCard.setVisibility(View.GONE);
        getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_CLEAR_MAP, new Bundle());
        finishedCard = findViewById(R.id.finished_ride_fragment_driver);
        rideTimerCard.setVisibility(View.GONE);
        if(rideTimeCounter != null) rideTimeCounter.cancel();
        rideTimeCounter = null;
        finishedCard.setVisibility(View.VISIBLE);
        getSupportFragmentManager().setFragmentResult(event, new Bundle());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> finishedCard.setVisibility(View.GONE));
            }
        }, 5000);
    }

    public void showAccountDetails(View view) {
        startActivity(new Intent(DriverMainActivity.this, DriverAccountActivity.class));
    }

    public void showInbox(View view) {
        Intent showInbox = new Intent(DriverMainActivity.this, InboxActivity.class);
        showInbox.putExtra(InboxActivity.ARG_USER, driver);
        startActivity(showInbox);
    }

    public void showRideHistory(View view) {
        startActivity(new Intent(DriverMainActivity.this, DriverRideHistoryActivity.class));
    }

    public void logout(View view) {
        startActivity(new Intent(DriverMainActivity.this, LoginActivity.class));
        finish();
    }

    private void tryResumeRide() {
        Call<CreatedRideDTO> call = ServiceGenerator.rideService.getDriverActiveRide(this.id);
        call.enqueue(new Callback<CreatedRideDTO>() {
            @Override
            public void onResponse(Call<CreatedRideDTO> call, Response<CreatedRideDTO> response) {
                CreatedRideDTO testRide = response.body();
                if (testRide == null) return;
                ride = testRide;
                Bundle res = new Bundle();
                res.putString(DriverNewRideInfoFragment.ARG_STATUS, ride.getStatus());
                getSupportFragmentManager().setFragmentResult(DriverNewRideInfoFragment.EVENT_SET_RIDE_BUTTONS, res);
                startRide();
            }
            @Override
            public void onFailure(Call<CreatedRideDTO> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(driver != null) tryResumeRide();
    }

}