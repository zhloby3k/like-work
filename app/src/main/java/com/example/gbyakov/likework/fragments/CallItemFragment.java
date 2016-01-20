package com.example.gbyakov.likework.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CallItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener,
        DialogInterface.OnClickListener {

    public static final String CALL_URI = "URI";
    private static final int CALL_LOADER = 0;
    private static final int QUESTIONS_LOADER = 1;
    private static final int ANSWERS_LOADER = 2;

    private static final String[] CALL_COLUMNS = {
            LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry._ID,
            LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry.COLUMN_ID_1C,
            LikeWorkContract.CallEntry.COLUMN_DATE,
            LikeWorkContract.CallEntry.COLUMN_REASON,
            LikeWorkContract.CallEntry.COLUMN_SUM,
            LikeWorkContract.CallEntry.COLUMN_TYPE,
            "Client."+LikeWorkContract.ClientEntry.COLUMN_NAME + " ClientName",
            LikeWorkContract.CarEntry.COLUMN_BRAND,
            LikeWorkContract.CarEntry.COLUMN_MODEL,
            LikeWorkContract.CarEntry.COLUMN_REGNUMBER,
    };

    private static final String[] QUESTION_COLUMNS = {
            LikeWorkContract.QuestionEntry.TABLE_NAME + "." + LikeWorkContract.QuestionEntry.COLUMN_ID_1C,
            LikeWorkContract.QuestionEntry.COLUMN_NAME
    };

    private TextView mClientView;
    private TextView mCarView;
    private TextView mCommentView;
    private TextView mTypeView;
    private TextView mSumView;
    private LinearLayout mContainer;
    private View mQuestion;

    private Uri mUri;
    private String mDocId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(CALL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_call_item, container, false);
        mClientView = (TextView) rootView.findViewById(R.id.call_client);
        mCarView = (TextView) rootView.findViewById(R.id.call_car);
        mCommentView = (TextView) rootView.findViewById(R.id.call_comment);
        mTypeView = (TextView) rootView.findViewById(R.id.call_type);
        mSumView = (TextView) rootView.findViewById(R.id.call_sum);

        mContainer = (LinearLayout) rootView.findViewById(R.id.questions_container);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CALL_LOADER, null, this);
        getLoaderManager().initLoader(QUESTIONS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( mUri == null ) {
            return null;
        } else if ( id == CALL_LOADER ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    CALL_COLUMNS,
                    null,
                    null,
                    null
            );
        } else if ( id == QUESTIONS_LOADER ) {
            String callID = LikeWorkContract.CallEntry.getIDFromUri(mUri);
            return new CursorLoader(
                    getActivity(),
                    LikeWorkContract.QuestionEntry.buildInterviewUri(callID),
                    QUESTION_COLUMNS,
                    null,
                    null,
                    null
            );
        } else if ( id == ANSWERS_LOADER ) {
            String qID = (String) mQuestion.getTag();
            return new CursorLoader(
                    getActivity(),
                    LikeWorkContract.AnswerEntry.buildQuestionUri(qID),
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CALL_LOADER && data != null && data.moveToFirst()) {

            String carBrand  = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_BRAND));
            String carModel = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_MODEL));
            String carRegNum = data.getString(data.getColumnIndex(LikeWorkContract.CarEntry.COLUMN_REGNUMBER));
            mCarView.setText(carBrand + " " + carModel + " - " + carRegNum);

            String client    = data.getString(data.getColumnIndex("ClientName"));
            mClientView.setText(client);

            double sum       = data.getDouble(data.getColumnIndex(LikeWorkContract.CallEntry.COLUMN_SUM));
            DecimalFormatSymbols unusualSymbols = new DecimalFormatSymbols();
            unusualSymbols.setDecimalSeparator('.');
            unusualSymbols.setGroupingSeparator(' ');

            DecimalFormat myFormatter = new DecimalFormat("###,##0.00", unusualSymbols);
            myFormatter.setGroupingSize(3);
            mSumView.setText(myFormatter.format(sum));

            String type    = data.getString(data.getColumnIndex(LikeWorkContract.CallEntry.COLUMN_TYPE));
            mTypeView.setText(type);

            String comment = data.getString(data.getColumnIndex(LikeWorkContract.CallEntry.COLUMN_REASON));
            mCommentView.setText(comment);

            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if (actionBar != null) actionBar.setTitle("Звонок заботы");

        } else if (loader.getId() == QUESTIONS_LOADER && data != null) {

            LayoutInflater ltInflater = getLayoutInflater(null);

            while (data.moveToNext()) {

                View element = ltInflater.inflate(
                        R.layout.question_item, null, false);

                String qID = data.getString(data.getColumnIndex(LikeWorkContract.QuestionEntry.COLUMN_ID_1C));

                element.setTag(qID);
                element.setOnClickListener(this);

                TextView qHeader= (TextView) element.findViewById(R.id.question_header);
                String qName = data.getString(data.getColumnIndex(LikeWorkContract.QuestionEntry.COLUMN_NAME));
                qHeader.setText(qName);

                TextView qAnswer = (TextView) element.findViewById(R.id.question_answer);
                qAnswer.setText("");

                mContainer.addView(element);

            }

        } else if (loader.getId() == ANSWERS_LOADER && data != null) {

            TextView qHeader= (TextView) mQuestion.findViewById(R.id.question_header);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(qHeader.getText());
            builder.setCursor(data, this, LikeWorkContract.AnswerEntry.COLUMN_NAME);

            AlertDialog dialog = builder.create();
            dialog.show();

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {

        mQuestion = v;
        getLoaderManager().restartLoader(ANSWERS_LOADER, null, this);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        TextView qAnswer = (TextView) mQuestion.findViewById(R.id.question_answer);

        ListView lv = ((AlertDialog) dialog).getListView();
        Cursor cursor = ((SimpleCursorAdapter) lv.getAdapter()).getCursor();
        cursor.moveToPosition(which);

        String aName = cursor.getString(cursor.getColumnIndex(LikeWorkContract.AnswerEntry.COLUMN_NAME));
        String aID   = cursor.getString(cursor.getColumnIndex(LikeWorkContract.AnswerEntry.COLUMN_ID_1C));
        qAnswer.setText(aName);
        qAnswer.setTag(aID);

    }
}
