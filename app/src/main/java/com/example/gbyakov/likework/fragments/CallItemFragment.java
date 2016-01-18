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
import android.widget.TextView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CallItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String CALL_URI = "URI";
    private static final int CALL_LOADER = 0;

    private static final String[] CALL_COLUMNS = {
            LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry._ID,
            LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry.COLUMN_ID_1C,
            LikeWorkContract.CallEntry.COLUMN_DATE,
            LikeWorkContract.CallEntry.COLUMN_REASON,
            LikeWorkContract.CallEntry.COLUMN_SUM,
            LikeWorkContract.CallEntry.COLUMN_TYPE,
            "Client."+LikeWorkContract.ClientEntry.COLUMN_NAME + " ClientName",
            LikeWorkContract.CarEntry.COLUMN_BRAND,
            LikeWorkContract.CarEntry.COLUMN_MODEL,
            LikeWorkContract.CarEntry.COLUMN_REGNUMBER,
    };

    private TextView mClientView;
    private TextView mCarView;
    private TextView mCommentView;
    private TextView mTypeView;
    private TextView mSumView;

    private Uri mUri;
    private String mDocId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(CALL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_call_item, container, false);
        mClientView = (TextView) rootView.findViewById(R.id.call_client);
        mCarView = (TextView) rootView.findViewById(R.id.call_car);
        mCommentView = (TextView) rootView.findViewById(R.id.call_comment);
        mTypeView = (TextView) rootView.findViewById(R.id.call_type);
        mSumView = (TextView) rootView.findViewById(R.id.call_sum);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CALL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( mUri != null ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    CALL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            String carBrand  = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_BRAND));
            String carModel = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_MODEL));
            String carRegNum = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_REGNUMBER));
            mCarView.setText(carBrand + " " + carModel + " - " + carRegNum);

            String client    = data.getString(data.getColumnIndex("ClientName"));
            mClientView.setText(client);

            double sum       = data.getDouble(data.getColumnIndex(LikeWorkContract.CallEntry.COLUMN_SUM));
            DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
            unusualSymbols.setDecimalSeparator('.');
            unusualSymbols.setGroupingSeparator(' ');

            DecimalFormat myFormatter = new DecimalFormat("###,##0.00", unusualSymbols);
            myFormatter.setGroupingSize(3);
            mSumView.setText(myFormatter.format(sum));

            String type    = data.getString(data.getColumnIndex(LikeWorkContract.CallEntry.COLUMN_TYPE));
            mTypeView.setText(type);

            String comment = data.getString(data.getColumnIndex(LikeWorkContract.CallEntry.COLUMN_REASON));
            mCommentView.setText(comment);

            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) actionBar.setTitle("Звонок заботы");

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
