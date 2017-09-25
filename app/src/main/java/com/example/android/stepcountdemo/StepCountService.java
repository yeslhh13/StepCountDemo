package com.example.android.stepcountdemo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.stepcountdemo.db.TreeContract;
import com.example.android.stepcountdemo.db.TreeDBHelper;

import java.util.Calendar;

/**
 * Created by Kat on 2017-04-02.
 * <p>
 * Do the step count in background using {@link Service}
 */

public class StepCountService extends Service implements SensorEventListener {
    /**
     * SensorManager to get the access of the device's sensors
     */
    private SensorManager mSensorManager;
    /**
     * Sensor to get the default sensor of the {@link #mSensorManager}
     */
    private Sensor mSensor;
    /**
     * {@link GlobalVariable} to store the step count value
     */
    private GlobalVariable mGlobalVariable;

    public StepCountService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Creates an {@link Service} invoked by the constructor of the subclass
     */
    @Override
    public void onCreate() {
        unregisterRestartAlarm();
        Log.i("StepCountService", "onCreate");
        super.onCreate();

        // Get the {@link SensorManager} instance with the argument {@link SENSOR_SERVICE}
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Get the default TYPE_STEP_DETECTOR sensor
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        // Get the {@link GlobalVariable} instance
        mGlobalVariable = (GlobalVariable) getApplication();
    }

    @Override
    public void onDestroy() {
        //TODO:Save the step count data to the database
        super.onDestroy();
        Log.i("StepCountService", "onDestroy");
        registerRestartAlarm();
    }

    /**
     * Called when there is a new sensor event
     * increase the step count variable when the sensor is changed
     *
     * @param event to get the new sensor event from
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mGlobalVariable.getTreeStep() == 3000) {
            Intent intent = new Intent(getApplicationContext(), StepCountReceiver.class).setAction("ACTION.DESTROY.TreeGrownUp");
            sendBroadcast(intent);
        } else {
            mGlobalVariable.increaseCount();
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed
     * Only called when this accuracy value changes
     * Won't use it as the accuracy of the sensor rarely changes
     *
     * @param sensor   sensor to detect the changes
     * @param accuracy the new accuracy of the sensor
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TreeDBHelper dbHelper = new TreeDBHelper(this.getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Current calendar value
        Calendar calendar = Calendar.getInstance();
        String date_value = String.valueOf(calendar.get(Calendar.YEAR)) + String.valueOf(calendar.get(Calendar.MONTH) + 1)
                + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        String selectString = "SELECT * FROM " + TreeContract.StepsEntry.TABLE_NAME + " WHERE "
                + TreeContract.StepsEntry.COLUMN_STEPS_DATE + " =?";

        // Put the date value in an array to avoid an unrecognized token error
        Cursor cursor = db.rawQuery(selectString, new String[]{date_value});

        boolean hasStepRecord = false;
        if (cursor.moveToFirst())
            hasStepRecord = true;

        //Load the step count data from the database if today's data exists
        if (hasStepRecord)
            mGlobalVariable.setStepCount(cursor.getInt(0));

        cursor.close();

        Log.i("StepCountService", "onStartCommand");

        Intent intent1 = new Intent(this, LoadingActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

        // Show the notification when using startForeground()
        Notification notification = new Notification.Builder(getApplicationContext()).setContentText("App Running")
                .setSmallIcon(R.drawable.greentree_logo).setContentTitle(getString(R.string.app_name)).setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Register StepCountService on AlarmManager
     */
    private void registerRestartAlarm() {
        Log.i("StepCountService", "registerRestartAlarm");
        Intent intent = new Intent(getApplicationContext(), StepCountReceiver.class)
                .setAction("ACTION.RESTART.StepCountService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StepCountService.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1000;

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 1000, pendingIntent);
    }

    /**
     * Unregister StepCountService on AlarmManager
     */
    private void unregisterRestartAlarm() {
        Log.i("StepCountService", "unregisterRestartAlarm");
        Intent intent = new Intent(getApplicationContext(), StepCountReceiver.class)
                .setAction("ACTION.RESTART.StepCountService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StepCountService.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
