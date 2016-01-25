package com.example.gbyakov.likework.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
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
        public final TextView trendView;
        public final TextView percentView;
        public final LinearLayout percentLayaout;
        public final LinearLayout containerView;

        public ViewHolder(View view) {
            nameView    = (TextView) view.findViewById(R.id.kpi_header);
            valueView   = (TextView) view.findViewById(R.id.kpi_value);
            trendView   = (TextView) view.findViewById(R.id.kpi_trend);
            percentView = (TextView) view.findViewById(R.id.kpi_percent);
            percentLayaout  = (LinearLayout) view.findViewById(R.id.kpi_percents);
            containerView   = (LinearLayout) view.findViewById(R.id.kpi_container);
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

        Integer isPercent = cursor.getInt(cursor.getColumnIndex(LikeWorkContract.KpiEntry.COLUMN_ISPERCENT));

        String name   = cursor.getString(cursor.getColumnIndex(LikeWorkContract.KpiEntry.COLUMN_NAME));
        viewHolder.nameView.setText(name);

        double value  = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.KpiEntry.COLUMN_VALUE));
        DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
        unusualSymbols.setDecimalSeparator('.');
        unusualSymbols.setGroupingSeparator(' ');

        DecimalFormat myFormatter = new DecimalFormat("##,###,###", unusualSymbols);
        myFormatter.setGroupingSize(3);
        viewHolder.valueView.setText(myFormatter.format(value) + ((isPercent == 1) ? " %" : ""));

        if (isPercent == 0) {
            viewHolder.percentLayaout.setVisibility(View.VISIBLE);

            double percent = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.KpiEntry.COLUMN_PERCENT));
            myFormatter = new DecimalFormat("##0", unusualSymbols);
            myFormatter.setGroupingSize(3);
            viewHolder.percentView.setText(myFormatter.format(percent)+" %");

            double trend = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.KpiEntry.COLUMN_TREND));
            myFormatter = new DecimalFormat("##0", unusualSymbols);
            myFormatter.setGroupingSize(3);
            viewHolder.trendView.setText(myFormatter.format(trend) + " %");

            value = trend;
        } else {
            viewHolder.percentLayaout.setVisibility(View.GONE);
            viewHolder.valueView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }

        if (value >= 95) viewHolder.containerView.setBackgroundColor(Color.parseColor("#C8E6C9"));
        else if (value >= 85 && value < 95) viewHolder.containerView.setBackgroundColor(Color.parseColor("#FFECB3"));
        else if (value > 0 && value < 85) viewHolder.containerView.setBackgroundColor(Color.parseColor("#FFCDD2"));
    }
}
