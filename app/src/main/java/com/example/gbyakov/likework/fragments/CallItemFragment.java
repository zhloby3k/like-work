package com.example.gbyakov.likework.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gbyakov.likework.MainActivity;
import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.data.LikeWorkContract;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class CallItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener,
        DialogInterface.OnClickListener {

    public static final String CALL_URI = "URI";
    private static final int CALL_LOADER = 0;
    private static final int QUESTIONS_LOADER = 1;
    private static final int ANSWERS_LOADER = 2;
    private static final int PHONES_LOADER = 3;

    private static final String[] CALL_COLUMNS = {
            LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry._ID,
            LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry.COLUMN_ID_1C,
            LikeWorkContract.CallEntry.COLUMN_DATE,
            LikeWorkContract.CallEntry.COLUMN_REASON,
            LikeWorkContract.CallEntry.COLUMN_SUM,
            LikeWorkContract.CallEntry.COLUMN_TYPE,
            LikeWorkContract.CallEntry.COLUMN_INTERVIEW_ID,
            "Client."+LikeWorkContract.ClientEntry.COLUMN_NAME + " ClientName",
            "Client."+LikeWorkContract.ClientEntry.COLUMN_ID_1C + " ClientID",
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
    private EditText mEditComment;
    private LinearLayout mContainer;
    private View mQuestion;
    private ArrayList<View> qList;

    private Uri mUri;
    private String mCallID;
    private String mInterviewID;
    private String mClientID;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.call_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_call:

                Boolean errors = false;
                for (View v:qList) {
                    TextView qAnswer = (TextView) v.findViewById(R.id.question_answer);
                    if (qAnswer.getTag() == null) {
                        errors = true;
                    }
                }
                if (errors) {
                    Toast.makeText(getActivity(), "Не заполнены ответы на некоторые вопросы",
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                for (View v:qList) {
                    TextView qAnswer = (TextView) v.findViewById(R.id.question_answer);

                    ContentValues newValues = new ContentValues();
                    newValues.put(LikeWorkContract.ReplyEntry.COLUMN_CALL_ID, mCallID);
                    newValues.put(LikeWorkContract.ReplyEntry.COLUMN_INTERVIEW_ID, mInterviewID);
                    newValues.put(LikeWorkContract.ReplyEntry.COLUMN_QUESTION_ID, (String) v.getTag());
                    newValues.put(LikeWorkContract.ReplyEntry.COLUMN_ANSWER_ID, (String) qAnswer.getTag());
                    newValues.put(LikeWorkContract.ReplyEntry.COLUMN_COMMENT, (qList.indexOf(v) != 0) ? "":mEditComment.getText().toString());

                    getContext().getContentResolver().insert(LikeWorkContract.ReplyEntry.CONTENT_URI, newValues);
                }

                ContentValues newValues = new ContentValues();
                newValues.put(LikeWorkContract.CallEntry.COLUMN_DONE, 1);

                String selection = LikeWorkContract.CallEntry.TABLE_NAME + "." +
                                     LikeWorkContract.CallEntry.COLUMN_ID_1C + " = ?";
                String[] selectionArgs = {mCallID};

                getContext().getContentResolver().update(LikeWorkContract.CallEntry.CONTENT_URI, newValues, selection, selectionArgs);

                getFragmentManager().popBackStackImmediate();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(CALL_URI);
        }

        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_call_item, container, false);
        mClientView = (TextView) rootView.findViewById(R.id.call_client);
        mCarView = (TextView) rootView.findViewById(R.id.call_car);
        mCommentView = (TextView) rootView.findViewById(R.id.call_comment);
        mTypeView = (TextView) rootView.findViewById(R.id.call_type);
        mSumView = (TextView) rootView.findViewById(R.id.call_sum);
        mEditComment = (EditText) rootView.findViewById(R.id.call_comment_input);

        mContainer = (LinearLayout) rootView.findViewById(R.id.questions_container);

        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle("Звонок заботы");

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
        } else if ( id == PHONES_LOADER ) {
            return new CursorLoader(
                    getActivity(),
                    LikeWorkContract.AnswerEntry.buildQuestionUri(mClientID),
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

            mCallID = data.getString(data.getColumnIndex(LikeWorkContract.CallEntry.COLUMN_ID_1C));
            mInterviewID = data.getString(data.getColumnIndex(LikeWorkContract.CallEntry.COLUMN_INTERVIEW_ID));
            mClientID = data.getString(data.getColumnIndex("ClientID"));

            getLoaderManager().initLoader(PHONES_LOADER, null, this);

        } else if (loader.getId() == QUESTIONS_LOADER && data != null) {

            LayoutInflater ltInflater = getLayoutInflater(null);
            qList = new ArrayList<>();

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

                qList.add(element);

                mContainer.addView(element);

            }

        } else if (loader.getId() == ANSWERS_LOADER && data != null) {

            TextView qHeader= (TextView) mQuestion.findViewById(R.id.question_header);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(qHeader.getText());
            builder.setCursor(data, this, LikeWorkContract.AnswerEntry.COLUMN_NAME);

            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (loader.getId() == PHONES_LOADER && data != null) {

            FloatingActionMenu fabMenu = ((MainActivity) getActivity()).fabMenu;
            //fabMenu.removeAllMenuButtons();

            while (data.moveToNext()) {

                String pName    = data.getString(data.getColumnIndex(LikeWorkContract.PhoneEntry.COLUMN_NAME));
                String pDescr   = data.getString(data.getColumnIndex(LikeWorkContract.PhoneEntry.COLUMN_DESCR));
                String pNumber  = data.getString(data.getColumnIndex(LikeWorkContract.PhoneEntry.COLUMN_PHONE));

                FloatingActionButton fabCall = new FloatingActionButton(getActivity());
                fabCall.setButtonSize(FloatingActionButton.SIZE_MINI);
                fabCall.setLabelText(pName + ": " + pDescr);
                fabCall.setImageResource(R.drawable.ic_phone_in_talk);
                fabCall.setColorNormal(getResources().getColor(R.color.colorAccent));
                fabCall.setTag(pNumber);
                fabMenu.addMenuButton(fabCall);
                fabCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + v.getTag().toString()));
                        startActivity(intent);
                    }
                });

            }
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

        ImageView image = (ImageView) mQuestion.findViewById(R.id.question_logo);
        TransitionDrawable drawable = (TransitionDrawable) image.getDrawable();
        drawable.setCrossFadeEnabled(true);
        drawable.startTransition(500);

    }
}
