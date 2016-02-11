package com.example.gbyakov.likework.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.gbyakov.likework.MainActivity;
import com.example.gbyakov.likework.R;
import com.google.android.gms.gcm.GcmListenerService;

public class GCMListenerService extends GcmListenerService {

    private static final String LOG_TAG = "GCMListenerService";
    private static final String EXTRA_DATA = "data";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_ID = "id";

    public int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(LOG_TAG, "Received: " + data.toString());

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean getNotifications = sharedPreferences.getBoolean(MainActivity.ENABLE_NOTIFICATIONS, true);

        if (!data.isEmpty() && getNotifications) {
            String senderId = getString(R.string.gcm_defaultSenderId);
            if (senderId.length() == 0) {
                Toast.makeText(this, "SenderID string needs to be set", Toast.LENGTH_LONG).show();
            }
            if ((senderId).equals(from)) {
                try {
                    String message = data.getString(EXTRA_MESSAGE);
                    String id = data.getString(EXTRA_ID);
                    sendNotification(message, id);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Parsing error: " + data.toString(), e);
                }
            }
        }
    }

    private void sendNotification(String message, String id) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("id_1c", id);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_gcm);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_gcm)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(this.getString(R.string.gcm_message_title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentText(message)
                        .setTicker(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        NOTIFICATION_ID++;
    }
}