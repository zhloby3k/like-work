package com.example.gbyakov.likework.adapters;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class OperationAdapter extends CursorAdapter {

    public OperationAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView statusView;
        public final TextView nameView;
        public final TextView catnumberView;
        public final TextView amountView;
        public final TextView sumView;

        public ViewHolder(View view) {
            statusView      = (TextView) view.findViewById(R.id.list_item_operations_status);
            nameView        = (TextView) view.findViewById(R.id.list_item_operations_name);
            catnumberView   = (TextView) view.findViewById(R.id.list_item_operations_catnumber);
            amountView      = (TextView) view.findViewById(R.id.list_item_operations_amount);
            sumView         = (TextView) view.findViewById(R.id.list_item_operations_sum);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_operations, parent, false);

        ViewHolder viewHolderOrder = new ViewHolder(view);
        view.setTag(viewHolderOrder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.setIntrinsicHeight(24);
        drawable.setIntrinsicWidth(24);
        drawable.getPaint().setColor(Color.parseColor("#BCAAA4"));
        viewHolder.statusView.setBackground(drawable);

        String status = cursor.getString(cursor.getColumnIndex(LikeWorkContract.OperationEntry.COLUMN_STATUS));
        viewHolder.statusView.setText(status);

        String name = cursor.getString(cursor.getColumnIndex(LikeWorkContract.OperationEntry.COLUMN_NAME));
        viewHolder.nameView.setText(name);

        String catnumber = cursor.getString(cursor.getColumnIndex(LikeWorkContract.OperationEntry.COLUMN_CODE_1C));
        viewHolder.catnumberView.setText(catnumber);

        double amount    = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.OperationEntry.COLUMN_AMOUNT));
        DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
        unusualSymbols.setDecimalSeparator('.');
        unusualSymbols.setGroupingSeparator(' ');

        DecimalFormat myFormatter = new DecimalFormat("###,###.##", unusualSymbols);
        myFormatter.setGroupingSize(3);
        viewHolder.amountView.setText(myFormatter.format(amount));

        double sum       = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.OperationEntry.COLUMN_SUM));
        myFormatter = new DecimalFormat("###,##0.00", unusualSymbols);
        myFormatter.setGroupingSize(3);
        viewHolder.sumView.setText(myFormatter.format(sum));

    }
}
