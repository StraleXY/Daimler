package com.tim1.daimler.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.tim1.daimler.R;
import com.tim1.daimler.dtos.message.InboxDTO;
import com.tim1.daimler.model.Message;
import com.tim1.daimler.util.Dater;
import com.tim1.daimler.util.data.InboxItem;

import java.util.ArrayList;
import java.util.Comparator;

public class InboxAdapter extends ArrayAdapter<InboxItem> {

    private ArrayList<InboxItem> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtPersonName;
        TextView txtDestination;
        TextView txtMessage;
        TextView txtDate;
        ImageView imgIsNotification;
        CardView pinedMarker;
    }

    public InboxDTO get(Integer id) {
        return (InboxDTO) dataSet.get(id);
    }

    public InboxAdapter(ArrayList<InboxItem> data, Context context){
        super(context, R.layout.list_inbox_item, data);
        data.sort((o1, o2) -> {
            if (o1.getIsPinned() && !o2.getIsPinned()) return -1;
            else if (!o1.getIsPinned() && o2.getIsPinned()) return 1;
            else return Long.compare(o2.getLastMessage().getTimestamp(), o1.getLastMessage().getTimestamp());
        });
        this.dataSet = data;
        this.mContext = context;
    }

    public void addItems(InboxItem inbox) {
        dataSet.add(inbox);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InboxItem dataModel = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_inbox_item, parent, false);
            viewHolder.txtPersonName = convertView.findViewById(R.id.inboxItemPerson);
            viewHolder.txtDestination = convertView.findViewById(R.id.inboxItemDestination);
            viewHolder.txtMessage = convertView.findViewById(R.id.inboxItemMessage);
            viewHolder.txtDate = convertView.findViewById(R.id.inboxItemDate);
            viewHolder.imgIsNotification = convertView.findViewById(R.id.inboxItemIsNotification);
            viewHolder.pinedMarker = convertView.findViewById(R.id.pinedMarker);
            convertView.setTag(viewHolder);
        }
        else viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.txtPersonName.setText(dataModel.getPersonName());
        viewHolder.txtDestination.setText(dataModel.getLastMessage().getType().equals("RIDE") ? dataModel.getDestination() : dataModel.getLastMessage().getType() + " CHAT");
        viewHolder.txtMessage.setText(dataModel.getLastMessage().getMessage());
        viewHolder.txtDate.setText(Dater.toDate(dataModel.getLastMessage().getTimestamp()));
        viewHolder.imgIsNotification.setVisibility(dataModel.getLastMessage().getType().equals(Message.Types.NOTIFICATION) ? View.VISIBLE : View.GONE);
        viewHolder.pinedMarker.setVisibility(dataModel.getIsPinned()? View.VISIBLE : View.GONE);

        return convertView;
    }
}