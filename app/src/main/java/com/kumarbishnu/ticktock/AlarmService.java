package com.kumarbishnu.ticktock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.Objects;

public class AlarmService extends Service {

    private String CHANNEL_ID = "AlarmService";
    private String CHANNEL_NAME = "Alarm Service";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent snoozeAction = new Intent(getApplicationContext(), AlarmActionReceiver.class);
        snoozeAction.putExtra("snooze", "snooze");

        Intent stopAction = new Intent(getApplicationContext(), AlarmActionReceiver.class);
        stopAction.putExtra("stop", "stop");

        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, snoozeAction, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, snoozeAction, PendingIntent.FLAG_UPDATE_CURRENT);

        createChannel();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentText("Text")
                        .setContentTitle("Title")
                        .setSmallIcon(R.drawable.ic_alarm)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .addAction(R.drawable.ic_check, "Snooze", snoozePendingIntent)
                        .addAction(R.drawable.ic_close, "Stop", stopPendingIntent)
                        .setAutoCancel(true)
                        .setTimeoutAfter(60 * 1000)
                        .setFullScreenIntent(snoozePendingIntent, true);

        Notification alarmNotification = notificationBuilder.build();
        startForeground(120, alarmNotification);

//        CountDownTimer timer = new CountDownTimer(1000 * 60, 1000) {
//            @Override
//            public void onTick(long l) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                stopSelf();
//                getApplicationContext().getSystemService(NotificationManager.class).notify(120, alarmNotification);
//            }
//        }.start();


        return START_STICKY;
    }

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Alarm Notifications");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            channel.setShowBadge(true);
            channel.setSound(Uri.parse("android:resource//" + getApplicationContext().getPackageName() + "/" + R.raw.carnival),
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_RING)
                            .setUsage(AudioAttributes.USAGE_ALARM).build());
            getApplicationContext().getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}
