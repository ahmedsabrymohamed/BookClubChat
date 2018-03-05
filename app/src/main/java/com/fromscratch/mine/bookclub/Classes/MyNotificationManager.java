package com.fromscratch.mine.bookclub.Classes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.fromscratch.mine.bookclub.R;

public class MyNotificationManager {

    public static final int NOTIFICATION_ID = 101;
    public static final String NOTIFICATION_CHANNEL_ID = "101";
    private static final String CHAT_ID_KEY = "chatID";
    private static final String CHAT_NAME_KEY = "chatName";
    private static final String RING_PATH = "://" + "com.fromscratch.mine.bookclub" + "/raw/notification_ring_tone";
    Context context;

    MyNotificationManager(Context mContext) {
        this.context = mContext;
    }

    public void displayNotification(ClubNotification notification, Intent intent) {

        intent.putExtra(CHAT_ID_KEY, notification.getChatID());
        intent.putExtra(CHAT_NAME_KEY, notification.getChatName());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        Notification mNotification;
        mNotification = builder.setSmallIcon(R.drawable.social_send_now).setTicker(notification.getChatName()).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(notification.getChatName())
                .setSmallIcon(R.drawable.social_send_now)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentText(notification.getTitle() + " : " + notification.getNotificationMessage())
                .build();

        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mNotification);

    }

    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + RING_PATH);
            Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
