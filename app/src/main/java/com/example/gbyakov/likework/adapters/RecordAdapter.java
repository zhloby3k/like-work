package com.example.gbyakov.likework.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordAdapter extends CursorAdapter {

    public RecordAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView timeView;
        public final TextView carView;
        public final TextView clientView;
        public final TextView detailsView;
        public final ImageView statusView;

        public ViewHolder(View view) {
            timeView    = (TextView) view.findViewById(R.id.list_item_time);
            carView     = (TextView) view.findViewById(R.id.list_item_car);
            clientView  = (TextView) view.findViewById(R.id.list_item_client);
            detailsView = (TextView) view.findViewById(R.id.list_item_details);
            statusView  = (ImageView) view.findViewById(R.id.list_item_status);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_records, parent, false);

        ViewHolder viewHolderOrder = new ViewHolder(view);
        view.setTag(viewHolderOrder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        long dateInMS    = cursor.getLong(cursor.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_DATE));
        Date date        = new Date(dateInMS);
        SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm");
        viewHolder.timeView.setText(dayFormat.format(date));

        String carModel  = cursor.getString(cursor.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_MODEL));
        String carRegNum = cursor.getString(cursor.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_REGNUMBER));
        viewHolder.carView.setText(carModel + " - " + carRegNum);

        String client    = cursor.getString(cursor.getColumnIndex(LikeWorkContract.ClientEntry.COLUMN_NAME));
        viewHolder.clientView.setText(client);

        String details   = cursor.getString(cursor.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_REASON));
        viewHolder.detailsView.setText(details);

        int done         = cursor.getInt(cursor.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_DONE));
        if (done == 1) {
            viewHolder.statusView.setImageResource(R.drawable.ic_check);
        }

    }

}
