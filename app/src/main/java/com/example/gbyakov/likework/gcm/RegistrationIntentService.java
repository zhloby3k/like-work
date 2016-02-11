package com.example.gbyakov.likework.gcm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.gbyakov.likework.MainActivity;
import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.sync.Exchange1C;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;


public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                boolean sentIdToServer = sendRegistrationToServer(token);
                sharedPreferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, sentIdToServer).apply();
                sharedPreferences.edit().putBoolean(MainActivity.ENABLE_NOTIFICATIONS, sentIdToServer).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private boolean sendRegistrationToServer(String id) {
        Log.i(TAG, "GCM Registration ID: " + id);
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccountsByType(this.getString(R.string.sync_account_type));
        if (accounts.length > 0) {
            Account account = accounts[0];

            String domain = "";
            String username = account.name;
            if (username.indexOf("\\") >= 0) {
                int indexOfSlash = username.indexOf("\\");
                domain = username.substring(0, indexOfSlash);
                username = username.substring(indexOfSlash + 1);
            }
            am.getPassword(account);
            String password = am.getPassword(account);

            Exchange1C mExchange = new Exchange1C(username, domain, password, this);
            return mExchange.RegDevice(id);
        }
        return false;
    }
}