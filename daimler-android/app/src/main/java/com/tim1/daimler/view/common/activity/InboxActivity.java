package com.tim1.daimler.view.common.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.message.InboxDTO;
import com.tim1.daimler.dtos.user.UserDTO;
import com.tim1.daimler.dtos.user.UserInRideDTO;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.util.adapter.InboxAdapter;
import com.tim1.daimler.util.data.InboxItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboxActivity extends AppCompatActivity {

    public static final String ARG_USER = "arg_user";
    private UserInRideDTO user;
    private List<InboxDTO> inboxDTOS;
    private InboxAdapter adapter;
    private ListView inboxListView;
    private String searchQuery;
    private boolean showPanicChats;
    private boolean showSupportChats;
    private boolean showRideChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.makeFullScreen(this);
        user = new UserInRideDTO((UserDTO) getIntent().getSerializableExtra(ARG_USER));
        inboxListView = findViewById(R.id.chatsList);
        inboxListView.addHeaderView(getLayoutInflater().inflate(R.layout.header_inbox, null, false));
        showPanicChats = true;
        showSupportChats = true;
        showRideChats = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPanicChats = true;
        showSupportChats = true;
        showRideChats = true;
        getMessages();
    }

    private void getMessages() {
        Call<List<InboxDTO>> call = ServiceGenerator.messagesService.getInbox(user.getId());
        call.enqueue(new Callback<List<InboxDTO>>() {
            @Override
            public void onResponse(Call<List<InboxDTO>> call, Response<List<InboxDTO>> response) {
                inboxDTOS = response.body();
                if (searchQuery != null && !searchQuery.isEmpty()) {
                    inboxDTOS = inboxDTOS.stream().filter(i -> i.getPersonName().toLowerCase().contains(searchQuery.toLowerCase())).collect(Collectors.toList());
                }
                if (!showPanicChats) {
                    inboxDTOS = inboxDTOS.stream().filter(i -> !i.getLastMessage().getType().equals("PANIC")).collect(Collectors.toList());
                }
                if (!showSupportChats) {
                    inboxDTOS = inboxDTOS.stream().filter(i -> !i.getLastMessage().getType().equals("SUPPORT")).collect(Collectors.toList());
                }
                if (!showRideChats) {
                    inboxDTOS = inboxDTOS.stream().filter(i -> !i.getLastMessage().getType().equals("RIDE")).collect(Collectors.toList());
                }
                initList(new ArrayList<>(inboxDTOS.stream().map(i -> (InboxItem)i).collect(Collectors.toList())));
            }
            @Override
            public void onFailure(Call<List<InboxDTO>> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "There was a problem with getting messages.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initList(ArrayList<InboxItem> inboxItems) {
        adapter = new InboxAdapter(inboxItems, this);
        inboxListView.setAdapter(adapter);
        inboxListView.setOnItemClickListener((adapterView, view, i, l) -> openChatDetails(view, adapter.get(i-1)));
    }

    private void openChatDetails(View view, InboxDTO item) {
        Intent openChat = new Intent(InboxActivity.this, ChatActivity.class);
        openChat.putExtra(ChatActivity.ARG_USER_FROM, user);
        openChat.putExtra(ChatActivity.ARG_USER_TO, new UserInRideDTO(item.getWith()));
        openChat.putExtra(ChatActivity.ARG_RIDE_ID, item.getLastMessage().getRideId());
        openChat.putExtra(ChatActivity.ARG_TYPE, item.getLastMessage().getType());
        openChat.putExtra(ChatActivity.ARG_ADDRESS_DESTINATION, item.getLastMessage().getType().equals("RIDE") ? item.getDestination() : item.getLastMessage().getType() + " CHAT");
        startActivity(openChat);
    }

    public void backClicked(View view) {
        finish();
    }

    public void searchClicked(View view) {
        TextInputLayout inboxSearchLayout = inboxListView.findViewById(R.id.inboxSearchLayout);
        this.searchQuery = Objects.requireNonNull(inboxSearchLayout.getEditText()).getText().toString();
        getMessages();
    }

    public void applyFilter(View view) {
        MaterialCheckBox panicCheckbox = inboxListView.findViewById(R.id.inboxFilterPanicCheckBox);
        showPanicChats = panicCheckbox.isChecked();
        MaterialCheckBox supportCheckbox = inboxListView.findViewById(R.id.inboxFilterSupportCheckBox);
        showSupportChats = supportCheckbox.isChecked();
        MaterialCheckBox rideCheckbox = inboxListView.findViewById(R.id.inboxFilterRideCheckBox);
        showRideChats = rideCheckbox.isChecked();
        getMessages();
    }

    public void filterClicked(View view) {
        LinearLayout filterLayout = inboxListView.findViewById(R.id.inbox_filter_layout);
        if (filterLayout.getVisibility() == View.VISIBLE) {
            filterLayout.setVisibility(View.GONE);
        } else {
            filterLayout.setVisibility(View.VISIBLE);
        }
    }
}