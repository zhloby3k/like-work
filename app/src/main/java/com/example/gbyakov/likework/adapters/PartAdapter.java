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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class PartAdapter extends CursorAdapter {

    public PartAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final ImageView statusView;
        public final TextView nameView;
        public final TextView codeView;
        public final TextView catnumberView;
        public final TextView amountView;
        public final TextView sumView;

        public ViewHolder(View view) {
            statusView      = (ImageView) view.findViewById(R.id.list_item_parts_status);
            nameView        = (TextView) view.findViewById(R.id.list_item_parts_name);
            codeView        = (TextView) view.findViewById(R.id.list_item_parts_code);
            catnumberView   = (TextView) view.findViewById(R.id.list_item_parts_catnumber);
            amountView      = (TextView) view.findViewById(R.id.list_item_parts_amount);
            sumView         = (TextView) view.findViewById(R.id.list_item_parts_sum);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_parts, parent, false);

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

        Integer color = Color.RED;
        Integer status = cursor.getInt(cursor.getColumnIndex(LikeWorkContract.PartEntry.COLUMN_STATUS));
        switch (status) {
            case 2: color = Color.GREEN; break;
            case 3: color = Color.YELLOW; break;
            case 4: color = Color.CYAN; break;
            case 5: color = Color.BLUE; break;
        }
        drawable.getPaint().setColor(color);
        viewHolder.statusView.setImageDrawable(drawable);

        String name = cursor.getString(cursor.getColumnIndex(LikeWorkContract.PartEntry.COLUMN_NAME));
        viewHolder.nameView.setText(name);

        String code   = cursor.getString(cursor.getColumnIndex(LikeWorkContract.PartEntry.COLUMN_CODE_1C));
        viewHolder.codeView.setText(code);

        String catnumber = cursor.getString(cursor.getColumnIndex(LikeWorkContract.PartEntry.COLUMN_CATNUM));
        viewHolder.catnumberView.setText(catnumber);

        double amount    = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.PartEntry.COLUMN_AMOUNT));
        DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
        unusualSymbols.setDecimalSeparator('.');
        unusualSymbols.setGroupingSeparator(' ');

        DecimalFormat myFormatter = new DecimalFormat("###,###.##", unusualSymbols);
        myFormatter.setGroupingSize(3);
        viewHolder.amountView.setText(myFormatter.format(amount));

        double sum       = cursor.getDouble(cursor.getColumnIndex(LikeWorkContract.PartEntry.COLUMN_SUM));
        myFormatter = new DecimalFormat("###,##0.00", unusualSymbols);
        myFormatter.setGroupingSize(3);
        viewHolder.sumView.setText(myFormatter.format(sum));

    }

}
