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
import com.example.gbyakov.likework.adapters.CallAdapter;
import com.example.gbyakov.likework.data.LikeWorkContract;

public class CallsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private CallAdapter mCallAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private static final int CALL_LOADER = 0;

    private static final String[] CALL_COLUMNS = {
            LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry._ID,
            LikeWorkContract.CallEntry.COLUMN_DATE,
            LikeWorkContract.CallEntry.COLUMN_REASON,
            LikeWorkContract.CarEntry.COLUMN_MODEL,
            LikeWorkContract.CarEntry.COLUMN_REGNUMBER,
            "Client." + LikeWorkContract.ClientEntry.COLUMN_NAME + " " + LikeWorkContract.ClientEntry.COLUMN_NAME
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mCallAdapter = new CallAdapter(getActivity(), null, 0);

        View x = inflater.inflate(R.layout.fragment_calls_list, container, false);

        mListView = (ListView) x.findViewById(R.id.calls_listview);
        mListView.setAdapter(mCallAdapter);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Звонки заботы");

        return x;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CALL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
            LikeWorkContract.CallEntry.CONTENT_URI,
            CALL_COLUMNS,
            null,
            null,
            LikeWorkContract.CallEntry.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCallAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallAdapter.swapCursor(null);
    }
}