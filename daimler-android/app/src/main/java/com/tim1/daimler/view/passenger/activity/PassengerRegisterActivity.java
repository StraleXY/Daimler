package com.tim1.daimler.view.passenger.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.user.RegisterDTO;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_register);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.makeFullScreen(this);
    }

    public void createAccount(View view) {
        if (Validate(findViewById(R.id.form))) {
            RegisterDTO newUser = new RegisterDTO();
            newUser.setName(((TextInputLayout) findViewById(R.id.nameLayout)).getEditText().getText().toString());
            newUser.setSurname(((TextInputLayout) findViewById(R.id.surnameLayout)).getEditText().getText().toString());
            newUser.setProfilePicture("");
            newUser.setTelephoneNumber(((TextInputLayout) findViewById(R.id.phoneNumberLayout)).getEditText().getText().toString());
            newUser.setEmail(((TextInputLayout) findViewById(R.id.emailLayout)).getEditText().getText().toString());
            newUser.setAddress(((TextInputLayout) findViewById(R.id.addressLayout)).getEditText().getText().toString());
            newUser.setPassword(((TextInputLayout) findViewById(R.id.passwordLayout)).getEditText().getText().toString());
            Call<UserDTO> call = ServiceGenerator.loginService.register(newUser);
            call.enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    Toast.makeText(getApplicationContext(), "Check your email to verify account!", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    call.cancel();
                    Toast.makeText(getApplicationContext(), "There was an error with the request", Toast.LENGTH_LONG).show();
                }
            });
            finish();
        }
    }

    private boolean Validate(LinearLayout inputForm) {
        if(getCurrentFocus() != null) getCurrentFocus().clearFocus();
        boolean isFilled = true;
        for (int i = 0; i < inputForm.getChildCount(); i++) {
            try {
                TextInputLayout input = (TextInputLayout)inputForm.getChildAt(i);
                if (Objects.requireNonNull(input.getEditText()).getText().toString().isEmpty()) {
                    input.setError(getString(R.string.error_field_required));
                    isFilled = false;
                } else {
                    input.setError(null);
                    input.setErrorEnabled(false);
                }
            } catch (Exception e) {
                // Unsupported widget
            }
        }
        if (!isFilled) return isFilled;
        boolean isPasswordCorrect =
                ((TextInputLayout)findViewById(R.id.passwordLayout)).getEditText().getText().toString()
                        .equals(((TextInputLayout)findViewById(R.id.repeatPasswordLayout)).getEditText().getText().toString());
        if (isPasswordCorrect) ((TextInputLayout)findViewById(R.id.repeatPasswordLayout)).setErrorEnabled(false);
        else ((TextInputLayout)findViewById(R.id.repeatPasswordLayout)).setError(getString(R.string.error_password_mismatch));
        return isPasswordCorrect;
    }
}
