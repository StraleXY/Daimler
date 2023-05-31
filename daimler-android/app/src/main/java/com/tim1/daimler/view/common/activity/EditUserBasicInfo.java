package com.tim1.daimler.view.common.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.user.UpdateUserDTO;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.util.Bitmaper;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Servicer;
import com.tim1.daimler.util.Styler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class EditUserBasicInfo extends AppCompatActivity {

    public static final String ARG_USER_ID = "arg_user_id";
    public static final int PICK_IMAGE = 1;
    protected TextView txtName, txtSurname, txtEmail, txtPhone, txtAddress, txtPassword, txtRepeatPassword;
    protected MaterialButton positiveButton;
    protected ImageView imgProfile;
    protected UserDTO user;
    protected UpdateUserDTO updated = new UpdateUserDTO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_basic_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.makeFullScreen(this);

        txtName = ((TextInputLayout) findViewById(R.id.basicInfoNameLayout)).getEditText();
        txtSurname = ((TextInputLayout) findViewById(R.id.basicInfoSurnameLayout)).getEditText();
        txtEmail = ((TextInputLayout) findViewById(R.id.basicInfoEmailLayout)).getEditText();
        txtPhone = ((TextInputLayout) findViewById(R.id.basicInfoPhoneNumberLayout)).getEditText();
        txtAddress = ((TextInputLayout) findViewById(R.id.basicInfoAddressLayout)).getEditText();
        txtPassword = ((TextInputLayout) findViewById(R.id.basicInfoPasswordLayout)).getEditText();
        txtRepeatPassword = ((TextInputLayout) findViewById(R.id.basicInfoRepeatPasswordLayout)).getEditText();
        imgProfile = findViewById(R.id.basicInfoProfilePicture);
        positiveButton = findViewById(R.id.updateBasicInfoButton);

        ((CheckBox) findViewById(R.id.basicInfoPasswordCheckbox)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                txtPassword.setText("");
                txtRepeatPassword.setText("");
            }
            findViewById(R.id.basicInfoPasswordLayout).setEnabled(isChecked);
            findViewById(R.id.basicInfoRepeatPasswordLayout).setEnabled(isChecked);
        });

        (findViewById(R.id.basicInfoPickImageButton)).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, PICK_IMAGE);
        });

        positiveButton.setOnClickListener(v -> {
            updated.setName(txtName.getText().toString());
            updated.setSurname(txtSurname.getText().toString());
            updated.setEmail(txtEmail.getText().toString());
            updated.setTelephoneNumber(txtPhone.getText().toString());
            updated.setAddress(txtAddress.getText().toString());
            updated.setPassword(txtPassword.getText().toString());
            if(updated.getProfilePicture().isEmpty()) updated.setProfilePicture(user.getProfilePicture());
            save(user.getId(), updated);
        });

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(resultCode, resultCode, data);
        if (requestCode != PICK_IMAGE || resultCode != Activity.RESULT_OK) return;

        final Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap newProfilePic = extras.getParcelable("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            newProfilePic.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            String encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            updated.setProfilePicture(encoded);
            imgProfile.setImageBitmap(newProfilePic);
        }
    }

    protected abstract void init();
    protected abstract void save(int id, UpdateUserDTO updated);
}