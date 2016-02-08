package com.example.gbyakov.likework.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.gbyakov.likework.LoginActivity;
import com.example.gbyakov.likework.R;
import com.google.android.gms.gcm.GcmListenerService;

public class GCMListenerService extends GcmListenerService {
    private static final String LOG_TAG = "GCMListenerService";
    private static final String EXTRA_DATA = "data";
    private static final String EXTRA_MESSAGE = "message";
    private static final String EXTRA_ID = "id";

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(LOG_TAG, "Received: " + data.toString());
        if (!data.isEmpty()) {
            String senderId = getString(R.string.gcm_defaultSenderId);
            if (senderId.length() == 0) {
                Toast.makeText(this, "SenderID string needs to be set", Toast.LENGTH_LONG).show();
            }
            if ((senderId).equals(from)) {
                try {
                    String message = data.getString(EXTRA_MESSAGE);
                    String id = data.getString(EXTRA_ID);
                    sendNotification(message);
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Parsing error: " + data.toString(), e);
                }
            }
        }
    }

    private void sendNotification(String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, LoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_comment);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_comment)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(this.getString(R.string.gcm_message_title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}