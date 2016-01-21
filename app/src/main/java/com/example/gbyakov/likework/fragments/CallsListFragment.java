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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.adapters.CallAdapter;
import com.example.gbyakov.likework.data.LikeWorkContract;

public class CallsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private CallAdapter mCallAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    OnItemSelectedListener mListener;

    private static final int CALL_LOADER = 0;

    private static final String[] CALL_COLUMNS = {
            LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry._ID,
            LikeWorkContract.CallEntry.COLUMN_DATE,
            LikeWorkContract.CallEntry.COLUMN_REASON,
            LikeWorkContract.CallEntry.COLUMN_DONE,
            LikeWorkContract.CarEntry.COLUMN_MODEL,
            LikeWorkContract.CarEntry.COLUMN_REGNUMBER,
            "Client." + LikeWorkContract.ClientEntry.COLUMN_NAME + " " + LikeWorkContract.ClientEntry.COLUMN_NAME
    };


    public interface OnItemSelectedListener {
        void OnItemSelected(Uri itemUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mCallAdapter = new CallAdapter(getActivity(), null, 0);

        View x = inflater.inflate(R.layout.fragment_calls_list, container, false);

        mListView = (ListView) x.findViewById(R.id.calls_listview);
        mListView.setAdapter(mCallAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Integer id = cursor.getInt(cursor.getColumnIndex(LikeWorkContract.CallEntry._ID));
                    Uri itemUri = LikeWorkContract.CallEntry.buildCallID(id);
                    mListener.OnItemSelected(itemUri);
                }
                mPosition = position;
            }
        });

        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("Звонки заботы");

        return x;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CALL_LOADER, null, this);
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