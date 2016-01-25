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

public class KpiAdapter extends CursorAdapter {

    public KpiAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView valueView;
        public final TextView percentView;

        public ViewHolder(View view) {
            nameView    = (TextView) view.findViewById(R.id.kpi_header);
            valueView   = (TextView) view.findViewById(R.id.kpi_value);
            percentView = (TextView) view.findViewById(R.id.kpi_percent);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_kpi, parent, false);

        ViewHolder viewHolderOrder = new ViewHolder(view);
        view.setTag(viewHolderOrder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String name   = cursor.getString(cursor.getColumnIndex(LikeWorkContract.KpiEntry.COLUMN_NAME));
        viewHolder.nameView.setText(name);

        double value  = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.KpiEntry.COLUMN_VALUE));
        DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
        unusualSymbols.setDecimalSeparator('.');
        unusualSymbols.setGroupingSeparator(' ');

        DecimalFormat myFormatter = new DecimalFormat("##,###,###.##", unusualSymbols);
        myFormatter.setGroupingSize(3);
        viewHolder.valueView.setText(myFormatter.format(value));

        double percent = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.KpiEntry.COLUMN_PERCENT));
        myFormatter = new DecimalFormat("##0.00", unusualSymbols);
        myFormatter.setGroupingSize(3);
        viewHolder.percentView.setText(myFormatter.format(value));

    }
}
