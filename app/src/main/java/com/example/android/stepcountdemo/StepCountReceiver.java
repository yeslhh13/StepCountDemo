package com.example.android.stepcountdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Kat on 2017-04-15.
 * <p>
 * {@link BroadcastReceiver} class to restart the {@link StepCountService}
 */

public class StepCountReceiver extends BroadcastReceiver {
    /**
     * {@link GlobalVariable} to get the step count value
     */
    private GlobalVariable mGlobal;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("StepCountReceiver", "onReceive");
        /**
         * When the service is killed, register the service again by the alarm
         */
        if (intent.getAction().equals("ACTION.RESTART.StepCountService")) {
            Log.i("StepCountReceiver", "ACTION.RESTART.StepCountService");
            Intent i = new Intent(context, StepCountService.class);
            context.startService(i);
        }

        /**
         * Register the service again when the phone is booted
         */
        if (intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)) {
            Log.i("StepCountReceiver", "ACTION_BOOT_COMPLETED");
            Intent i = new Intent(context, StepCountService.class);
            context.startService(i);
        }

        if (intent.getAction().equals("ACTION.MIDNIGHT.StepCountService")) {
            mGlobal = (GlobalVariable) context.getApplicationContext();

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("자정 알림").setAutoCancel(true)
                    .setWhen(System.currentTimeMillis()).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentText("어제는 " + String.valueOf(mGlobal.getStepCount()) + "걸음 걸으셨습니다!")
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE).setContentIntent(pendingIntent);
            mGlobal.resetStepCount();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }
    }
}
