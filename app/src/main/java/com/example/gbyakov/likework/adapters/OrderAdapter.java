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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;

    private static final int VIEW_TYPE_ORDER = 0;
    private static final int VIEW_TYPE_GROUP = 1;

    public OrderAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolderGroup {
        public final TextView groupView;

        public ViewHolderGroup(View view) {
            groupView   = (TextView) view.findViewById(R.id.list_item_order_group);
        }
    }

    public static class ViewHolderOrder {
        public final TextView dateView;
        public final TextView carView;
        public final TextView clientView;
        public final TextView statusView;
        public final TextView sumView;

        public ViewHolderOrder(View view) {
            dateView    = (TextView) view.findViewById(R.id.list_item_order_date);
            carView     = (TextView) view.findViewById(R.id.list_item_order_car);
            clientView  = (TextView) view.findViewById(R.id.list_item_order_client);
            statusView  = (TextView) view.findViewById(R.id.list_item_order_status);
            sumView     = (TextView) view.findViewById(R.id.list_item_order_sum);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int isGroup = cursor.getInt(cursor.getColumnIndex("isGroup"));
        int layoutId = (isGroup == 1) ? R.layout.list_item_orders_status : R.layout.list_item_orders;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        if (isGroup == 1) {
            ViewHolderGroup viewHolderGroup = new ViewHolderGroup(view);
            view.setTag(viewHolderGroup);
        }
        else {
            ViewHolderOrder viewHolderOrder = new ViewHolderOrder(view);
            view.setTag(viewHolderOrder);
        }

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int isGroup = cursor.getInt(cursor.getColumnIndex("isGroup"));
        if (isGroup == 1) {
            ViewHolderGroup viewHolder = (ViewHolderGroup) view.getTag();

            String group_name = cursor.getString(cursor.getColumnIndex(LikeWorkContract.StatusEntry.COLUMN_GROUP));
            viewHolder.groupView.setText(group_name);
        }
        else {
            ViewHolderOrder viewHolder = (ViewHolderOrder) view.getTag();

            long dateInMS    = cursor.getLong(cursor.getColumnIndex(LikeWorkContract.OrderEntry.COLUMN_DATE));
            Date date        = new Date(dateInMS);
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yy");
            viewHolder.dateView.setText(dayFormat.format(date));

            String carModel  = cursor.getString(cursor.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_MODEL));
            String carRegNum = cursor.getString(cursor.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_REGNUMBER));
            viewHolder.carView.setText(carModel + " - " + carRegNum);

            String client    = cursor.getString(cursor.getColumnIndex(LikeWorkContract.ClientEntry.COLUMN_NAME));
            viewHolder.clientView.setText(client);

            String status    = cursor.getString(cursor.getColumnIndex(LikeWorkContract.StatusEntry.COLUMN_NAME));
            viewHolder.statusView.setText(status);

            double sum       = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.OrderEntry.COLUMN_SUM));
            DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
            unusualSymbols.setDecimalSeparator('.');
            unusualSymbols.setGroupingSeparator(' ');

            DecimalFormat myFormatter = new DecimalFormat("###,##0.00", unusualSymbols);
            myFormatter.setGroupingSize(3);
            viewHolder.sumView.setText(myFormatter.format(sum));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        int isGroup = cursor.getInt(cursor.getColumnIndex("isGroup"));
        return (isGroup == 1) ? VIEW_TYPE_GROUP : VIEW_TYPE_ORDER;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

}