package com.techguys.tester;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String h1 = sharedPreferences.getString("s1", "");
        String h2 = sharedPreferences.getString("s2", "");
        String h3 = sharedPreferences.getString("s3", "");
        assert h1 != null;
        if (!h1.isEmpty()) {
            new tweet().execute(h1);
            notification(context,"check it now");
        }
        assert h2 != null;
        if (!h2.isEmpty()) {
            new tweet().execute(h2);
        }
        assert h3 != null;
        if (!h3.isEmpty()) {
            new tweet().execute(h3);
        }



    }
    public void notification(Context context,String message){
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notification", "channel1", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notification")
                .setContentTitle("you got new tweets to analyse")
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_btn_speak_now);
        notificationManager.notify(1, builder.build());
    }
}
