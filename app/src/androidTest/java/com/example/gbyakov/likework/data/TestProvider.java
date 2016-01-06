package com.example.gbyakov.likework.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                LikeWorkContract.OrderEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LikeWorkContract.RecordEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LikeWorkContract.CallEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LikeWorkContract.CarEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LikeWorkContract.ClientEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LikeWorkContract.StatusEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.OrderEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from order table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LikeWorkContract.RecordEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from record table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LikeWorkContract.CallEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from call table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LikeWorkContract.CarEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from car table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LikeWorkContract.ClientEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from client table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LikeWorkContract.StatusEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from status table during delete", 0, cursor.getCount());
        cursor.close();

    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                LikeWorkProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: LikeWorkProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + LikeWorkContract.CONTENT_AUTHORITY,
                    providerInfo.authority, LikeWorkContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: LikeWorkProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {

        String type = mContext.getContentResolver().getType(LikeWorkContract.OrderEntry.CONTENT_URI);

        assertEquals("Error: the OrderEntry CONTENT_URI should return OrderEntry.CONTENT_TYPE",
                LikeWorkContract.OrderEntry.CONTENT_TYPE, type);

        int id = 141912; // December 21st, 2014
        // content://com.example.gbyakov.likework/order/141912
        type = mContext.getContentResolver().getType(
                LikeWorkContract.OrderEntry.buildOrderID(id));
        // vnd.android.cursor.item/com.example.gbyakov.likework/order/141912
        assertEquals("Error: the OrderEntry CONTENT_URI with id should return OrderEntry.CONTENT_ITEM_TYPE",
                LikeWorkContract.OrderEntry.CONTENT_ITEM_TYPE, type);

    }

    public void testBasicOrderQuery() {
        // insert our test records into the database
        LikeWorkDBHelper dbHelper = new LikeWorkDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues orderValues = TestUtilities.createOrderValues();
        long orderRowId = TestUtilities.insertOrderValues(mContext);
        assertTrue("Unable to Insert OrderEntry into the Database", orderRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor orderCursor = mContext.getContentResolver().query(
                LikeWorkContract.OrderEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", orderCursor, orderValues);
    }

    public void testUpdateData() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createCarValues();

        Uri carUri = mContext.getContentResolver().
                insert(LikeWorkContract.CarEntry.CONTENT_URI, values);
        long carRowId = ContentUris.parseId(carUri);

        // Verify we got a row back.
        assertTrue(carRowId != -1);
        Log.d(LOG_TAG, "New row id: " + carRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LikeWorkContract.CarEntry._ID, carRowId);
        updatedValues.put(LikeWorkContract.CarEntry.COLUMN_MODEL, "HILUX");

        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(LikeWorkContract.CarEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                LikeWorkContract.CarEntry.CONTENT_URI, updatedValues, LikeWorkContract.CarEntry._ID + "= ?",
                new String[] { Long.toString(carRowId)});
        assertEquals(count, 1);

        // if your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.CarEntry.CONTENT_URI,
                null,   // projection
                LikeWorkContract.CarEntry._ID + " = " + carRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateCar.  Error validating car entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testInsertReadProvider() {

        ContentValues testValues = TestUtilities.createCarValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LikeWorkContract.CarEntry.CONTENT_URI, true, tco);
        Uri carUri = mContext.getContentResolver().insert(LikeWorkContract.CarEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert order
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long carRowId = ContentUris.parseId(carUri);

        // Verify we got a row back.
        assertTrue(carRowId != -1);

         // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.CarEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating CarEntry.",
                cursor, testValues);

    }

    public void testDeleteRecords() {

        long orderRowId = TestUtilities.insertOrderValues(mContext);

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver orderObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LikeWorkContract.OrderEntry.CONTENT_URI, true, orderObserver);

        deleteAllRecordsFromProvider();

        orderObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(orderObserver);

    }
}
