package com.skl.bingofire.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.skl.bingofire.R;
import com.skl.bingofire.activities.MainActivity;

/**
 * Created by Kristina on 7/14/16.
 */
public class BingoFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = BingoFirebaseMessagingService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 10;

    private NotificationCompat.Builder mNotificationBuilder;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (mNotificationBuilder == null) {
            mNotificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.bingo_fire_notif_icon);

            Intent openIntent = new Intent(this, MainActivity.class);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent openPendingIntent = PendingIntent.getActivity(this, 2, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mNotificationBuilder.setContentIntent(openPendingIntent);
            mNotificationBuilder.setAutoCancel(true);
        }

        final RemoteMessage.Notification notification = remoteMessage.getNotification();

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + notification.getBody());

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder.setContentTitle(notification.getTitle());
        mNotificationBuilder.setContentText(notification.getBody());
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }
}
