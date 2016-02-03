package com.example.gbyakov.likework.fragments;

import android.database.Cursor;
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
import android.widget.ListView;

import com.example.gbyakov.likework.MainActivity;
import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.adapters.PartAdapter;
import com.example.gbyakov.likework.data.LikeWorkContract;

public class PartsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mDocId;
    private PartAdapter mPartAdapter;
    private ListView mListView;

    private int mPosition = ListView.INVALID_POSITION;
    private static final int PARTS_LOADER = 0;

    private static final String[] PART_COLUMNS = {
            LikeWorkContract.PartEntry.TABLE_NAME + "." + LikeWorkContract.PartEntry._ID,
            LikeWorkContract.PartEntry.COLUMN_LINENUM,
            LikeWorkContract.PartEntry.COLUMN_NAME,
            LikeWorkContract.PartEntry.COLUMN_CODE_1C,
            LikeWorkContract.PartEntry.COLUMN_CATNUM,
            LikeWorkContract.PartEntry.COLUMN_AMOUNT,
            LikeWorkContract.PartEntry.COLUMN_STATUS,
            LikeWorkContract.PartEntry.COLUMN_SUM
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mDocId = arguments.getString("id_1c");
        }

        mPartAdapter = new PartAdapter(getActivity(), null, 0);

        View x = inflater.inflate(R.layout.fragment_parts_list, container, false);

        mListView = (ListView) x.findViewById(R.id.parts_listview);
        mListView.setAdapter(mPartAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                SwipeRefreshLayout mSwipeRefresh = ((MainActivity) getActivity()).mSwipeRefresh;
                mSwipeRefresh.setEnabled(false);
            }
        });

        return x;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PARTS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                LikeWorkContract.PartEntry.buildDocUri(mDocId),
                PART_COLUMNS,
                null,
                null,
                LikeWorkContract.PartEntry.COLUMN_LINENUM);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPartAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPartAdapter.swapCursor(null);
    }
}