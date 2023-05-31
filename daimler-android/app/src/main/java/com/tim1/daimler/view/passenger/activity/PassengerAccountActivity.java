package com.tim1.daimler.view.passenger.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.util.FragmentTransition;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.view.common.fragment.UserBasicInfoFragment;
import com.tim1.daimler.view.common.fragment.UserReportsFragment;
import com.tim1.daimler.view.passenger.fragment.PassengerFavouriteRoutesFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_account);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.makeFullScreen(this);
        FragmentTransition.to(UserBasicInfoFragment.newInstance(), this, false, R.id.basicInfoCard);
        FragmentTransition.to(PassengerFavouriteRoutesFragment.newInstance(), this, false, R.id.favouriteRoutesInfoCard);
        FragmentTransition.to(UserReportsFragment.newInstance(), this, false, R.id.reportsInfoCard);
        fillUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillUserInfo();
    }

    private void fillUserInfo() {
        SharedPreferences pref = getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        Call<UserDTO> call = ServiceGenerator.passengerService.getById(pref.getInt("userId", 0));
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                Bundle request = new Bundle();
                request.putSerializable(UserBasicInfoFragment.ARG_USER_BASIC_INFO, response.body());
                request.putString(UserBasicInfoFragment.ARG_USER_ROLE, "PASSENGER");
                getSupportFragmentManager().setFragmentResult(UserBasicInfoFragment.FRAGMENT_ACTION_SHOW_USER_BASIC_INFO, request);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void backClicked(View view) {
        finish();
    }
}