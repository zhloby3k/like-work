package com.example.gbyakov.likework.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String RECORD_URI = "URI";
    private static final int RECORD_LOADER = 0;

    private static final String[] RECORD_COLUMNS = {
            LikeWorkContract.RecordEntry.TABLE_NAME + "." + LikeWorkContract.RecordEntry._ID,
            LikeWorkContract.RecordEntry.TABLE_NAME + "." + LikeWorkContract.RecordEntry.COLUMN_ID_1C,
            LikeWorkContract.RecordEntry.COLUMN_NUMBER,
            LikeWorkContract.RecordEntry.COLUMN_DATE,
            LikeWorkContract.RecordEntry.COLUMN_COMMENT,
            LikeWorkContract.RecordEntry.COLUMN_SUM,
            LikeWorkContract.RecordEntry.COLUMN_TYPE,
            "Client."+LikeWorkContract.ClientEntry.COLUMN_NAME + " ClientName",
            "Customer."+LikeWorkContract.ClientEntry.COLUMN_NAME + " CustomerName",
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
    private LinearLayout mCommentLinearLayout;

    private TextView mTypeView;
    private TextView mSumView;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static int pagesCount = 2;

    private Uri mUri;
    private String mDocId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(RECORD_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_record_item, container, false);
        mCustomerView = (TextView) rootView.findViewById(R.id.record_customer);
        mDateView = (TextView) rootView.findViewById(R.id.record_date);
        mClientView = (TextView) rootView.findViewById(R.id.record_client);
        mClientCaptionView = (TextView) rootView.findViewById(R.id.record_client_caption);
        mCarView = (TextView) rootView.findViewById(R.id.record_car);
        mCommentView = (TextView) rootView.findViewById(R.id.record_comment);
        mCommentLinearLayout = (LinearLayout) rootView.findViewById(R.id.record_comment_layout);
        mTypeView = (TextView) rootView.findViewById(R.id.record_type);
        mSumView = (TextView) rootView.findViewById(R.id.record_sum);

        tabLayout = (TabLayout) rootView.findViewById(R.id.record_tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.record_viewpager);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECORD_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( mUri != null ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    RECORD_COLUMNS,
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

            long dateInMS    = data.getLong(data.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_DATE));
            Date date        = new Date(dateInMS);
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
            mDateView.setText(dayFormat.format(date));

            String carModel  = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_MODEL));
            String carRegNum = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_REGNUMBER));
            mCarView.setText(carModel + " - " + carRegNum);

            String client    = data.getString(data.getColumnIndex("ClientName"));
            mClientView.setText(client);

            String customer  = data.getString(data.getColumnIndex("CustomerName"));
            mCustomerView.setText(customer);

            if (client.equals(customer)) {
                mClientView.setVisibility(View.GONE);
                mClientCaptionView.setVisibility(View.GONE);
            }

            double sum       = data.getDouble(data.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_SUM));
            DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
            unusualSymbols.setDecimalSeparator('.');
            unusualSymbols.setGroupingSeparator(' ');

            DecimalFormat myFormatter = new DecimalFormat("###,##0.00", unusualSymbols);
            myFormatter.setGroupingSize(3);
            mSumView.setText(myFormatter.format(sum));

            String type    = data.getString(data.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_TYPE));
            mTypeView.setText(type);

            String comment = data.getString(data.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_COMMENT));
            mCommentView.setText(comment);

            if (comment.isEmpty()) {
                mCommentLinearLayout.setVisibility(View.GONE);
            }

            String number = data.getString(data.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_NUMBER));
            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) actionBar.setTitle("Запись "+number);

            mDocId = data.getString(data.getColumnIndex(LikeWorkContract.RecordEntry.COLUMN_ID_1C));
            viewPager.setAdapter(new RecordTabPagerAdapter(getChildFragmentManager()));

            tabLayout.post(new Runnable() {
                public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    class RecordTabPagerAdapter extends FragmentPagerAdapter {

        public RecordTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0 : {
                    Bundle args = new Bundle();
                    args.putString("id_1c", mDocId);
                    PartsListFragment fPartsList = new PartsListFragment();
                    fPartsList.setArguments(args);
                    return fPartsList;
                }
                case 1 : {
                    Bundle args = new Bundle();
                    args.putString("id_1c", mDocId);
                    OperatiosListFragment fOperationsList = new OperatiosListFragment();
                    fOperationsList.setArguments(args);
                    return fOperationsList;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return pagesCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 : return "Товары";
                case 1 : return "Работы";
            }
            return null;
        }
    }
}
