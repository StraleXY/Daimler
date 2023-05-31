package com.tim1.daimler.view.passenger.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.WindowInsetsControllerCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.ride.AssumptionDTO;
import com.tim1.daimler.dtos.ride.CreateRideDTO;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.dtos.ride.DepartureDestinationDTO;
import com.tim1.daimler.dtos.ride.EstimationDTO;
import com.tim1.daimler.dtos.ride.LocationDTO;
import com.tim1.daimler.dtos.ride.ReasonDTO;
import com.tim1.daimler.dtos.ride.VehicleDTO;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.dtos.user.UserShortDTO;
import com.tim1.daimler.util.Addresser;
import com.tim1.daimler.util.FragmentTransition;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Stepper;
import com.tim1.daimler.util.Websocketer;
import com.tim1.daimler.view.common.activity.InboxActivity;
import com.tim1.daimler.view.common.activity.LoginActivity;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.view.common.fragment.FinishedRideFragment;
import com.tim1.daimler.view.common.fragment.MapFragment;
import com.tim1.daimler.view.driver.fragment.DriverNewRideInfoFragment;
import com.tim1.daimler.view.passenger.fragment.PassengerNewRouteDriverInfoFragment;
import com.tim1.daimler.view.passenger.fragment.PassengerNewRoutePointsFragment;
import com.tim1.daimler.view.passenger.fragment.PassengerNewRouteRidePropertiesFragment;
import com.tim1.daimler.view.passenger.fragment.PassengerNewRouteSuggestionFragment;
import com.tim1.daimler.view.passenger.fragment.PassengerRateDialogFragment;

