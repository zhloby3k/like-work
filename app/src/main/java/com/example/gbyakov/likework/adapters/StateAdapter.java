package com.example.gbyakov.likework.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StateAdapter extends CursorAdapter {

    public StateAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView dateView;
        public final TextView userView;
        public final TextView statusView;

        public ViewHolder(View view) {
            dateView    = (TextView) view.findViewById(R.id.list_item_state_date);
            userView    = (TextView) view.findViewById(R.id.list_item_state_user);
            statusView  = (TextView) view.findViewById(R.id.list_item_state_status);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_states, parent, false);

        ViewHolder viewHolderOrder = new ViewHolder(view);
        view.setTag(viewHolderOrder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        long dateInMS    = cursor.getLong(cursor.getColumnIndex(LikeWorkContract.StateEntry.COLUMN_DATE));
        Date date        = new Date(dateInMS);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yy");
        viewHolder.dateView.setText(dayFormat.format(date));

        String client    = cursor.getString(cursor.getColumnIndex(LikeWorkContract.StatusEntry.COLUMN_NAME));
        viewHolder.statusView.setText(client);

        String details   = cursor.getString(cursor.getColumnIndex(LikeWorkContract.StateEntry.COLUMN_USER));
        viewHolder.userView.setText(details);

    }
}
