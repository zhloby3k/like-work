package com.example.gbyakov.likework.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;

import com.example.gbyakov.likework.R;
import com.example.gbyakov.likework.sync.LikeWorkSyncAdapter;

public class PreferencesFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_AUTO_SYNC = "enable_periodic_sync";
    private static final String KEY_AUTO_SYNC_INTERVAL = "periodic_sync_interval";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        final ListPreference interval = (ListPreference) getPreferenceManager()
                .findPreference(KEY_AUTO_SYNC_INTERVAL);
        interval.setSummary(interval.getEntry());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (TextUtils.equals(KEY_AUTO_SYNC, key)) {
            if (prefs.getBoolean(key, false)) {
                int interval = Integer.parseInt(prefs.getString(
                        KEY_AUTO_SYNC_INTERVAL,
                        getString(R.string.auto_sync_interval_default)
                ));
                LikeWorkSyncAdapter.changePeriodicSync(getContext(), true, interval, interval/3);
            } else {
                LikeWorkSyncAdapter.changePeriodicSync(getContext(), false, 0, 0);
            }
        } else if (TextUtils.equals(KEY_AUTO_SYNC_INTERVAL, key)) {
            final ListPreference interval = (ListPreference) getPreferenceManager().findPreference(key);
            interval.setSummary(interval.getEntry());
            int intervalValue = Integer.parseInt(interval.getValue());
            LikeWorkSyncAdapter.changePeriodicSync(getContext(), true, intervalValue, intervalValue / 3);
        }
    }
}
