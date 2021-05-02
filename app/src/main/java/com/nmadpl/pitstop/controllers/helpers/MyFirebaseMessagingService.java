package com.nmadpl.pitstop.controllers.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nmadpl.pitstop.R;
import com.nmadpl.pitstop.ui.activity.SplashActivity;

import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("Token Updated", s);

        //updateTokenToDatabase(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String tittle = "Qzee", body = "Notification";
//        tag = remoteMessage.getNotification().getTag();
//        String tittle = remoteMessage.getNotification().getTitle();
//        //String tittle=remoteMessage.getData().get("tittle");
//        String body = remoteMessage.getNotification().getBody();
        if (remoteMessage.getData().size() > 0) {
            tittle = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
        } else {
            tittle = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("tittle", tittle);
        intent.putExtra("body", body);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int j = new Random().nextInt();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LatestDevicesNotificationService latest = new LatestDevicesNotificationService(this);
            Notification.Builder builder = latest.getLatest(tittle, body, pendingIntent, defaultSound);
            int i = 0;
            if (j > 0) {
                i = j;
            }

            latest.getManager().notify(i, builder.build());

        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_noti)
                    .setContentTitle(tittle)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigPictureStyle());
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int i = 0;
            if (j > 0) {
                i = j;
            }
            assert notificationManager != null;
            notificationManager.notify(i, builder.build());
        }


    }


}