package com.example.android.stepcountdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.android.stepcountdemo.db.TreeContract;
import com.example.android.stepcountdemo.db.TreeDBHelper;

import java.util.Calendar;

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

        /**
         * When the app is destroyed, save the steps data to the database
         */
        if (intent.getAction().equals("ACTION.DESTROY.StepCountService")) {
            mGlobal = (GlobalVariable) context.getApplicationContext();

            ContentValues values = new ContentValues();
            values.put(TreeContract.StepsEntry.COLUMN_STEPS_DATE, intent.getStringExtra("date"));
            values.put(TreeContract.StepsEntry.COLUMN_STEPS_VALUE, mGlobal.getStepCount());

            if (intent.getData() != null) {
                Uri contentUri = intent.getData();
                context.getContentResolver().update(contentUri, values, null, null);
            } else {
                context.getContentResolver().insert(TreeContract.StepsEntry.CONTENT_URI, values);
            }
        }

        /**
         * When midnight, save the steps data and alert to user
         */
        if (intent.getAction().equals("ACTION.MIDNIGHT.StepCountService")) {
            final TreeDBHelper dbHelper = new TreeDBHelper(context.getApplicationContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Uri contentUri = TreeContract.StepsEntry.CONTENT_URI;

            mGlobal = (GlobalVariable) context.getApplicationContext();

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Step Count Demo").setAutoCancel(true)
                    .setWhen(System.currentTimeMillis()).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentText("어제는 " + String.valueOf(mGlobal.getStepCount()) + "걸음 걸으셨습니다!")
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE).setContentIntent(pendingIntent);

            /**
             * Current calendar value
             */
            Calendar calendar = Calendar.getInstance();
            String date_value = String.valueOf(calendar.get(Calendar.YEAR));

            /**
             * If today is the first day of the month, check if yesterday was 30th or the 31th
             */
            if (String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)).equals("1")) {
                String month = String.valueOf(calendar.get(Calendar.MONTH));
                date_value += month;

                if (month.equals("1") || month.equals("3") || month.equals("5") || month.equals("7")
                        || month.equals("8") || month.equals("10") || month.equals("12"))
                    date_value += "31";
                else
                    date_value += "30";
            } else {
                date_value += String.valueOf(calendar.get(Calendar.MONTH) + 1) + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            }

            int step_count = mGlobal.getStepCount();

            /**
             * Create a {@link ContentValues} object where column names are the keys and product attributes from the editor are the values
             */
            ContentValues values = new ContentValues();

            /**
             * This is an existing steps value, so update the value
             * and pass in the new {@link ContentValues}
             */
            if (hasStepRecord(context, date_value)) {
                Cursor cursor = db.rawQuery("SELECT " + TreeContract.StepsEntry.COLUMN_STEPS_VALUE
                        + " FROM " + TreeContract.StepsEntry.TABLE_NAME + " WHERE "
                        + TreeContract.StepsEntry.COLUMN_STEPS_DATE + "='" + date_value + "';", null);
                cursor.moveToFirst();
                int existing_value = cursor.getInt(0);

                cursor.close();

                values.put(TreeContract.StepsEntry.COLUMN_STEPS_VALUE, existing_value + step_count);
                String selection = TreeContract.StepsEntry.COLUMN_STEPS_DATE + " =?";
                String[] selectionArgs = new String[]{date_value};

                int rowsAffected = context.getContentResolver().update(contentUri, values, selection, selectionArgs);
                if (rowsAffected == 0)
                    Log.e("Midnight Update", "failed");
            } else {
                /**
                 * Check if this is supposed to be a new steps value
                 */
                values.put(TreeContract.StepsEntry.COLUMN_STEPS_VALUE, step_count);
                values.put(TreeContract.StepsEntry.COLUMN_STEPS_DATE, date_value);

                Uri newUri = context.getContentResolver().insert(contentUri, values);
                if (newUri == null)
                    Log.e("Midnight Insertion", "failed");
            }
            mGlobal.resetStepCount();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }
    }

    /**
     * Check if the record which date is same as yesterday exists
     *
     * @param date to find
     * @return if the object exists
     */
    public boolean hasStepRecord(Context context, String date) {
        final TreeDBHelper dbHelper = new TreeDBHelper(context.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectString = "SELECT * FROM " + TreeContract.StepsEntry.TABLE_NAME + " WHERE "
                + TreeContract.StepsEntry.COLUMN_STEPS_DATE + " =?";

        /**
         * Add the String you are searching by here.
         * Put it in an array to avoid an unrecognized token error
         */
        Cursor cursor = db.rawQuery(selectString, new String[]{date});

        boolean hasStepRecord = false;
        if (cursor.moveToFirst())
            hasStepRecord = true;

        cursor.close();

        return hasStepRecord;
    }
}
