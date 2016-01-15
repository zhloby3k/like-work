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
import com.example.gbyakov.likework.adapters.OperationAdapter;
import com.example.gbyakov.likework.data.LikeWorkContract;

public class OperatiosListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mDocId;
    private OperationAdapter mOperationAdapter;
    private ListView mListView;

    private int mPosition = ListView.INVALID_POSITION;
    private static final int OPERATION_LOADER = 0;

    private static final String[] OPERATION_COLUMNS = {
            LikeWorkContract.OperationEntry.TABLE_NAME + "." + LikeWorkContract.OperationEntry._ID,
            LikeWorkContract.OperationEntry.COLUMN_LINENUM,
            LikeWorkContract.OperationEntry.COLUMN_NAME,
            LikeWorkContract.OperationEntry.COLUMN_CODE_1C,
            LikeWorkContract.OperationEntry.COLUMN_AMOUNT,
            LikeWorkContract.OperationEntry.COLUMN_STATUS,
            LikeWorkContract.OperationEntry.COLUMN_SUM
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mDocId = arguments.getString("id_1c");
        }

        mOperationAdapter = new OperationAdapter(getActivity(), null, 0);

        View x = inflater.inflate(R.layout.fragment_operations_list, container, false);

        mListView = (ListView) x.findViewById(R.id.operations_listview);
        mListView.setAdapter(mOperationAdapter);

        return x;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(OPERATION_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                LikeWorkContract.OperationEntry.buildDocUri(mDocId),
                OPERATION_COLUMNS,
                null,
                null,
                LikeWorkContract.OperationEntry.COLUMN_LINENUM);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mOperationAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mOperationAdapter.swapCursor(null);
    }
}
