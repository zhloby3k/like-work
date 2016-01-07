package com.example.gbyakov.likework.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.adapters.OrderAdapter;
import com.example.gbyakov.likework.data.LikeWorkContract;

public class OrdersListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private OrderAdapter mOrderAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private static final int ORDER_LOADER = 0;

    private static final String[] ORDER_COLUMNS = {
        LikeWorkContract.OrderEntry.TABLE_NAME + "." + LikeWorkContract.OrderEntry._ID + " " + LikeWorkContract.OrderEntry._ID,
        LikeWorkContract.OrderEntry.COLUMN_DATE,
        LikeWorkContract.OrderEntry.COLUMN_SUM,
        LikeWorkContract.StatusEntry.COLUMN_NAME,
        LikeWorkContract.StatusEntry.COLUMN_GROUP,
        LikeWorkContract.CarEntry.COLUMN_MODEL,
        LikeWorkContract.CarEntry.COLUMN_REGNUMBER,
        "Client." + LikeWorkContract.ClientEntry.COLUMN_NAME + " " + LikeWorkContract.ClientEntry.COLUMN_NAME
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mOrderAdapter = new OrderAdapter(getActivity(), null, 0);

        View x = inflater.inflate(R.layout.fragment_orders_list, container, false);

        mListView = (ListView) x.findViewById(R.id.orders_listview);
        mListView.setAdapter(mOrderAdapter);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Реестр заказ-нарядов");

        return x;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ORDER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                LikeWorkContract.OrderEntry.CONTENT_URI,
                ORDER_COLUMNS,
                null,
                null,
                LikeWorkContract.StatusEntry.COLUMN_GROUP);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mOrderAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
             mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mOrderAdapter.swapCursor(null);
    }

}
