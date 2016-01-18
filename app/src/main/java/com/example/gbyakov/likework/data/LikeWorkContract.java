package com.example.gbyakov.likework.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class LikeWorkContract {

    public static final String CONTENT_AUTHORITY = "com.example.gbyakov.likework";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ORDER   = "order";
    public static final String PATH_RECORD  = "record";
    public static final String PATH_CALL    = "call";
    public static final String PATH_CAR     = "car";
    public static final String PATH_CLIENT  = "client";
    public static final String PATH_STATUS  = "status";
    public static final String PATH_STATE   = "state";
    public static final String PATH_PART    = "part";
    public static final String PATH_OPERATION  = "operation";

    public static final class OrderEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;

        public static final String TABLE_NAME           = "orders";

        public static final String COLUMN_ID_1C         = "_id_1c";
        public static final String COLUMN_DATE          = "date";
        public static final String COLUMN_NUMBER        = "number";
        public static final String COLUMN_CAR_ID        = "car_id";
        public static final String COLUMN_CUSTOMER_ID   = "customer_id";
        public static final String COLUMN_CLIENT_ID     = "client_id";
        public static final String COLUMN_STATUS_ID     = "status_id";
        public static final String COLUMN_TYPE          = "type";
        public static final String COLUMN_REASON        = "reason";
        public static final String COLUMN_COMMENT       = "comment";
        public static final String COLUMN_SUM           = "sum";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildOrderID(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }

        public static Uri buildOrderWithGroups() {
            return CONTENT_URI.buildUpon().appendPath("withgroups").build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class RecordEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECORD).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECORD;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECORD;

        public static final String TABLE_NAME           = "records";

        public static final String COLUMN_ID_1C         = "_id_1c";
        public static final String COLUMN_DATE          = "date";
        public static final String COLUMN_NUMBER        = "number";
        public static final String COLUMN_CAR_ID        = "car_id";
        public static final String COLUMN_CUSTOMER_ID   = "customer_id";
        public static final String COLUMN_CLIENT_ID     = "client_id";
        public static final String COLUMN_TYPE          = "type";
        public static final String COLUMN_COMMENT       = "comment";
        public static final String COLUMN_REASON        = "reason";
        public static final String COLUMN_SUM           = "sum";
        public static final String COLUMN_DONE          = "done";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildRecordID(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }

        public static Uri buildOrderDates() {
            return CONTENT_URI.buildUpon().appendPath("dates").build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class CallEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CALL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALL;

        public static final String TABLE_NAME           = "calls";

        public static final String COLUMN_ID_1C         = "_id_1c";
        public static final String COLUMN_DATE          = "date";
        public static final String COLUMN_CAR_ID        = "car_id";
        public static final String COLUMN_CLIENT_ID     = "client_id";
        public static final String COLUMN_TYPE          = "type";
        public static final String COLUMN_REASON        = "reason";
        public static final String COLUMN_SUM           = "sum";
        public static final String COLUMN_INTERVIEW_ID  = "interview_id";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCallID(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }

        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class CarEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAR;

        public static final String TABLE_NAME           = "cars";

        public static final String COLUMN_ID_1C         = "_id_1c";
        public static final String COLUMN_BRAND         = "brand";
        public static final String COLUMN_MODEL         = "model";
        public static final String COLUMN_REGNUMBER     = "regnumber";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ClientEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLIENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENT;

        public static final String TABLE_NAME           = "clients";

        public static final String COLUMN_ID_1C         = "_id_1c";
        public static final String COLUMN_NAME          = "client_name";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class StatusEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATUS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENT;

        public static final String TABLE_NAME           = "statuses";

        public static final String COLUMN_ID_1C         = "_id_1c";
        public static final String COLUMN_NAME          = "status_name";
        public static final String COLUMN_COLOR         = "color";
        public static final String COLUMN_GROUP         = "group_name";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class StateEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STATE;

        public static final String TABLE_NAME           = "states";

        public static final String COLUMN_DOC_ID_1C     = "doc_id";
        public static final String COLUMN_DATE          = "date";
        public static final String COLUMN_STATUS        = "status_id";
        public static final String COLUMN_USER          = "user_name";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildDocUri(String docId) {
            return CONTENT_URI.buildUpon().appendPath(docId).build();
        }
        public static String getDocFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PartEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PART).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PART;

        public static final String TABLE_NAME           = "parts";

        public static final String COLUMN_CODE_1C       = "code_1c";
        public static final String COLUMN_DOC_ID_1C     = "doc_id";
        public static final String COLUMN_LINENUM       = "linenumber";
        public static final String COLUMN_CATNUM        = "catnumber";
        public static final String COLUMN_NAME          = "name";
        public static final String COLUMN_AMOUNT        = "amount";
        public static final String COLUMN_SUM           = "sum";
        public static final String COLUMN_STATUS        = "status";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildDocUri(String docId) {
            return CONTENT_URI.buildUpon().appendPath(docId).build();
        }
        public static String getDocFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class OperationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_OPERATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OPERATION;

        public static final String TABLE_NAME           = "operations";

        public static final String COLUMN_CODE_1C       = "code_1c";
        public static final String COLUMN_DOC_ID_1C     = "doc_id";
        public static final String COLUMN_LINENUM       = "linenumber";
        public static final String COLUMN_NAME          = "name";
        public static final String COLUMN_AMOUNT        = "amount";
        public static final String COLUMN_SUM           = "sum";
        public static final String COLUMN_STATUS        = "status";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildDocUri(String docId) {
            return CONTENT_URI.buildUpon().appendPath(docId).build();
        }
        public static String getDocFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

}
