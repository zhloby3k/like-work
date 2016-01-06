package com.example.gbyakov.likework.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.gbyakov.likework.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createCarValues() {

        ContentValues testValues = new ContentValues();
        testValues.put(LikeWorkContract.CarEntry.COLUMN_ID_1C,      "0000");
        testValues.put(LikeWorkContract.CarEntry.COLUMN_MODEL,      "COROLLA");
        testValues.put(LikeWorkContract.CarEntry.COLUMN_BRAND,      "TOYOTA");
        testValues.put(LikeWorkContract.CarEntry.COLUMN_REGNUMBER,  "Y 924 YD 159");

        return testValues;
    }

    static long insertCarValues(Context context) {

        LikeWorkDBHelper dbHelper = new LikeWorkDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createCarValues();

        long carRowId;
        carRowId = db.insert(LikeWorkContract.CarEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert car Values", carRowId != -1);

        return carRowId;
    }

    static ContentValues createClientValues() {

        ContentValues testValues = new ContentValues();
        testValues.put(LikeWorkContract.ClientEntry.COLUMN_ID_1C,   "0000");
        testValues.put(LikeWorkContract.ClientEntry.COLUMN_NAME,    "Иван");

        return testValues;
    }

    static long insertClientValues(Context context) {

        LikeWorkDBHelper dbHelper = new LikeWorkDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createClientValues();

        long clientRowId;
        clientRowId = db.insert(LikeWorkContract.ClientEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert client Values", clientRowId != -1);

        return clientRowId;
    }

    static ContentValues createStatusValues() {

        ContentValues testValues = new ContentValues();
        testValues.put(LikeWorkContract.StatusEntry.COLUMN_NAME,    "В работе");
        testValues.put(LikeWorkContract.StatusEntry.COLUMN_COLOR,   "1111");
        testValues.put(LikeWorkContract.StatusEntry.COLUMN_GROUP,   "Запись");

        return testValues;
    }

    static long insertStatusValues(Context context) {

        LikeWorkDBHelper dbHelper = new LikeWorkDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createStatusValues();

        long statusRowId;
        statusRowId = db.insert(LikeWorkContract.StatusEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert status Values", statusRowId != -1);

        return statusRowId;
    }

    static ContentValues createOrderValues() {

        ContentValues testValues = new ContentValues();
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_ID_1C,        "0000");
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_DATE,         10000);
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_NUMBER,       "3333");
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_CAR_ID,       "0000");
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_CLIENT_ID,    "0000");
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_CUSTOMER_ID,  "0000");
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_STATUS_ID,    1);
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_TYPE,         "----");
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_REASON,       "++++");
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_COMMENT,      "+");
        testValues.put(LikeWorkContract.OrderEntry.COLUMN_SUM, 11000);
        return testValues;
    }

    static long insertOrderValues(Context context) {

        LikeWorkDBHelper dbHelper = new LikeWorkDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long CarRowId       = TestUtilities.insertCarValues(context);
        long ClientRowId    = TestUtilities.insertClientValues(context);
        long StatusRowId    = TestUtilities.insertStatusValues(context);

        ContentValues testValues = TestUtilities.createOrderValues();
        long orderRowId = db.insert(LikeWorkContract.OrderEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert order Values", orderRowId != -1);

        return orderRowId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
