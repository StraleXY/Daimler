package com.tim1.daimler.view.passenger.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.passenger.FavoriteRouteDTO;
import com.tim1.daimler.dtos.review.CombinedReviewsDTO;
import com.tim1.daimler.dtos.review.ReviewDTO;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.dtos.ride.LocationDTO;
import com.tim1.daimler.dtos.user.UserInRideDTO;
import com.tim1.daimler.model.Message;
import com.tim1.daimler.model.User;
import com.tim1.daimler.util.Addresser;
import com.tim1.daimler.util.Bitmaper;
import com.tim1.daimler.util.Dater;
import com.tim1.daimler.util.FragmentTransition;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.util.data.InboxItem;
import com.tim1.daimler.view.common.activity.ChatActivity;
import com.tim1.daimler.view.common.fragment.MapFragment;
import com.tim1.daimler.view.passenger.fragment.PassengerListFragment;
import com.tim1.daimler.view.passenger.fragment.PassengerNewRoutePointsFragment;
import com.tim1.daimler.view.passenger.fragment.PassengerRateDialogFragment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideActivity extends AppCompatActivity {

    public static String ARG_RIDE = "arg_ride";

    private TextView rideStatus;
    private TextView rideFrom;
    private TextView rideTo;
    private TextView rideDate;
    private TextView rideStartTime;
    private TextView rideFinishTime;
    private TextView rideEstimatedTime;
    private TextView ridePrice;
    private TextView rideLength;
    private TextView rideBabies;
    private TextView ridePets;
    private TextView rideSplitFare;

    private TextView driverName;
    private TextView driverEmail;

    private MaterialButton scheduleARideButton;
    private MaterialButton addToFavouritesButton;
    private MaterialButton showMessagesButton;
    private MaterialButton rateDriverButton;
    private MaterialButton rateVehicleButton;

    private ImageView driverProfileImage;

    private int id;
    private CreatedRideDTO ride;
    private boolean isFavorite;
    private boolean expired;
    private ReviewDTO driverReview;
    private ReviewDTO vehicleReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_ride);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.makeFullScreen(this);

        SharedPreferences pref = getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        id = pref.getInt("userId", 0);
        ride = (CreatedRideDTO) getIntent().getSerializableExtra(ARG_RIDE);

        FragmentTransition.to(MapFragment.newInstance(), this, false, R.id.passenger_ride_map_fragment_container);
        drawRoute();

        rideStatus = (TextView) findViewById(R.id.rideDetailsStatus);
        rideFrom = (TextView) findViewById(R.id.rideDetailsFrom);
        rideTo = (TextView) findViewById(R.id.rideDetailsTo);
        rideDate = (TextView) findViewById(R.id.rideDetailsDate);
        rideStartTime = (TextView) findViewById(R.id.rideDetailsStartTime);
        rideFinishTime = (TextView) findViewById(R.id.rideDetailsFinishTime);
        rideEstimatedTime = (TextView) findViewById(R.id.rideDetailsEstimatedTime);
        ridePrice = (TextView) findViewById(R.id.rideDetailsPrice);
        rideLength = (TextView) findViewById(R.id.rideDetailsLength);
        rideBabies = (TextView) findViewById(R.id.rideDetailsBabies);
        ridePets = (TextView) findViewById(R.id.rideDetailsPets);
        rideSplitFare = (TextView) findViewById(R.id.rideDetailsSplitFare);

        rideStatus.setText(ride.getStatus());
        rideFrom.setText(ride.getLocations().get(0).getDeparture().getAddress());
        rideTo.setText(ride.getLocations().get(0).getDestination().getAddress());
        rideDate.setText(Dater.toDate(ride.getScheduledTimestamp()));
        //rideStartTime.setText(ride.getStartTime().substring(11, 16));
        //rideFinishTime.setText(ride.getEndTime().substring(11, 16));
        rideEstimatedTime.setText(ride.getEstimatedTimeInMinutes() + " min");
        ridePrice.setText(ride.getTotalCost() + " RSD");
        rideLength.setText(ride.getDistance() + " Km");
        rideBabies.setText(ride.getBabyTransport() ? "Yes" : "No");
        ridePets.setText(ride.getPetTransport() ? "Yes" : "No");
        rideSplitFare.setText(ride.getPassengers().size() > 1 ? "Yes" : "No");

        driverName = (TextView) findViewById(R.id.rideDetailsDriverName);
        driverEmail = (TextView) findViewById(R.id.rideDetailsDriverEmail);
        driverProfileImage = (ImageView) findViewById(R.id.driverProfileImage);

        driverName.setText(ride.getDriver().getName() + " " + ride.getDriver().getSurname());
        driverEmail.setText(ride.getDriver().getEmail());
        driverProfileImage.setImageBitmap(Bitmaper.toBitmap(ride.getDriver().getProfilePicture()));

        scheduleARideButton = (MaterialButton) findViewById(R.id.scheduleARideButton);
        addToFavouritesButton = (MaterialButton) findViewById(R.id.addToFavouritesButton);
        showMessagesButton = (MaterialButton) findViewById(R.id.showMessagesButton);
        rateDriverButton = (MaterialButton) findViewById(R.id.rateDriverButton);
        rateVehicleButton = (MaterialButton) findViewById(R.id.rateVehicleButton);

        FragmentTransition.to(PassengerListFragment.newInstance(), this, false, R.id.passengerListCard);
        showPassengers();

        addToFavouritesButton.setVisibility(View.GONE);

        Call<List<FavoriteRouteDTO>> call = ServiceGenerator.passengerService.getFavoriteRoutes(id);
        call.enqueue(new Callback<List<FavoriteRouteDTO>>() {
            @Override
            public void onResponse(Call<List<FavoriteRouteDTO>> call, Response<List<FavoriteRouteDTO>> response) {
                isFavorite = false;
                if (response.body() != null) {
                    for (FavoriteRouteDTO favoriteRoute : response.body()) {
                        if (favoriteRoute.getDeparture().getAddress().equals(ride.getLocations().get(0).getDeparture().getAddress()) &&
                            favoriteRoute.getDestination().getAddress().equals(ride.getLocations().get(0).getDestination().getAddress())) {
                            isFavorite = true;
                            findViewById(R.id.isFavouriteStar).setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (!isFavorite) {
                    addToFavouritesButton.post(new Runnable() {
                        @Override
                        public void run() {
                            addToFavouritesButton.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteRouteDTO>> call, Throwable t) {

            }
        });

        scheduleARideButton.setOnClickListener((view)->scheduleARide());
        addToFavouritesButton.setOnClickListener((view)->addToFavourites());
        showMessagesButton.setOnClickListener((view)->showMessages());
        rateDriverButton.setOnClickListener((view)->rateDriver());
        rateVehicleButton.setOnClickListener((view)->rateVehicle());

        getReviews();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getReviews();
                    }
                });
            }
        }, 0, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawRoute();
        getReviews();
    }

    private void getReviews() {
        driverReview = null;
        vehicleReview = null;
        expired = false;
        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(ride.getScheduledTimestamp());
        startTime.add(Calendar.DAY_OF_MONTH, 3);
        if (startTime.getTimeInMillis() < System.currentTimeMillis()) {
            expired = true;
        }
        Call<List<CombinedReviewsDTO>> call = ServiceGenerator.reviewService.getRideReviews(ride.getId());
        call.enqueue(new Callback<List<CombinedReviewsDTO>>() {
            @Override
            public void onResponse(Call<List<CombinedReviewsDTO>> call, Response<List<CombinedReviewsDTO>> response) {
                if (response.body() != null) {
                    System.out.println(response.body().size());
                    for (CombinedReviewsDTO reviews: response.body()) {
                        if (reviews.getDriverReview() != null && reviews.getDriverReview().getPassenger().getId().equals(id)) {
                            rateDriverButton.setVisibility(View.GONE);
                            driverReview = reviews.getDriverReview();
                        }
                        if (reviews.getVehicleReview() != null && reviews.getVehicleReview().getPassenger().getId().equals(id)) {
                            rateVehicleButton.setVisibility(View.GONE);
                            vehicleReview = reviews.getVehicleReview();
                        }
                    }
                }
                if ((driverReview != null && vehicleReview != null) || expired) {
                    findViewById(R.id.rateButtonsDivider).setVisibility(View.GONE);
                }
                if (expired) {
                    rateDriverButton.setVisibility(View.GONE);
                    rateVehicleButton.setVisibility(View.GONE);
                }
                if (driverReview != null) {
                    findViewById(R.id.driverReviewLayout).setVisibility(View.VISIBLE);
                    LinearLayout starsLayout = findViewById(R.id.driverRateStarsLayout);
                    for (int i = 0; i < 5; ++i) {
                        ImageView star = (ImageView) starsLayout.getChildAt(i);
                        if (i < driverReview.getRating()) {
                            star.setImageResource(R.drawable.ic_baseline_star_rate_24);
                        } else {
                            star.setImageResource(R.drawable.ic_baseline_star_outline_24);
                        }
                    }
                    TextView comment = (TextView) findViewById(R.id.driverReviewComment);
                    comment.setText(driverReview.getComment());
                } else {
                    findViewById(R.id.driverReviewLayout).setVisibility(View.GONE);
                }
                if (vehicleReview != null) {
                    findViewById(R.id.vehicleReviewLayout).setVisibility(View.VISIBLE);
                    LinearLayout starsLayout = findViewById(R.id.vehicleRateStarsLayout);
                    for (int i = 0; i < 5; ++i) {
                        ImageView star = (ImageView) starsLayout.getChildAt(i);
                        if (i < vehicleReview.getRating()) {
                            star.setImageResource(R.drawable.ic_baseline_star_rate_24);
                        } else {
                            star.setImageResource(R.drawable.ic_baseline_star_outline_24);
                        }
                    }
                    TextView comment = (TextView) findViewById(R.id.vehicleReviewComment);
                    comment.setText(vehicleReview.getComment());
                } else {
                    findViewById(R.id.vehicleReviewLayout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<CombinedReviewsDTO>> call, Throwable t) {

            }
        });
    }

    private void showPassengers() {
        Bundle bundle = new Bundle();
        ArrayList<UserInRideDTO> passengers = new ArrayList<>(ride.getPassengers().stream().filter((passenger)->!passenger.getId().equals(id)).collect(Collectors.toList()));
        if(passengers.size() == 0) findViewById(R.id.passengerListCard).setVisibility(View.GONE);
        else {
            bundle.putSerializable(PassengerListFragment.ARG_PASSENGER_LIST, passengers);
            getSupportFragmentManager().setFragmentResult(PassengerListFragment.EVENT_SHOW_PASSENGERS, bundle);
        }
    }

    private void drawRoute() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle drawRoute = new Bundle();
                        drawRoute.putSerializable(MapFragment.ARG_DEPARTURE, ride.getLocations().get(0).getDeparture());
                        drawRoute.putSerializable(MapFragment.ARG_DESTINATION, ride.getLocations().get(0).getDestination());
                        drawRoute.putString(MapFragment.ARG_FIXED, "fixed");
                        getSupportFragmentManager().setFragmentResult(MapFragment.EVENT_DRAW_ROUTE_AND_REFRESH, drawRoute);
                    }
                });
            }
        }, 200);
    }

    public void scheduleARide() {
        Intent intent = new Intent(PassengerRideActivity.this, PassengerMainActivity.class);
        intent.putExtra(PassengerMainActivity.ARG_DEPARTURE, ride.getLocations().get(0).getDeparture().getAddress());
        intent.putExtra(PassengerMainActivity.ARG_DESTINATION, ride.getLocations().get(0).getDestination().getAddress());
        startActivity(intent);
    }

    public void addToFavourites() {
        addToFavouritesButton.setVisibility(View.GONE);
        isFavorite = true;
        Call<FavoriteRouteDTO> call = ServiceGenerator.passengerService.addRouteToFavorite(id, ride.getLocations().get(0).getDeparture().getId(), ride.getLocations().get(0).getDestination().getId());
        call.enqueue(new Callback<FavoriteRouteDTO>() {
            @Override
            public void onResponse(Call<FavoriteRouteDTO> call, Response<FavoriteRouteDTO> response) {
                Toast.makeText(getApplicationContext(), "Route added to favorites!", Toast.LENGTH_SHORT);
                findViewById(R.id.isFavouriteStar).setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<FavoriteRouteDTO> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to add route to favorites!", Toast.LENGTH_SHORT);
                isFavorite = false;
                addToFavouritesButton.post(new Runnable() {
                    @Override
                    public void run() {
                        addToFavouritesButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    public void showMessages() {
        Intent transition = new Intent(PassengerRideActivity.this, ChatActivity.class);

        UserInRideDTO passenger = null;
        for (UserInRideDTO p : ride.getPassengers()) {
            if (p.getId().equals(id)) {
                passenger = p;
            }
        }

        transition.putExtra(ChatActivity.ARG_USER_FROM, passenger);
        transition.putExtra(ChatActivity.ARG_USER_TO, ride.getDriver());
        transition.putExtra(ChatActivity.ARG_RIDE_ID, ride.getId());
        transition.putExtra(ChatActivity.ARG_TYPE, "RIDE");
        transition.putExtra(ChatActivity.ARG_ADDRESS_DESTINATION, ride.getLocations().get(0).getDestination().getAddress());

        startActivity(transition);
    }

    public void rateDriver() {
        PassengerRateDialogFragment dialog = PassengerRateDialogFragment.newInstance("DRIVER", ride.getId(), id);
        dialog.show(getSupportFragmentManager(), "");
    }

    public void rateVehicle() {
        PassengerRateDialogFragment dialog = PassengerRateDialogFragment.newInstance("VEHICLE", ride.getId(), id);
        dialog.show(getSupportFragmentManager(), "");
    }

    public void backClicked(View view) {
        finish();
    }
}