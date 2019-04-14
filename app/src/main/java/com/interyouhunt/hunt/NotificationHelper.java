package com.interyouhunt.hunt;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationHelper {

    public static void displayNotification(Context context, String title, String body) {

        NotificationCompat.Builder notifBuilder =
                new NotificationCompat.Builder(context, "Hunt")
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notifMgr = NotificationManagerCompat.from(context);
        notifMgr.notify(1, notifBuilder.build());

    }
}
