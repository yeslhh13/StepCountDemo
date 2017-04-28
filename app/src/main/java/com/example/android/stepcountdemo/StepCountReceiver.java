package com.example.android.stepcountdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Kat on 2017-04-15.
 * <p>
 * {@link BroadcastReceiver} class to restart the {@link StepCountService}
 */

public class StepCountReceiver extends BroadcastReceiver {
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
    }
}
