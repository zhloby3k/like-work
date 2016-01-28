package com.example.gbyakov.likework.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gbyakov.likework.MainActivity;
import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.adapters.OrderAdapter;
import com.example.gbyakov.likework.data.LikeWorkContract;

public class OrdersListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private OrderAdapter mOrderAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    OnItemSelectedListener mListener;

    private static final int ORDER_LOADER = 0;

    private static final String[] ORDER_COLUMNS = {
        LikeWorkContract.OrderEntry.TABLE_NAME + "." + LikeWorkContract.OrderEntry._ID + " " + LikeWorkContract.OrderEntry._ID,
        LikeWorkContract.OrderEntry.COLUMN_DATE,
        LikeWorkContract.OrderEntry.COLUMN_SUM,
        LikeWorkContract.StatusEntry.COLUMN_NAME,
        LikeWorkContract.StatusEntry.COLUMN_GROUP,
        LikeWorkContract.StatusEntry.COLUMN_COLOR,
        LikeWorkContract.CarEntry.COLUMN_MODEL,
        LikeWorkContract.CarEntry.COLUMN_REGNUMBER,
        "Client." + LikeWorkContract.ClientEntry.COLUMN_NAME + " " + LikeWorkContract.ClientEntry.COLUMN_NAME
    };

    public interface OnItemSelectedListener {
        void OnItemSelected(Uri itemUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mOrderAdapter = new OrderAdapter(getActivity(), null, 0);

        View x = inflater.inflate(R.layout.fragment_orders_list, container, false);

        mListView = (ListView) x.findViewById(R.id.orders_listview);
        mListView.setAdapter(mOrderAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Integer id = cursor.getInt(cursor.getColumnIndex(LikeWorkContract.OrderEntry._ID));
                    Uri itemUri = LikeWorkContract.OrderEntry.buildOrderID(id);
                    mListener.OnItemSelected(itemUri);
                }
                mPosition = position;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                SwipeRefreshLayout mSwipeRefresh = ((MainActivity) getActivity()).mSwipeRefresh;
                mSwipeRefresh.setEnabled((firstVisibleItem == 0));
            }
        });

        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("Реестр заказ-нарядов");

        return x;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ORDER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof Activity){
            try {
                mListener = (OnItemSelectedListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() + " must implement OnItemSelectedListener");
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                LikeWorkContract.OrderEntry.buildOrderWithGroups(),
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
