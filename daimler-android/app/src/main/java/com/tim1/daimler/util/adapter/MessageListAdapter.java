package com.tim1.daimler.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tim1.daimler.R;
import com.tim1.daimler.model.Message;
import com.tim1.daimler.util.Dater;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private final List<Message> mMessageList;

    public MessageListAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    public void addItems(Message message) {
        mMessageList.add(message);
        notifyDataSetChanged();
    }

    private static class BaseMessageHolder extends RecyclerView.ViewHolder {

        protected TextView txtContent, txtTime;
        public BaseMessageHolder(View itemView) {
            super(itemView);
        }

        protected void bind(@NonNull Message message) {
            txtContent.setText(message.getMessage());
            txtTime.setText(Dater.toTime(message.getCreatedAt()));
        }
    }

    private static class NotificationMessageHolder extends BaseMessageHolder {
        NotificationMessageHolder(View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.notificationMessageText);
            txtTime = itemView.findViewById(R.id.notificationMessageDate);
        }
    }

    private static class ReceivedMessageHolder extends BaseMessageHolder {
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.chatMessageOther);
            txtTime = itemView.findViewById(R.id.chatTimestampOther);
        }
    }

    private static class SentMessageHolder extends BaseMessageHolder {
        SentMessageHolder(View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.chatMessageUser);
            txtTime = itemView.findViewById(R.id.chatTimestampUser);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        return message.getType().getValue();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Message.Types.SENT.getValue()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_message, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == Message.Types.RECEIVED.getValue())  {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_other_message, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == Message.Types.NOTIFICATION.getValue())  {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notification_message, parent, false);
            return new NotificationMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (message.getType()) {
            case SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case NOTIFICATION:
                ((NotificationMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}
