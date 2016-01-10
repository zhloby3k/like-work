package com.example.gbyakov.likework.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LikeWorkAuthenticatorService extends Service {

    private LikeWorkAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new LikeWorkAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}