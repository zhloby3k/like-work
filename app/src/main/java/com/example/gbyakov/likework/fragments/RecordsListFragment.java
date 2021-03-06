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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gbyakov.likework.MainActivity;
import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.adapters.RecordAdapter;
import com.example.gbyakov.likework.data.LikeWorkContract;

public class RecordsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private RecordAdapter mRecordAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    OnItemSelectedListener mListener;

    private String mDate;
    private static final int RECORD_LOADER = 0;

    private static final String[] RECORD_COLUMNS = {
            LikeWorkContract.RecordEntry.TABLE_NAME + "." + LikeWorkContract.RecordEntry._ID,
            LikeWorkContract.RecordEntry.COLUMN_DATE,
            LikeWorkContract.RecordEntry.COLUMN_DONE,
            LikeWorkContract.RecordEntry.COLUMN_REASON,
            LikeWorkContract.CarEntry.COLUMN_MODEL,
            LikeWorkContract.CarEntry.COLUMN_REGNUMBER,
            "Client." + LikeWorkContract.ClientEntry.COLUMN_NAME + " " + LikeWorkContract.ClientEntry.COLUMN_NAME
    };

    public interface OnItemSelectedListener {
        void OnItemSelected(Uri itemUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        mDate = bundle.getString("date");

        mRecordAdapter = new RecordAdapter(getActivity(), null, 0);

        View x = inflater.inflate(R.layout.fragment_records_list, container, false);

        mListView = (ListView) x.findViewById(R.id.records_listview);
        mListView.setAdapter(mRecordAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Integer id = cursor.getInt(cursor.getColumnIndex(LikeWorkContract.RecordEntry._ID));
                    Uri itemUri = LikeWorkContract.RecordEntry.buildRecordID(id);
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

        return x;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECORD_LOADER, null, this);
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

        String selection = "date("+LikeWorkContract.RecordEntry.COLUMN_DATE+"/1000, \"unixepoch\")" + " = ?";
        String[] selectionArgs = {mDate};

        return new CursorLoader(getActivity(),
                LikeWorkContract.RecordEntry.CONTENT_URI,
                RECORD_COLUMNS,
                selection,
                selectionArgs,
                LikeWorkContract.RecordEntry.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecordAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecordAdapter.swapCursor(null);
    }

}