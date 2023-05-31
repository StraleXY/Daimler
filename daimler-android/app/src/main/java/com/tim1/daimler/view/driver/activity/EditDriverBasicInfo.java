package com.tim1.daimler.view.driver.activity;

import android.app.Activity;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.user.UpdateUserDTO;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.util.Bitmaper;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Servicer;
import com.tim1.daimler.view.common.activity.EditUserBasicInfo;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDriverBasicInfo extends EditUserBasicInfo {

    @Override
    protected void init() {
        positiveButton.setText(R.string.button_send_request);

        int userId = getIntent().getIntExtra(ARG_USER_ID, 0);
        Call<UserDTO> call = ServiceGenerator.driverService.getById(userId);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                user = response.body();
                assert user != null;
                txtName.setText(user.getName());
                txtSurname.setText(user.getSurname());
                txtEmail.setText(user.getEmail());
                txtPhone.setText(user.getTelephoneNumber());
                txtAddress.setText(user.getAddress());
                imgProfile.setImageBitmap(Bitmaper.toBitmap(user.getProfilePicture()));
            }
            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    protected void save(int id, UpdateUserDTO updated) {
        Call<UserDTO> call = ServiceGenerator.driverService.update(id, updated);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                Toast.makeText(getApplicationContext(), "Request sent to admin!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "There was a problem with the request. Please try again!", Toast.LENGTH_LONG).show();
            }
        });
        finish();
    }
}
