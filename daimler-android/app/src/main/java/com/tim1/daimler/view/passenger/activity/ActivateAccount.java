package com.tim1.daimler.view.passenger.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.tim1.daimler.R;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.makeFullScreen(this);

        Intent appLinkIntent = getIntent();
        String token = appLinkIntent.getData().toString().split("token=")[1];
        ServiceGenerator.initLoginService("");
        Call<String> call = ServiceGenerator.loginService.activate(token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {}
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                call.cancel();
            }
        });
    }
}