package com.example.gbyakov.likework.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.gbyakov.likework.data.LikeWorkContract.AnswerEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.CallEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.CarEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.ClientEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.OperationEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.OrderEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.PartEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.QuestionEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.RecordEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.ReplyEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.StateEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.StatusEntry;

import java.util.ArrayList;

public class LikeWorkProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private LikeWorkDBHelper mOpenHelper;

    static final int ORDER              = 100;
    static final int ORDER_ID           = 101;
    static final int ORDER_WITH_GROUPS  = 110;
    static final int RECORD             = 200;
    static final int RECORD_ID          = 201;
    static final int RECORD_DATES       = 210;
    static final int CALL               = 300;
    static final int CALL_ID            = 301;

    static final int STATE              = 400;
    static final int STATE_OF_DOC       = 401;
    static final int PART               = 410;
    static final int PART_OF_DOC        = 411;
    static final int OPERATION          = 420;
    static final int OPERATION_OF_DOC   = 421;
    static final int QUESTION           = 430;
    static final int QUESTION_OF_DOC    = 431;
    static final int ANSWER             = 440;
    static final int ANSWER_OF_QUESTION = 441;
    static final int REPLY              = 450;

    static final int CAR                = 1;
    static final int CLIENT             = 2;
    static final int STATUS             = 3;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LikeWorkContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, LikeWorkContract.PATH_ORDER,                  ORDER);
        matcher.addURI(authority, LikeWorkContract.PATH_ORDER + "/#",           ORDER_ID);
        matcher.addURI(authority, LikeWorkContract.PATH_ORDER + "/withgroups",  ORDER_WITH_GROUPS);
        matcher.addURI(authority, LikeWorkContract.PATH_RECORD,                 RECORD);
        matcher.addURI(authority, LikeWorkContract.PATH_RECORD + "/dates",      RECORD_DATES);
        matcher.addURI(authority, LikeWorkContract.PATH_RECORD + "/#",          RECORD_ID);
        matcher.addURI(authority, LikeWorkContract.PATH_CALL,                   CALL);
        matcher.addURI(authority, LikeWorkContract.PATH_CALL + "/#",            CALL_ID);

        matcher.addURI(authority, LikeWorkContract.PATH_STATE,                  STATE);
        matcher.addURI(authority, LikeWorkContract.PATH_STATE + "/*",           STATE_OF_DOC);
        matcher.addURI(authority, LikeWorkContract.PATH_PART,                   PART);
        matcher.addURI(authority, LikeWorkContract.PATH_PART + "/*",            PART_OF_DOC);
        matcher.addURI(authority, LikeWorkContract.PATH_OPERATION,              OPERATION);
        matcher.addURI(authority, LikeWorkContract.PATH_OPERATION + "/*",       OPERATION_OF_DOC);
        matcher.addURI(authority, LikeWorkContract.PATH_QUESTION,               QUESTION);
        matcher.addURI(authority, LikeWorkContract.PATH_QUESTION + "/*",        QUESTION_OF_DOC);
        matcher.addURI(authority, LikeWorkContract.PATH_ANSWER,                 ANSWER);
        matcher.addURI(authority, LikeWorkContract.PATH_ANSWER + "/*",          ANSWER_OF_QUESTION);
        matcher.addURI(authority, LikeWorkContract.PATH_REPLY,                  REPLY);

        matcher.addURI(authority, LikeWorkContract.PATH_CAR,                    CAR);
        matcher.addURI(authority, LikeWorkContract.PATH_CLIENT,                 CLIENT);
        matcher.addURI(authority, LikeWorkContract.PATH_STATUS,                 STATUS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new LikeWorkDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case ORDER:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        OrderEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ORDER_ID:
            {
                SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
                qBuilder.setTables(OrderEntry.TABLE_NAME +
                                " LEFT JOIN " + CarEntry.TABLE_NAME +
                                " ON " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_CAR_ID +
                                " = " + CarEntry.TABLE_NAME + "." + CarEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Client" +
                                " ON " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_CLIENT_ID +
                                " = Client." + ClientEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Customer" +
                                " ON " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_CUSTOMER_ID +
                                " = Customer." + ClientEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + StatusEntry.TABLE_NAME +
                                " ON " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_STATUS_ID +
                                " = " + StatusEntry.TABLE_NAME + "." + StatusEntry.COLUMN_ID_1C
                );

                selection = OrderEntry.TABLE_NAME + "." + OrderEntry._ID + " = ?";
                selectionArgs = new String[] {OrderEntry.getIDFromUri(uri)};

                retCursor = qBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ORDER_WITH_GROUPS:
            {
                SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
                qBuilder.setTables(OrderEntry.TABLE_NAME +
                                " LEFT JOIN " + CarEntry.TABLE_NAME +
                                " ON " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_CAR_ID +
                                " = " + CarEntry.TABLE_NAME + "." + CarEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Client" +
                                " ON " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_CLIENT_ID +
                                " = Client." + ClientEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Customer" +
                                " ON " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_CUSTOMER_ID +
                                " = Customer." + ClientEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + StatusEntry.TABLE_NAME +
                                " ON " + OrderEntry.TABLE_NAME + "." + OrderEntry.COLUMN_STATUS_ID +
                                " = " + StatusEntry.TABLE_NAME + "." + StatusEntry.COLUMN_ID_1C
                );

                ArrayList<String> projectionListItems = new ArrayList<>();
                ArrayList<String> projectionListGroup = new ArrayList<>(projectionListItems);
                for (int i=0; i<projection.length; i++) {
                    projectionListItems.add(projection[i]);
                    projectionListGroup.add((projection[i].equals(StatusEntry.COLUMN_GROUP)) ? StatusEntry.COLUMN_GROUP : "null");
                }

                projectionListItems.add("0 isGroup");
                String[] projectionItems = projectionListItems.toArray(new String[projectionListItems.size()]);
                String qItems = qBuilder.buildQuery(projectionItems, selection, null, null, null, null);

                projectionListGroup.add("1");
                String[] projectionGroup = projectionListGroup.toArray(new String[projectionListGroup.size()]);
                String qGroups = qBuilder.buildQuery(projectionGroup, selection, StatusEntry.COLUMN_GROUP, null, null, null);

                String[] subQueries = {qItems, qGroups};

                String qUnion = qBuilder.buildUnionQuery(subQueries, sortOrder + ", isGroup DESC", null);

                retCursor = mOpenHelper.getReadableDatabase().rawQuery(qUnion, selectionArgs);
                break;
            }
            case RECORD:
            {
                SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
                qBuilder.setTables(RecordEntry.TABLE_NAME +
                                " LEFT JOIN " + CarEntry.TABLE_NAME +
                                " ON " + RecordEntry.TABLE_NAME + "." + RecordEntry.COLUMN_CAR_ID +
                                " = " + CarEntry.TABLE_NAME + "." + CarEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Client" +
                                " ON " + RecordEntry.TABLE_NAME + "." + RecordEntry.COLUMN_CLIENT_ID +
                                " = Client." + ClientEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Customer" +
                                " ON " + RecordEntry.TABLE_NAME + "." + RecordEntry.COLUMN_CUSTOMER_ID +
                                " = Customer." + ClientEntry.COLUMN_ID_1C
                );

                retCursor = qBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RECORD_ID:
            {
                SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
                qBuilder.setTables(RecordEntry.TABLE_NAME +
                                " LEFT JOIN " + CarEntry.TABLE_NAME +
                                " ON " + RecordEntry.TABLE_NAME + "." + RecordEntry.COLUMN_CAR_ID +
                                " = " + CarEntry.TABLE_NAME + "." + CarEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Client" +
                                " ON " + RecordEntry.TABLE_NAME + "." + RecordEntry.COLUMN_CLIENT_ID +
                                " = Client." + ClientEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Customer" +
                                " ON " + RecordEntry.TABLE_NAME + "." + RecordEntry.COLUMN_CUSTOMER_ID +
                                " = Customer." + ClientEntry.COLUMN_ID_1C
                );

                selection = RecordEntry.TABLE_NAME + "." + RecordEntry._ID + " = ?";
                selectionArgs = new String[] {RecordEntry.getIDFromUri(uri)};

                retCursor = qBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case RECORD_DATES: {

                String groupBy = "date("+LikeWorkContract.RecordEntry.COLUMN_DATE+"/1000, \"unixepoch\")";
                String[] newprojection = {groupBy + " " + LikeWorkContract.RecordEntry.COLUMN_DATE};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        RecordEntry.TABLE_NAME,
                        newprojection,
                        selection,
                        selectionArgs,
                        groupBy,
                        null,
                        groupBy
                );
                break;
            }
            case CALL:
            {
                SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
                qBuilder.setTables(CallEntry.TABLE_NAME +
                                " LEFT JOIN " + CarEntry.TABLE_NAME +
                                " ON " + CallEntry.TABLE_NAME + "." + CallEntry.COLUMN_CAR_ID +
                                " = " + CarEntry.TABLE_NAME + "." + CarEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Client" +
                                " ON " + CallEntry.TABLE_NAME + "." + CallEntry.COLUMN_CLIENT_ID +
                                " = Client." + ClientEntry.COLUMN_ID_1C
                );

                retCursor = qBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CALL_ID:
            {
                SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
                qBuilder.setTables(CallEntry.TABLE_NAME +
                                " LEFT JOIN " + CarEntry.TABLE_NAME +
                                " ON " + CallEntry.TABLE_NAME + "." + CallEntry.COLUMN_CAR_ID +
                                " = " + CarEntry.TABLE_NAME + "." + CarEntry.COLUMN_ID_1C +
                                " LEFT JOIN " + ClientEntry.TABLE_NAME + " AS Client" +
                                " ON " + CallEntry.TABLE_NAME + "." + CallEntry.COLUMN_CLIENT_ID +
                                " = Client." + ClientEntry.COLUMN_ID_1C
                );

                selection = CallEntry.TABLE_NAME + "." + CallEntry._ID + " = ?";
                selectionArgs = new String[] {CallEntry.getIDFromUri(uri)};

                retCursor = qBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CAR: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CarEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CLIENT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ClientEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case STATUS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        StatusEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case STATE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        StateEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case STATE_OF_DOC: {

                SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
                qBuilder.setTables(StateEntry.TABLE_NAME +
                        " LEFT JOIN " + StatusEntry.TABLE_NAME +
                        " ON " + StateEntry.TABLE_NAME + "." + StateEntry.COLUMN_STATUS +
                        " = " + StatusEntry.TABLE_NAME + "." + StatusEntry.COLUMN_ID_1C
                );

                selection = StateEntry.TABLE_NAME + "." + StateEntry.COLUMN_DOC_ID_1C + " = ?";
                selectionArgs = new String[] {StateEntry.getDocFromUri(uri)};

                retCursor = qBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PART: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PartEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PART_OF_DOC: {
                selection = PartEntry.TABLE_NAME + "." + PartEntry.COLUMN_DOC_ID_1C + " = ?";
                selectionArgs = new String[] {PartEntry.getDocFromUri(uri)};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        PartEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case OPERATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        OperationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case OPERATION_OF_DOC: {
                selection = OperationEntry.TABLE_NAME + "." + OperationEntry.COLUMN_DOC_ID_1C + " = ?";
                selectionArgs = new String[] {OperationEntry.getDocFromUri(uri)};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        OperationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case QUESTION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuestionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case QUESTION_OF_DOC: {

                SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
                qBuilder.setTables(QuestionEntry.TABLE_NAME +
                                " INNER JOIN " + CallEntry.TABLE_NAME +
                                " ON " + CallEntry.TABLE_NAME + "." + CallEntry.COLUMN_INTERVIEW_ID +
                                " = " + QuestionEntry.TABLE_NAME + "." + QuestionEntry.COLUMN_INTERVIEW_ID
                );

                selection = CallEntry.TABLE_NAME + "." + CallEntry._ID + " = ?";
                selectionArgs = new String[] {QuestionEntry.getInterviewFromUri(uri)};

                retCursor = qBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ANSWER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AnswerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ANSWER_OF_QUESTION: {
                selection = AnswerEntry.TABLE_NAME + "." + AnswerEntry.COLUMN_QUESTION_ID + " = ?";
                selectionArgs = new String[] {AnswerEntry.getQuestionFromUri(uri)};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        AnswerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REPLY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ReplyEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (getContext() != null) retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ORDER:
                return OrderEntry.CONTENT_TYPE;
            case ORDER_ID:
                return OrderEntry.CONTENT_ITEM_TYPE;
            case RECORD:
                return RecordEntry.CONTENT_TYPE;
            case RECORD_ID:
                return RecordEntry.CONTENT_ITEM_TYPE;
            case CALL:
                return CallEntry.CONTENT_TYPE;
            case CALL_ID:
                return CallEntry.CONTENT_ITEM_TYPE;
            case CAR:
                return CarEntry.CONTENT_TYPE;
            case CLIENT:
                return ClientEntry.CONTENT_TYPE;
            case STATUS:
                return StatusEntry.CONTENT_TYPE;
            case STATE:
                return StateEntry.CONTENT_TYPE;
            case PART:
                return ClientEntry.CONTENT_TYPE;
            case OPERATION:
                return StatusEntry.CONTENT_TYPE;
            case QUESTION:
                return ClientEntry.CONTENT_TYPE;
            case ANSWER:
                return StatusEntry.CONTENT_TYPE;
            case REPLY:
                return ReplyEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ORDER: {
                long _id = db.insert(OrderEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = OrderEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case RECORD: {
                long _id = db.insert(RecordEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = RecordEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CALL: {
                long _id = db.insert(CallEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CallEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CAR: {
                long _id = db.insert(CarEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CarEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CLIENT: {
                long _id = db.insert(ClientEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ClientEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STATUS: {
                long _id = db.insert(StatusEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = StatusEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STATE: {
                long _id = db.insert(StateEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = StateEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PART: {
                long _id = db.insert(PartEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PartEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case OPERATION: {
                long _id = db.insert(OperationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = OperationEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case QUESTION: {
                long _id = db.insert(QuestionEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuestionEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ANSWER: {
                long _id = db.insert(AnswerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AnswerEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REPLY: {
                long _id = db.insert(ReplyEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ReplyEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match) {
            case ORDER:
                rowsDeleted = db.delete(OrderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RECORD:
                rowsDeleted = db.delete(RecordEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CALL:
                rowsDeleted = db.delete(CallEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CAR:
                rowsDeleted = db.delete(CarEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CLIENT:
                rowsDeleted = db.delete(ClientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STATUS:
                rowsDeleted = db.delete(StatusEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STATE:
                rowsDeleted = db.delete(StateEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PART:
                rowsDeleted = db.delete(PartEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case OPERATION:
                rowsDeleted = db.delete(OperationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case QUESTION:
                rowsDeleted = db.delete(QuestionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ANSWER:
                rowsDeleted = db.delete(AnswerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REPLY:
                rowsDeleted = db.delete(ReplyEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ORDER:
                rowsUpdated = db.update(OrderEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case RECORD:
                rowsUpdated = db.update(RecordEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CALL:
                rowsUpdated = db.update(CallEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CAR:
                rowsUpdated = db.update(CarEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CLIENT:
                rowsUpdated = db.update(ClientEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case STATUS:
                rowsUpdated = db.update(StatusEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case STATE:
                rowsUpdated = db.update(StateEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PART:
                rowsUpdated = db.update(PartEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case OPERATION:
                rowsUpdated = db.update(OperationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case QUESTION:
                rowsUpdated = db.update(QuestionEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ANSWER:
                rowsUpdated = db.update(AnswerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REPLY:
                rowsUpdated = db.update(ReplyEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

}
