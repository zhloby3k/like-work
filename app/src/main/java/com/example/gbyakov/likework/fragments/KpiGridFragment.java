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
import android.widget.GridView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.adapters.KpiAdapter;
import com.example.gbyakov.likework.data.LikeWorkContract;

public class KpiGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private KpiAdapter mKpiAdapter;
    private GridView mGridView;

    private static final int KPI_LOADER = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(KPI_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mKpiAdapter = new KpiAdapter(getActivity(), null, 0);

        View x = inflater.inflate(R.layout.fragment_kpi_grid, container, false);

        mGridView = (GridView) x.findViewById(R.id.kpi_gridView);
        mGridView.setAdapter(mKpiAdapter);

        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("KPI");

        return x;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                LikeWorkContract.KpiEntry.CONTENT_URI,
                null,
                null,
                null,
                LikeWorkContract.KpiEntry.COLUMN_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mKpiAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mKpiAdapter.swapCursor(null);
    }

}
