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
        public static final String COLUMN_REASON        = "reason";
        public static final String COLUMN_SUM           = "sum";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
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

        public static final String COLUMN_NAME          = "status_name";
        public static final String COLUMN_COLOR         = "color";
        public static final String COLUMN_GROUP         = "group_name";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
