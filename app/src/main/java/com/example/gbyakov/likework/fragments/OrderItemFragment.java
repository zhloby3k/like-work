package com.example.gbyakov.likework.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ORDER_URI = "URI";
    private static final int ORDER_LOADER = 0;

    private static final String[] ORDER_COLUMNS = {
            LikeWorkContract.OrderEntry.TABLE_NAME + "." + LikeWorkContract.OrderEntry._ID,
            LikeWorkContract.OrderEntry.COLUMN_NUMBER,
            LikeWorkContract.OrderEntry.COLUMN_DATE,
            LikeWorkContract.OrderEntry.COLUMN_COMMENT,
            LikeWorkContract.OrderEntry.COLUMN_NUMBER,
            LikeWorkContract.OrderEntry.COLUMN_SUM,
            LikeWorkContract.OrderEntry.COLUMN_TYPE,
            "Client."+LikeWorkContract.ClientEntry.COLUMN_NAME + " ClientName",
            "Customer."+LikeWorkContract.ClientEntry.COLUMN_NAME + " CustomerName",
            LikeWorkContract.StatusEntry.COLUMN_NAME,
            LikeWorkContract.CarEntry.COLUMN_BRAND,
            LikeWorkContract.CarEntry.COLUMN_MODEL,
            LikeWorkContract.CarEntry.COLUMN_REGNUMBER,
    };

    private TextView mCustomerView;
    private TextView mDateView;
    private TextView mClientView;
    private TextView mClientCaptionView;
    private TextView mCarView;
    private TextView mCommentView;
    private ImageView mCommentLogoView;
    private TextView mStatusView;
    private TextView mTypeView;
    private TextView mSumView;

    private Uri mUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ORDER_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_order_item, container, false);
        mCustomerView = (TextView) rootView.findViewById(R.id.order_customer);
        mDateView = (TextView) rootView.findViewById(R.id.order_date);
        mClientView = (TextView) rootView.findViewById(R.id.order_client);
        mClientCaptionView = (TextView) rootView.findViewById(R.id.order_client_caption);
        mCarView = (TextView) rootView.findViewById(R.id.order_car);
        mCommentView = (TextView) rootView.findViewById(R.id.order_comment);
        mCommentLogoView = (ImageView) rootView.findViewById(R.id.order_comment_logo);
        mStatusView = (TextView) rootView.findViewById(R.id.order_status);
        mTypeView = (TextView) rootView.findViewById(R.id.order_type);
        mSumView = (TextView) rootView.findViewById(R.id.order_sum);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ORDER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( mUri != null ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    ORDER_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            long dateInMS    = data.getLong(data.getColumnIndex(LikeWorkContract.OrderEntry.COLUMN_DATE));
            Date date        = new Date(dateInMS);
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yy");
            mDateView.setText(dayFormat.format(date));

            String carBrand  = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_BRAND));
            String carModel  = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_MODEL));
            String carRegNum = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_REGNUMBER));
            mCarView.setText(carBrand + " " + carModel + " - " + carRegNum);

            String client    = data.getString(data.getColumnIndex("ClientName"));
            mClientView.setText(client);

            String customer  = data.getString(data.getColumnIndex("CustomerName"));
            mCustomerView.setText(customer);

            if (client.equals(customer)) {
                mClientView.setVisibility(View.GONE);
                mClientCaptionView.setVisibility(View.GONE);
            }

            String status    = data.getString(data.getColumnIndex(LikeWorkContract.StatusEntry.COLUMN_NAME));
            mStatusView.setText((status.isEmpty()) ? "-" : status);

            double sum       = data.getDouble(data.getColumnIndex(LikeWorkContract.OrderEntry.COLUMN_SUM));
            DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
            unusualSymbols.setDecimalSeparator('.');
            unusualSymbols.setGroupingSeparator(' ');

            DecimalFormat myFormatter = new DecimalFormat("###,##0.00", unusualSymbols);
            myFormatter.setGroupingSize(3);
            mSumView.setText(myFormatter.format(sum));

            String type    = data.getString(data.getColumnIndex(LikeWorkContract.OrderEntry.COLUMN_TYPE));
            mTypeView.setText(type);

            String comment = data.getString(data.getColumnIndex(LikeWorkContract.OrderEntry.COLUMN_COMMENT));
            mCommentView.setText(comment);

            if (comment.isEmpty()) {
                mCommentView.setVisibility(View.GONE);
                mCommentLogoView.setVisibility(View.GONE);
            }

            String number = data.getString(data.getColumnIndex(LikeWorkContract.OrderEntry.COLUMN_NUMBER));
            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) actionBar.setTitle("Заказ-наряд "+number);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
