package com.example.gbyakov.likework.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(LikeWorkDBHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(LikeWorkContract.OrderEntry.TABLE_NAME);
        tableNameHashSet.add(LikeWorkContract.RecordEntry.TABLE_NAME);
        tableNameHashSet.add(LikeWorkContract.CallEntry.TABLE_NAME);
        tableNameHashSet.add(LikeWorkContract.CarEntry.TABLE_NAME);
        tableNameHashSet.add(LikeWorkContract.ClientEntry.TABLE_NAME);
        tableNameHashSet.add(LikeWorkContract.StatusEntry.TABLE_NAME);

        mContext.deleteDatabase(LikeWorkDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new LikeWorkDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + LikeWorkContract.OrderEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<>();
        locationColumnHashSet.add(LikeWorkContract.OrderEntry._ID);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_CAR_ID);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_CLIENT_ID);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_COMMENT);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_CUSTOMER_ID);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_DATE);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_NUMBER);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_ID_1C);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_REASON);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_TYPE);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_SUM);
        locationColumnHashSet.add(LikeWorkContract.OrderEntry.COLUMN_STATUS_ID);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required order entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }
}
