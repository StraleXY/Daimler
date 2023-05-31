package com.tim1.daimler.view.common.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.tim1.daimler.R;
import com.tim1.daimler.dtos.message.CreateMessageDTO;
import com.tim1.daimler.dtos.message.MessageDTO;
import com.tim1.daimler.dtos.message.MessagesDTO;
import com.tim1.daimler.dtos.user.UserInRideDTO;
import com.tim1.daimler.model.Message;
import com.tim1.daimler.model.User;
import com.tim1.daimler.util.ServiceGenerator;
import com.tim1.daimler.util.Styler;
import com.tim1.daimler.util.adapter.MessageListAdapter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    public static final String ARG_USER_FROM = "user_from";
    public static final String ARG_USER_TO = "user_to";
    public static final String ARG_RIDE_ID = "ride_id";
    public static final String ARG_TYPE = "type";
    public static final String ARG_ADDRESS_DESTINATION = "address_to";

    private RecyclerView mMessageRecycler;
    private UserInRideDTO uFrom, uTo;
    private MessagesDTO messages = new MessagesDTO();
    private int rideId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Styler.hideStatusBar(this, findViewById(R.id.rootChatActivity));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(!this.obtainStyledAttributes(R.style.Theme_Daimler, R.styleable.Charter).getBoolean(R.styleable.Charter_android_windowLightStatusBar, false));
        }

        Log.d("STIGAO", "DO IZVLACENJA PODATAKA");
        uFrom = (UserInRideDTO) getIntent().getSerializableExtra(ARG_USER_FROM);
        uTo = (UserInRideDTO) getIntent().getSerializableExtra(ARG_USER_TO);
        rideId = getIntent().getIntExtra(ARG_RIDE_ID, 0);

        ((TextView)findViewById(R.id.chatPerson)).setText((uTo.getName() + " " + uTo.getSurname()).toUpperCase(Locale.ROOT));
        ((TextView)findViewById(R.id.chatTitle)).setText(getIntent().getStringExtra(ARG_ADDRESS_DESTINATION));
        Log.d("STIGAO", "DO KRAJA MAINA");
        populateMessages();
    }

    private void populateMessages() {

        List<Message> messages = new ArrayList<>();
        mMessageRecycler = findViewById(R.id.recyclerChat);
        MessageListAdapter mMessageAdapter = new MessageListAdapter(this, messages);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        getMessages();
    }

    private void getMessages() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("STIGAO", "DO POZIVA GET MESSAGES");
                Call<MessagesDTO> call = ServiceGenerator.messagesService.getMessagesBetweenUsers(uFrom.getId(), uTo.getId(), getIntent().getStringExtra(ARG_TYPE), rideId);
                call.enqueue(new Callback<MessagesDTO>() {
                    @Override
                    public void onResponse(Call<MessagesDTO> call, Response<MessagesDTO> response) {
                        Log.d("STIGAO", "U BODY POZIVA GET MESSAGES");
                        if (response.body() == null) return;
                        Log.d("CHAT", "Got " + response.body().getTotalCount());
                        List<MessageDTO> newMessages = messages.getNew(response.body());
                        for(MessageDTO message : newMessages) {
                            runOnUiThread(() -> addMessageToFront(new Message(message.getMessage(), message.getSenderId().equals(uTo.getId()) ? uTo : uFrom, message.getTimestamp(), message.getSenderId().equals(uTo.getId()) ? Message.Types.RECEIVED : Message.Types.SENT)));
                        }
                    }

                    @Override
                    public void onFailure(Call<MessagesDTO> call, Throwable t) {
                        call.cancel();
                        Log.d("MSG", t.getMessage());
                    }
                });
            }
        }, 0, 2000);
    }

    public void sendMessage(View view) {
        Message sent = new Message(((TextInputLayout)findViewById(R.id.messageLayout)).getEditText().getText().toString(), uFrom, ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("ECT")).toInstant().toEpochMilli(), Message.Types.SENT);
        sendToBack(sent);
        addMessageToFront(sent);
        View focused = getCurrentFocus();
        if (focused == null) focused = new View(this);
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(focused.getWindowToken(), 0);
        focused.clearFocus();
        Objects.requireNonNull(((TextInputLayout) findViewById(R.id.messageLayout)).getEditText()).setText("");
    }

    public void addMessageToFront(Message message) {
        ((MessageListAdapter)mMessageRecycler.getAdapter()).addItems(message);
        mMessageRecycler.scrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1);
    }

    private void sendToBack(Message message) {
        CreateMessageDTO newMessage = new CreateMessageDTO();
        newMessage.setReceiverId(uTo.getId());
        newMessage.setMessage(message.getMessage());
        newMessage.setRideId(getIntent().getIntExtra(ChatActivity.ARG_RIDE_ID, 1));
        newMessage.setTimestamp(message.getCreatedAt());
        newMessage.setType("RIDE");
        Call<MessageDTO> call = ServiceGenerator.messagesService.sendMessage(uFrom.getId(), newMessage);
        call.enqueue(new Callback<MessageDTO>() {
            @Override
            public void onResponse(Call<MessageDTO> call, Response<MessageDTO> response) {
                messages.addNew(response.body());
                Log.d("CHAT", "Message sent!");
            }
            @Override
            public void onFailure(Call<MessageDTO> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Sending message failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}