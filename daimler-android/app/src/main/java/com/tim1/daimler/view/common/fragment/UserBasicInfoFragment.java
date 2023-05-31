package com.tim1.daimler.view.common.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.util.Bitmaper;
import com.tim1.daimler.view.common.activity.EditUserBasicInfo;
import com.tim1.daimler.view.driver.activity.EditDriverBasicInfo;
import com.tim1.daimler.view.passenger.activity.EditPassengerBasicInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserBasicInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBasicInfoFragment extends Fragment {

    public static String FRAGMENT_ACTION_SHOW_USER_BASIC_INFO = "fragment_action_show_basic_info";
    public static String ARG_USER_BASIC_INFO = "arg_basic_info";
    public static String ARG_USER_ROLE = "arg_user_role";

    public UserBasicInfoFragment() {
        // Required empty public constructor
    }
    public static UserBasicInfoFragment newInstance() {
        return new UserBasicInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private UserDTO user;
    private String role;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_basic_info, container, false);

        TextView txtName = view.findViewById(R.id.infoFullName);
        TextView txtEmail = view.findViewById(R.id.infoEmail);
        TextView txtNumber = view.findViewById(R.id.infoPhoneNumber);
        TextView txtAddress = view.findViewById(R.id.infoAddress);
        ImageView imgView = view.findViewById(R.id.infoProfilePicture);

        getParentFragmentManager().setFragmentResultListener(FRAGMENT_ACTION_SHOW_USER_BASIC_INFO, getActivity(), (requestKey, result) -> {
            user = (UserDTO) result.getSerializable(ARG_USER_BASIC_INFO);
            role = result.getString(ARG_USER_ROLE);
            txtName.setText(user.getName() + " " + user.getSurname());
            txtEmail.setText(user.getEmail());
            txtNumber.setText(user.getTelephoneNumber());
            txtAddress.setText(user.getAddress());
            imgView.setImageBitmap(Bitmaper.toBitmap(user.getProfilePicture()));
        });

        view.findViewById(R.id.infoEditButton).setOnClickListener(v -> {
            if (role.equals("PASSENGER")) {
                Intent edit = new Intent(getContext(), EditPassengerBasicInfo.class);
                edit.putExtra(EditUserBasicInfo.ARG_USER_ID, user.getId());
                startActivity(edit);
            } else {
                Intent edit = new Intent(getContext(), EditDriverBasicInfo.class);
                edit.putExtra(EditUserBasicInfo.ARG_USER_ID, user.getId());
                startActivity(edit);
            }
        });

        return view;
    }
}