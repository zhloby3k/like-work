package com.example.gbyakov.likework.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.adapters.StateAdapter;
import com.example.gbyakov.likework.data.LikeWorkContract;

public class StatesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private String mDocId;
    private StateAdapter mStateAdapter;
    private ListView mListView;

    private int mPosition = ListView.INVALID_POSITION;
    private static final int STATES_LOADER = 0;

    private static final String[] STATE_COLUMNS = {
            LikeWorkContract.StateEntry.TABLE_NAME + "." + LikeWorkContract.StateEntry._ID,
            LikeWorkContract.StateEntry.COLUMN_DATE,
            LikeWorkContract.StateEntry.COLUMN_USER,
            LikeWorkContract.StatusEntry.COLUMN_NAME
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mDocId = arguments.getString("id_1c");
        }

        mStateAdapter = new StateAdapter(getActivity(), null, 0);

        View x = inflater.inflate(R.layout.fragment_states_list, container, false);

        mListView = (ListView) x.findViewById(R.id.states_listview);
        mListView.setAdapter(mStateAdapter);

        return x;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(STATES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                LikeWorkContract.StateEntry.buildDocUri(mDocId),
                STATE_COLUMNS,
                null,
                null,
                LikeWorkContract.StateEntry.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mStateAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStateAdapter.swapCursor(null);
    }

}
