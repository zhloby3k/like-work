package com.example.gbyakov.likework.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class IIDListenerService extends InstanceIDListenerService {
    private static final String TAG = "InstanceIDLS";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}