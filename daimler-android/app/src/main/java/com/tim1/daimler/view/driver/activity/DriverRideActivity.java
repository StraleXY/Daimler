package com.tim1.daimler.view.driver.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.review.CombinedReviewsDTO;
import com.tim1.daimler.dtos.review.ReviewDTO;
import com.tim1.daimler.dtos.ride.CreatedRideDTO;
import com.tim1.daimler.dtos.user.UserInRideDTO;
import com.tim1.daimler.model.User;
import com.tim1.daimler.util.Dater;
import com.tim1.daimler.util.FragmentTransition;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.util.data.InboxItem;
import com.tim1.daimler.view.common.activity.ChatActivity;
import com.tim1.daimler.view.passenger.activity.PassengerRideActivity;
import com.tim1.daimler.view.passenger.fragment.PassengerListFragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DriverRideActivity extends AppCompatActivity {

    public static String ARG_RIDE = "arg_ride";
    private CreatedRideDTO ride;
    private ReviewDTO driverReview;
    private ReviewDTO vehicleReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.makeFullScreen(this);

        ride = (CreatedRideDTO) getIntent().getSerializableExtra(ARG_RIDE);
        TextView rideStatus = (TextView) findViewById(R.id.rideDetailsStatus);
        TextView rideFrom = (TextView) findViewById(R.id.rideDetailsFrom);
        TextView rideTo = (TextView) findViewById(R.id.rideDetailsTo);
        TextView rideDate = (TextView) findViewById(R.id.rideDetailsDate);
        TextView rideStartTime = (TextView) findViewById(R.id.rideDetailsStartTime);
        TextView rideFinishTime = (TextView) findViewById(R.id.rideDetailsFinishTime);
        TextView rideEstimatedTime = (TextView) findViewById(R.id.rideDetailsEstimatedTime);
        TextView ridePrice = (TextView) findViewById(R.id.rideDetailsPrice);
        TextView rideLength = (TextView) findViewById(R.id.rideDetailsLength);
        TextView rideBabies = (TextView) findViewById(R.id.rideDetailsBabies);
        TextView ridePets = (TextView) findViewById(R.id.rideDetailsPets);
        TextView rideSplitFare = (TextView) findViewById(R.id.rideDetailsSplitFare);
        TextView passengerCount = (TextView) findViewById(R.id.ridePassengerCount);

        rideStatus.setText(ride.getStatus());
        rideFrom.setText(ride.getLocations().get(0).getDeparture().getAddress());
        rideTo.setText(ride.getLocations().get(0).getDestination().getAddress());
        rideDate.setText(Dater.toDate(ride.getScheduledTimestamp()));
//        rideStartTime.setText(ride.getStartTime().substring(11, 16));
//        rideFinishTime.setText(ride.getEndTime().substring(11, 16));
        rideEstimatedTime.setText(ride.getEstimatedTimeInMinutes() + " min");
        ridePrice.setText(ride.getTotalCost() + " RSD");
        rideLength.setText(ride.getDistance() + " Km");
        rideBabies.setText(ride.getBabyTransport() ? "Yes" : "No");
        ridePets.setText(ride.getPetTransport() ? "Yes" : "No");
        rideSplitFare.setText(ride.getPassengers().size() > 1 ? "Yes" : "No");
        passengerCount.setText(String.valueOf(ride.getPassengers().size()));
        getReviews();

        FragmentTransition.to(PassengerListFragment.newInstance(), this, false, R.id.passengerListCard);

        MaterialButton showMessagesButton = (MaterialButton) findViewById(R.id.showMessagesButton);

        showMessagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessages(view);
            }
        });

        showPassengers();
    }

    public void showMessages(View view) {
        Intent transition = new Intent(DriverRideActivity.this, ChatActivity.class);

        UserInRideDTO passenger = ride.getPassengers().get(0);
        transition.putExtra(ChatActivity.ARG_USER_TO, passenger);
        transition.putExtra(ChatActivity.ARG_USER_FROM, ride.getDriver());
        transition.putExtra(ChatActivity.ARG_RIDE_ID, ride.getId());
        transition.putExtra(ChatActivity.ARG_TYPE, "RIDE");
        transition.putExtra(ChatActivity.ARG_ADDRESS_DESTINATION, ride.getLocations().get(0).getDestination().getAddress());

        startActivity(transition);
    }

    private void getReviews() {
        driverReview = null;
        vehicleReview = null;
        Call<List<CombinedReviewsDTO>> call = ServiceGenerator.reviewService.getRideReviews(ride.getId());
        call.enqueue(new Callback<List<CombinedReviewsDTO>>() {
            @Override
            public void onResponse(Call<List<CombinedReviewsDTO>> call, Response<List<CombinedReviewsDTO>> response) {
                if (response.body() != null) {
                    for (CombinedReviewsDTO reviews: response.body()) {
                        if (reviews.getDriverReview() != null) {
                            driverReview = reviews.getDriverReview();
                        }
                        if (reviews.getVehicleReview() != null) {
                            vehicleReview = reviews.getVehicleReview();
                        }
                    }
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
        ArrayList<UserInRideDTO> passengers = new ArrayList<>(ride.getPassengers());
        bundle.putSerializable(PassengerListFragment.ARG_PASSENGER_LIST, passengers);
        getSupportFragmentManager().setFragmentResult(PassengerListFragment.EVENT_SHOW_PASSENGERS, bundle);
    }

    public void backClicked(View view) {
        finish();
    }
}