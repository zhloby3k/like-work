package com.example.gbyakov.likework.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.gbyakov.likework.data.LikeWorkContract.CallEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.CarEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.ClientEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.OrderEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.RecordEntry;
import com.example.gbyakov.likework.data.LikeWorkContract.StatusEntry;

public class LikeWorkDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "likework.db";

    public LikeWorkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_ORDER_TABLE = "CREATE TABLE " + OrderEntry.TABLE_NAME + " (" +
                OrderEntry._ID + " INTEGER PRIMARY KEY, " +
                OrderEntry.COLUMN_ID_1C + " TEXT UNIQUE NOT NULL, " +
                OrderEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                OrderEntry.COLUMN_NUMBER + " TEXT NOT NULL, " +
                OrderEntry.COLUMN_CAR_ID + " TEXT NOT NULL, " +
                OrderEntry.COLUMN_CLIENT_ID + " TEXT NOT NULL, " +
                OrderEntry.COLUMN_CUSTOMER_ID + " TEXT NOT NULL, " +
                OrderEntry.COLUMN_STATUS_ID + " INTEGER NOT NULL, " +
                OrderEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                OrderEntry.COLUMN_REASON + " TEXT NOT NULL, " +
                OrderEntry.COLUMN_COMMENT + " TEXT NOT NULL, " +
                OrderEntry.COLUMN_SUM + " REAL NOT NULL " +
                " );";

        final String SQL_CREATE_RECORD_TABLE = "CREATE TABLE " + RecordEntry.TABLE_NAME + " (" +
                RecordEntry._ID + " INTEGER PRIMARY KEY," +
                RecordEntry.COLUMN_ID_1C + " TEXT UNIQUE NOT NULL, " +
                RecordEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                RecordEntry.COLUMN_NUMBER + " TEXT NOT NULL, " +
                RecordEntry.COLUMN_CAR_ID + " TEXT NOT NULL, " +
                RecordEntry.COLUMN_CLIENT_ID + " TEXT NOT NULL, " +
                RecordEntry.COLUMN_CUSTOMER_ID + " TEXT NOT NULL, " +
                RecordEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                RecordEntry.COLUMN_REASON + " TEXT NOT NULL, " +
                RecordEntry.COLUMN_SUM + " REAL NOT NULL, " +
                RecordEntry.COLUMN_DONE + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_CALL_TABLE = "CREATE TABLE " + CallEntry.TABLE_NAME + " (" +
                CallEntry._ID + " INTEGER PRIMARY KEY," +
                CallEntry.COLUMN_ID_1C + " TEXT UNIQUE NOT NULL, " +
                CallEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                CallEntry.COLUMN_CAR_ID + " TEXT NOT NULL, " +
                CallEntry.COLUMN_CLIENT_ID + " TEXT NOT NULL, " +
                CallEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                CallEntry.COLUMN_REASON + " TEXT NOT NULL, " +
                CallEntry.COLUMN_SUM + " REAL NOT NULL, " +
                CallEntry.COLUMN_INTERVIEW_ID + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_CAR_TABLE = "CREATE TABLE " + CarEntry.TABLE_NAME + " (" +
                CarEntry._ID + " INTEGER PRIMARY KEY," +
                CarEntry.COLUMN_ID_1C + " TEXT UNIQUE NOT NULL, " +
                CarEntry.COLUMN_BRAND + " TEXT NOT NULL, " +
                CarEntry.COLUMN_MODEL + " TEXT NOT NULL, " +
                CarEntry.COLUMN_REGNUMBER + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_CLIENT_TABLE = "CREATE TABLE " + ClientEntry.TABLE_NAME + " (" +
                ClientEntry._ID + " INTEGER PRIMARY KEY," +
                ClientEntry.COLUMN_ID_1C + " TEXT UNIQUE NOT NULL, " +
                ClientEntry.COLUMN_NAME + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_STATUS_TABLE = "CREATE TABLE " + StatusEntry.TABLE_NAME + " (" +
                StatusEntry._ID + " INTEGER PRIMARY KEY," +
                StatusEntry.COLUMN_ID_1C + " TEXT NOT NULL, " +
                StatusEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                StatusEntry.COLUMN_COLOR + " TEXT NOT NULL, " +
                StatusEntry.COLUMN_GROUP + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_ORDER_TABLE);
        db.execSQL(SQL_CREATE_RECORD_TABLE);
        db.execSQL(SQL_CREATE_CALL_TABLE);
        db.execSQL(SQL_CREATE_CAR_TABLE);
        db.execSQL(SQL_CREATE_CLIENT_TABLE);
        db.execSQL(SQL_CREATE_STATUS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE " + RecordEntry.TABLE_NAME + " ADD COLUMN " + RecordEntry.COLUMN_DONE + " INTEGER ");
        }
        else {
            db.execSQL("DROP TABLE IF EXISTS " + OrderEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RecordEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + CallEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + CarEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ClientEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + StatusEntry.TABLE_NAME);
            onCreate(db);
        }

    }
}
