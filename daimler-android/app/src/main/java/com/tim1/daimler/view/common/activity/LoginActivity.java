package com.tim1.daimler.view.common.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.user.LoginDTO;
import com.tim1.daimler.dtos.user.TokenDTO;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.util.ERole;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.view.driver.activity.DriverMainActivity;
import com.tim1.daimler.view.passenger.activity.PassengerMainActivity;
import com.tim1.daimler.view.passenger.activity.PassengerRegisterActivity;

import org.json.JSONException;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Styler.applySplash(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.hideStatusBar(this, findViewById(R.id.loginRoot));
        ServiceGenerator.initLoginService("");
    }

    public void parseLoginData(View view) {
        if(getCurrentFocus() != null) getCurrentFocus().clearFocus();
        LinearLayout loginForm = findViewById(R.id.loginForm);
        TextInputLayout usernameLayout = (TextInputLayout)loginForm.getChildAt(1);
        TextInputLayout passwordLayout = (TextInputLayout)loginForm.getChildAt(2);
        if (Objects.requireNonNull(usernameLayout.getEditText()).getText().toString().isEmpty()) {
            usernameLayout.setError("Username cannot be empty");
            return;
        }
        if (Objects.requireNonNull(passwordLayout.getEditText()).getText().toString().isEmpty()) {
            passwordLayout.setError("Password cannot be empty");
            return;
        }
        String username = usernameLayout.getEditText().getText().toString();
        String password = passwordLayout.getEditText().getText().toString();
        login(view, username, password);
    }

    public void login(View view, String username, String password) {
        LoginDTO dto = new LoginDTO(username, password);
        Call<TokenDTO> call = ServiceGenerator.loginService.doLogin(dto);
        call.enqueue(new Callback<TokenDTO>() {
            @Override
            public void onResponse(Call<TokenDTO> call, Response<TokenDTO> response) {
                if(response.body() == null) {
                    Toast.makeText(getApplicationContext(), "User doesn't exist or email verification not confirmed.", Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences prefs;
                SharedPreferences.Editor edit;
                prefs=getBaseContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                edit=prefs.edit();
                edit.putString("authToken", response.body().getAccessToken());
                edit.putString("refreshToken", response.body().getRefreshToken());
                edit.putInt("userId", response.body().getUserId());
                edit.putString("userRole", response.body().getUserRole().toString());
                edit.apply();
                if (response.body().getUserRole() == ERole.ROLE_PASSENGER) {
                    startActivity(new Intent(LoginActivity.this, PassengerMainActivity.class));
                    finish();
                } else if (response.body().getUserRole() == ERole.ROLE_DRIVER) {
                    startActivity(new Intent(LoginActivity.this, DriverMainActivity.class));
                    finish();
                }
                else {
                     clearInputFields();
                     Toast.makeText(view.getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                 }
            }

            @Override
            public void onFailure(Call<TokenDTO> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "There was a problem with the request. Please try again!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void clearInputFields() {
        //TODO: get layouts
        //usernameLayout.getEditText().setText("");
        //passwordLayout.getEditText().setText("");
    }

    public void forgotPassword(View view) {
        //TODO: forgot password implementation
    }

    public void createAccount(View view) {
        startActivity(new Intent(LoginActivity.this, PassengerRegisterActivity.class));
    }
}