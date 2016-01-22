package com.example.gbyakov.likework.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.example.gbyakov.likework.R;

public class LikeWorkSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = LikeWorkSyncAdapter.class.getSimpleName();

    public LikeWorkSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String domain = "";
        String username = account.name;
        if (username.indexOf("\\") >= 0) {
            int indexOfSlash = username.indexOf("\\");
            domain = username.substring(0, indexOfSlash);
            username = username.substring(indexOfSlash + 1);
        }
        AccountManager am = AccountManager.get(getContext());
        am.getPassword(account);
        String password = am.getPassword(account);

        Log.d(LOG_TAG, "Start sync");

        Exchange1C mExchange = new Exchange1C(username, domain, password, getContext());
        mExchange.SendAnswers();
        mExchange.UpdateOrders();
        mExchange.UpdateCalls();
        mExchange.UpdateRecords();
        mExchange.UpdateStates();
        mExchange.UpdateParts();
        mExchange.UpdateOperations();
        mExchange.UpdateQuestions();

        Log.d(LOG_TAG, "End sync");

    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        Account account = getSyncAccount(context);
        if (account != null) {
            ContentResolver.requestSync(account,
                    context.getString(R.string.content_authority), bundle);
        }
    }

    public static Account getSyncAccount(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(context.getString(R.string.sync_account_type));
        return (accounts.length > 0) ? accounts[0] : null;
    }

}