import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerMainActivity extends AppCompatActivity {

    public static String ARG_DEPARTURE = "arg_departure";
    public static String ARG_DESTINATION = "arg_destination";

    private MaterialCardView scheduledCard;
    private Stepper newRouteStepper;
    private UserDTO passenger;
    private Integer id;
    private AssumptionDTO rideDetails = new AssumptionDTO();
    private EstimationDTO estimation = new EstimationDTO();
    private long scheduledTime = System.currentTimeMillis();
    private boolean addressFilled = false;
    private CreatedRideDTO ride;
    private MaterialCardView rideTimerCard;
    private TextView rideTimer;
    private RelativeLayout finishedCard;
    private Timer rideTimeCounter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_main);
        initServices();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.hideStatusBar(this, findViewById(R.id.passengerMainRoot));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        finishedCard = findViewById(R.id.finished_ride_fragment_passenger);
        if(isTablet(this)) {
            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.END;
            params.rightMargin = ((CoordinatorLayout.LayoutParams) ((MaterialCardView) findViewById(R.id.customAppBar)).getLayoutParams()).rightMargin;
            params.bottomMargin = ((CoordinatorLayout.LayoutParams) ((MaterialCardView) findViewById(R.id.customAppBar)).getLayoutParams()).bottomMargin;
            findViewById(R.id.customAppBar).setLayoutParams(params);
        }
        scheduledCard = findViewById(R.id.scheduled_ride_card);
        finishedCard.setVisibility(View.GONE);

        FragmentTransition.to(MapFragment.newInstance(), this, false, R.id.passenger_map_fragment_container);
        FragmentTransition.to(FinishedRideFragment.newInstance(), this, false, R.id.finished_ride_fragment_passenger);

        rideTimer = findViewById(R.id.passenger_ride_timer);
        rideTimerCard = findViewById(R.id.passenger_ride_timer_card);

        newRouteStepper = new Stepper(this, findViewById(R.id.passenger_appbar_placeholder), findViewById(R.id.passenger_steps_navigation_placeholder), findViewById(R.id.passenger_appbar_navigation_previous));
        newRouteStepper.addStep(PassengerNewRoutePointsFragment.newInstance(), findViewById(R.id.passenger_points_placeholder));
        newRouteStepper.addStep(PassengerNewRouteRidePropertiesFragment.newInstance(), findViewById(R.id.passenger_ride_properties_placeholder));
        newRouteStepper.addStep(PassengerNewRouteSuggestionFragment.newInstance(), findViewById(R.id.passenger_route_suggestion_placeholder));
        newRouteStepper.addStep(PassengerNewRouteDriverInfoFragment.newInstance(), findViewById(R.id.passenger_driver_info_placeholder));
        //newRouteStepper.addStep(PassengerNewRouteFriendsFragment.newInstance(), findViewById(R.id.passenger_friends_placeholder));

        getSupportFragmentManager().setFragmentResultListener(FragmentTransition.REQUEST_NEXT_STEP, this, (requestKey, result) -> newRouteStepper.next());
        // Address input
        getSupportFragmentManager().setFragmentResultListener(PassengerNewRoutePointsFragment.EVENT_FULL_ADDRESS_INPUT, this, (requestKey, result) -> {
            Address departure = Addresser.getAddressFromString(this, result.getString(PassengerNewRoutePointsFragment.ARG_DEPARTURE));
            Address destination = Addresser.getAddressFromString(this, result.getString(PassengerNewRoutePointsFragment.ARG_DESTINATION));
            Bundle drawRoute = new Bundle();
            drawRoute.putSerializable(MapFragment.ARG_DEPARTURE, new LocationDTO(Objects.requireNonNull(departure).getAddressLine(0), departure.getLatitude(), departure.getLongitude()));
            drawRoute.putSerializable(MapFragment.ARG_DESTINATION, new LocationDTO(Objects.requireNonNull(destination).getAddressLine(0), destination.getLatitude(), destination.getLongitude()));
            addressFilled = true;
            getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_DRAW_ROUTE_AND_REFRESH, drawRoute);
            newRouteStepper.next();
        });
        // Get time
        getSupportFragmentManager().setFragmentResultListener(PassengerNewRoutePointsFragment.EVENT_SCHEDULED_TIME_PICKED, this, (requestKey, result) -> {
            scheduledTime = result.getLong(PassengerNewRoutePointsFragment.ARG_TIMESTAMP);
        });
        // Route drawn
        getSupportFragmentManager().setFragmentResultListener(MapFragment.EVENT_ROUTE_DRAWN, this, (requestKey, result) -> {
            rideDetails.setLocation((DepartureDestinationDTO) result.getSerializable(MapFragment.ARGS_LOCATIONS));
            estimation.setDistance((double) result.getLong(MapFragment.ARGS_DISTANCE, 0));
            estimation.setEstimatedTimeInMinutes(Math.toIntExact(result.getLong(MapFragment.ARGS_TIME, 0)));
            if(addressFilled) {
                addressFilled = false;
                return;
            }
            Bundle pointAddresses = new Bundle();
            pointAddresses.putString(PassengerNewRoutePointsFragment.ARG_DEPARTURE, Addresser.getAddressFromLatLng(this, rideDetails.getLocations().get(0).getDeparture().getLatitude(), rideDetails.getLocations().get(0).getDeparture().getLongitude()));
            pointAddresses.putString(PassengerNewRoutePointsFragment.ARG_DESTINATION, Addresser.getAddressFromLatLng(this, rideDetails.getLocations().get(0).getDestination().getLatitude(), rideDetails.getLocations().get(0).getDestination().getLongitude()));
            getSupportFragmentManager().setFragmentResult(PassengerNewRoutePointsFragment.EVENT_POINTS_PICKED, pointAddresses);
            newRouteStepper.jumpTo(0);
        });
        // Extra properties selected
        getSupportFragmentManager().setFragmentResultListener(PassengerNewRouteRidePropertiesFragment.EVENT_PROPERTIES_PICKED, this, (requestKey, result) -> {
            rideDetails.setBabyTransport(result.getBoolean(PassengerNewRouteRidePropertiesFragment.ARGS_BABY));
            rideDetails.setPetTransport(result.getBoolean(PassengerNewRouteRidePropertiesFragment.ARGS_PET));
            rideDetails.setVehicleType(result.getString(PassengerNewRouteRidePropertiesFragment.ARGS_CAR_TYPE));
            Call<EstimationDTO> call = ServiceGenerator.userService.getEstimate(rideDetails);
            call.enqueue(new Callback<EstimationDTO>() {
                @Override
                public void onResponse(Call<EstimationDTO> call, Response<EstimationDTO> response) {
                    EstimationDTO estimate = response.body();
                    Bundle result = new Bundle();
                    result.putSerializable(PassengerNewRouteSuggestionFragment.ARGS_ESTIMATE, estimate);
                    getSupportFragmentManager().setFragmentResult(PassengerNewRouteSuggestionFragment.EVENT_FILL_DETAILS, result);
                    newRouteStepper.next();
                }
                @Override
                public void onFailure(Call<EstimationDTO> call, Throwable t) {
                    call.cancel();
                    Log.e("RIDE", t.getMessage());
                }
            });
        });
        // Create ride
        getSupportFragmentManager().setFragmentResultListener(PassengerNewRouteSuggestionFragment.EVENT_CREATE_RIDE, this, (requestKey, result) -> {
            CreateRideDTO dto = new CreateRideDTO();
            dto.setLocations(rideDetails.getLocations());
            dto.setPassenger(new UserShortDTO(passenger));
            dto.setVehicleType(rideDetails.getVehicleType());
            dto.setBabyTransport(rideDetails.getBabyTransport());
            dto.setPetTransport(rideDetails.getPetTransport());
            dto.setScheduledTimestamp(scheduledTime);
            Call<CreatedRideDTO> call = ServiceGenerator.rideService.createRide(dto);
            call.enqueue(new Callback<CreatedRideDTO>() {
                @Override
                public void onResponse(Call<CreatedRideDTO> call, Response<CreatedRideDTO> response) {
                    CreatedRideDTO ride = response.body();
                    showRide(ride);
                }
                @Override
                public void onFailure(Call<CreatedRideDTO> call, Throwable t) {
                    call.cancel();
                    newRouteStepper.tryClose();
                    Toast.makeText(getApplicationContext(), "Nema dostupnog vozaca.", Toast.LENGTH_SHORT).show();
                    Log.e("RIDE", t.getMessage());
                }
            });
        });

        SharedPreferences pref = getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        this.id = pref.getInt("userId", 0);
        Websocketer.createWebSocketClient(String.valueOf(id), this::rideUpdated);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = getIntent().getExtras();
                        if (bundle != null && bundle.getString(ARG_DEPARTURE) != null && bundle.getString(ARG_DESTINATION) != null) {
                            newRouteStepper.start();
                            View view = findViewById(R.id.passenger_points_placeholder);
                            TextView departure = view.findViewById(R.id.departure_address_field);
                            TextView destination = view.findViewById(R.id.destination_address_field);
                            if (departure != null) {
                                departure.setText(bundle.getString(ARG_DEPARTURE));
                            }
                            if (destination != null) {
                                destination.setText(bundle.getString(ARG_DESTINATION));
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 2000);
        getSupportFragmentManager().setFragmentResultListener(PassengerNewRouteDriverInfoFragment.EVENT_PANIC_RIDE, this, (requestKey, result) -> {
            Call<CreatedRideDTO> call = ServiceGenerator.rideService.panicRide(ride.getId(), new ReasonDTO("Passenger panic!!!"));
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
    }

    public static boolean isTablet(Context mContext){
        return (mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void rideUpdated(String message) {
        if (message.split(",")[0].equals("ride")) {
            Call<CreatedRideDTO> call = ServiceGenerator.rideService.getRide(Integer.parseInt(message.split(",")[1]));
            call.enqueue(new Callback<CreatedRideDTO>() {
                @Override
                public void onResponse(Call<CreatedRideDTO> call, Response<CreatedRideDTO> response) {
                    CreatedRideDTO createdRide = response.body();
                    newRouteStepper.jumpTo(3);
                    startRide(createdRide);
                }

                @Override
                public void onFailure(Call<CreatedRideDTO> call, Throwable t) {
                    call.cancel();
                    newRouteStepper.tryClose();
                    Log.e("RIDE", t.getMessage());
                }
            });
        } else if (message.split(",")[0].equals("vehicle")) {
            updateVehicleLocation(message);
        }
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

    private void driverArrived(String message) {
        Bundle drawRoute = new Bundle();
        drawRoute.putSerializable(MapFragment.ARG_DEPARTURE, new LocationDTO(Objects.requireNonNull(ride.getLocations().get(0).getDeparture()).getAddress(), ride.getLocations().get(0).getDeparture().getLatitude(), ride.getLocations().get(0).getDeparture().getLongitude()));
        drawRoute.putSerializable(MapFragment.ARG_DESTINATION, new LocationDTO(Objects.requireNonNull(ride.getLocations().get(0).getDestination()).getAddress(), ride.getLocations().get(0).getDestination().getLatitude(), ride.getLocations().get(0).getDestination().getLongitude()));
        getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_DRAW_ROUTE, drawRoute);
        Bundle driverArrived = new Bundle();
        driverArrived.putSerializable(PassengerNewRouteDriverInfoFragment.ARG_RIDE, ride);
        getSupportFragmentManager().setFragmentResult(PassengerNewRouteDriverInfoFragment.EVENT_DRIVER_ARRIVED, driverArrived);

        rideTimerCard.post(new Runnable() {
            @Override
            public void run() {
                rideTimerCard.setVisibility(View.VISIBLE);
            }
        });

        if(rideTimeCounter == null) {
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

    private void startRide(CreatedRideDTO ride) {
        scheduledCard.setVisibility(View.GONE);
        this.ride = ride;
        Call<VehicleDTO> call = ServiceGenerator.rideService.getDriversVehicle(ride.getDriver().getId());
        call.enqueue(new Callback<VehicleDTO>() {
            @Override
            public void onResponse(Call<VehicleDTO> call, Response<VehicleDTO> response) {
                VehicleDTO vehicle = (VehicleDTO) response.body();
                if (ride.getStatus().equals("ACCEPTED")) {
                    Bundle driverComing = new Bundle();
                    driverComing.putSerializable(PassengerNewRouteDriverInfoFragment.ARG_RIDE, ride);
                    driverComing.putSerializable(PassengerNewRouteDriverInfoFragment.ARG_VEHICLE, vehicle);
                    getSupportFragmentManager().setFragmentResult(PassengerNewRouteDriverInfoFragment.EVENT_DRIVER_COMING, driverComing);
                    Bundle drawRoute = new Bundle();
                    drawRoute.putSerializable(MapFragment.ARG_DEPARTURE, new LocationDTO("", Objects.requireNonNull(vehicle).getCurrentLocation().getLatitude(), vehicle.getCurrentLocation().getLongitude()));
                    drawRoute.putSerializable(MapFragment.ARG_DESTINATION, new LocationDTO(Objects.requireNonNull(ride.getLocations().get(0).getDeparture()).getAddress(), ride.getLocations().get(0).getDeparture().getLatitude(), ride.getLocations().get(0).getDeparture().getLongitude()));
                    addressFilled = true;
                    getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_DRAW_ROUTE, drawRoute);
                    newRouteStepper.next();
                } else if (ride.getStatus().equals("ACTIVE")) {
                    driverArrived("");
                } else if (ride.getStatus().equals("FINISHED") || ride.getStatus().equals("REJECTED") || ride.getStatus().equals("PANIC")) {
                    finishedRide(ride.getStatus().equals("FINISHED") ? FinishedRideFragment.EVENT_SET_FINISHED_TEXT : (ride.getStatus().equals("REJECTED") ? FinishedRideFragment.EVENT_SET_REJECT_TEXT : FinishedRideFragment.EVENT_SET_PANIC_TEXT));
                }
            }

            @Override
            public void onFailure(Call<VehicleDTO> call, Throwable t) {
                call.cancel();
                newRouteStepper.tryClose();
                Toast.makeText(getApplicationContext(), "Nema dostupnog vozaca.", Toast.LENGTH_SHORT).show();
                Log.e("RIDE", t.getMessage());
            }
        });
    }

    public void finishedRide(String event) {
        newRouteStepper.tryClose();
        getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_CLEAR_MAP, new Bundle());
        rideTimerCard.setVisibility(View.GONE);
        if(rideTimeCounter != null) rideTimeCounter.cancel();
        rideTimeCounter = null;
        finishedCard = findViewById(R.id.finished_ride_fragment_passenger);
        finishedCard.setVisibility(View.VISIBLE);
        getSupportFragmentManager().setFragmentResult(event, new Bundle());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> finishedCard.setVisibility(View.GONE));
            }
        }, 5000);//put here time 1000 milliseconds=1 second
        if(!event.equals(FinishedRideFragment.EVENT_SET_FINISHED_TEXT)) return;
        PassengerRateDialogFragment dialogDriver = PassengerRateDialogFragment.newInstance("DRIVER", ride.getId(), id);
        dialogDriver.show(getSupportFragmentManager(), "");
        PassengerRateDialogFragment dialogVehicle = PassengerRateDialogFragment.newInstance("VEHICLE", ride.getId(), id);
        dialogVehicle.show(getSupportFragmentManager(), "");
    }

    @SuppressLint("SetTextI18n")
    private void showScheduledRide(CreatedRideDTO ride) {
        ((ImageButton) findViewById(R.id.new_ride_button_passenger)).setEnabled(false);
        ((TextView) findViewById(R.id.scheduled_ride_from)).setText(ride.getLocations().get(0).getDeparture().getAddress());
        ((TextView) findViewById(R.id.scheduled_ride_to)).setText(ride.getLocations().get(0).getDestination().getAddress());
        Calendar scheduledTime = Calendar.getInstance();
        scheduledTime.setTimeInMillis(ride.getScheduledTimestamp());
        ((TextView) findViewById(R.id.scheduled_ride_time)).setText(scheduledTime.get(Calendar.HOUR_OF_DAY) + ":" + scheduledTime.get(Calendar.MINUTE));
        scheduledCard.setVisibility(View.VISIBLE);
    }

    private void initServices() {
        SharedPreferences pref = getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        ServiceGenerator.initPassenger(pref.getString("authToken", ""));
        Call<UserDTO> call = ServiceGenerator.passengerService.getById(pref.getInt("userId", 0));
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                passenger = response.body();
                tryResumeRide();
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void showAccountDetails(View view) {
        startActivity(new Intent(PassengerMainActivity.this, PassengerAccountActivity.class));
    }

    public void showInbox(View view) {
        Intent showInbox = new Intent(PassengerMainActivity.this, InboxActivity.class);
        showInbox.putExtra(InboxActivity.ARG_USER, passenger);
        startActivity(showInbox);
    }

    public void showRideHistory(View view) {
        startActivity(new Intent(PassengerMainActivity.this, PassengerRideHistoryActivity.class));
    }

    public void logout(View view) {
        SharedPreferences pref = getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        pref.edit().clear().apply();
        startActivity(new Intent(PassengerMainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if(!newRouteStepper.isFinished() && newRouteStepper.tryClose()) return;
        super.onBackPressed();
    }

    public void previousStep(View view) {
        newRouteStepper.previous();
    }

    public void onNewRideClick(View view) {
        newRouteStepper.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(passenger != null) tryResumeRide();
    }

    private void tryResumeRide() {
        Call<CreatedRideDTO> call = ServiceGenerator.rideService.getPassengerActiveRide(passenger.getId());
        call.enqueue(new Callback<CreatedRideDTO>() {
            @Override
            public void onResponse(Call<CreatedRideDTO> call, Response<CreatedRideDTO> response) {
                CreatedRideDTO ride = response.body();
                showRide(ride);
            }
            @Override
            public void onFailure(Call<CreatedRideDTO> call, Throwable t) {
                call.cancel();
                newRouteStepper.tryClose();
            }
        });
    }
    private void showRide(CreatedRideDTO ride) {
        if (ride == null) {
            newRouteStepper.tryClose();
            return;
        }
        Log.d("RIDE", ride.getStatus());
        if (ride.getStatus().equals("PENDING")) {
            showScheduledRide(ride);
            newRouteStepper.tryClose();
            getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_CLEAR_MAP, new Bundle());
        } else {
            newRouteStepper.jumpTo(3);
            startRide(ride);
        }
    }
}