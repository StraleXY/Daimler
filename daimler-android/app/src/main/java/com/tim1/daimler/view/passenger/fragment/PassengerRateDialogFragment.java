package com.tim1.daimler.view.passenger.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.review.RatingDTO;
import com.tim1.daimler.dtos.review.ReviewDTO;
import com.tim1.daimler.util.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRateDialogFragment extends DialogFragment {

    public static String ARG_TYPE = "arg_type";
    public static String ARG_ID = "arg_id";
    public static String ARG_PASSENGER_ID = "arg_passenger_id";

    private int rating;
    private int passengerId;
    private String type;
    private int id;
    private View view;

    public PassengerRateDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        rating = 0;
    }

    public static PassengerRateDialogFragment newInstance(String type, int id, int passengerId) {
        PassengerRateDialogFragment dialog = new PassengerRateDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TYPE, type);
        bundle.putInt(ARG_ID, id);
        bundle.putInt(ARG_PASSENGER_ID, passengerId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_rate_dialog, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        type = getArguments().getString(ARG_TYPE);
        id = getArguments().getInt(ARG_ID);
        passengerId = getArguments().getInt(ARG_PASSENGER_ID);
        TextView title = (TextView) view.findViewById(R.id.rateDialogTitle);
        title.setText(type.equals("DRIVER") ? "RATE DRIVER" : "RATE VEHICLE");
        MaterialButton rateButton = (MaterialButton) view.findViewById(R.id.rateDialogButton);
        rateButton.setOnClickListener((v)->rate());
        LinearLayout starsLayout = (LinearLayout) view.findViewById(R.id.rateStarsLayout);
        for (int i = 1; i <= 5; ++i) {
            ImageView star = (ImageView) starsLayout.getChildAt(i);
            final int rating = i;
            star.setOnClickListener((v)->chooseRating(rating));
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private void chooseRating(int rating) {
        this.rating = rating;
        LinearLayout starsLayout = (LinearLayout) view.findViewById(R.id.rateStarsLayout);
        for (int i = 1; i <= 5; ++i) {
            ImageView star = (ImageView) starsLayout.getChildAt(i);
            if (i <= rating) {
                star.setImageResource(R.drawable.ic_baseline_star_rate_24);
            } else {
                star.setImageResource(R.drawable.ic_baseline_star_outline_24);
            }
        }
        MaterialButton rateButton = (MaterialButton) view.findViewById(R.id.rateDialogButton);
        rateButton.setEnabled(true);
    }

    private void rate() {
        if (rating < 1 || rating > 5) {
            return;
        }

        RatingDTO ratingDto = new RatingDTO();
        ratingDto.setRating(rating);
        ratingDto.setPassengerId(passengerId);

        TextInputLayout commentLayout = (TextInputLayout) view.findViewById(R.id.rateCommentLayout);
        String comment = commentLayout.getEditText().getText().toString();
        ratingDto.setComment(comment);

        System.out.println("Ride id " + id + " Passenger id " + passengerId + " Rating " + rating + " Comment " + comment);

        if (type.equals("DRIVER")) {
            Call<ReviewDTO> call = ServiceGenerator.reviewService.reviewRideDriver(id, ratingDto);
            call.enqueue(new Callback<ReviewDTO>() {
                @Override
                public void onResponse(Call<ReviewDTO> call, Response<ReviewDTO> response) {
                    System.out.println("Succeeded!");
                    dismiss();
                }
                @Override
                public void onFailure(Call<ReviewDTO> call, Throwable t) {
                    System.out.println("Failed!");
                }
            });
        } else {
            Call<ReviewDTO> call = ServiceGenerator.reviewService.reviewRideVehicle(id, ratingDto);
            call.enqueue(new Callback<ReviewDTO>() {
                @Override
                public void onResponse(Call<ReviewDTO> call, Response<ReviewDTO> response) {
                    dismiss();
                }
                @Override
                public void onFailure(Call<ReviewDTO> call, Throwable t) {
                }
            });
        }
    }
}
