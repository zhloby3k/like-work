package com.example.gbyakov.likework.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LikeWorkSyncService  extends Service {

    private static LikeWorkSyncAdapter sLikeWorkSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        if (sLikeWorkSyncAdapter == null) {
             sLikeWorkSyncAdapter = new LikeWorkSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sLikeWorkSyncAdapter.getSyncAdapterBinder();
    }
}