package com.example.gbyakov.likework.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.gbyakov.likework.data.LikeWorkContract.CallEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.CarEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.ClientEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.OrderEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.RecordEntry;
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
    static final int RECORD_WITH_DATE   = 210;
    static final int CALL               = 300;
    static final int CALL_ID            = 301;

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
        matcher.addURI(authority, LikeWorkContract.PATH_RECORD + "/*",          RECORD_WITH_DATE);
        matcher.addURI(authority, LikeWorkContract.PATH_RECORD + "/#",          RECORD_ID);
        matcher.addURI(authority, LikeWorkContract.PATH_CALL,                   CALL);
        matcher.addURI(authority, LikeWorkContract.PATH_CALL + "/#",            CALL_ID);

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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

}
