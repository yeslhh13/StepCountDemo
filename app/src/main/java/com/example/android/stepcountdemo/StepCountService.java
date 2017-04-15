package com.example.android.stepcountdemo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Kat on 2017-04-02.
 * <p>
 * Do the step count in background using {@link Service}
 */

public class StepCountService extends Service implements SensorEventListener {
    private static final int MILLIS_IN_FUTURE = 1000 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
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
    /**
     * {@link CountDownTimer} to schedule a countdown until a time in the future,
     * with regular notifications on intervals along the way
     */
    private CountDownTimer mCountDownTimer;

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

        /**
         * Get the {@link SensorManager} instance with the argument {@link SENSOR_SERVICE}
         */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /**
         * Get the default TYPE_STEP_COUNTER sensor
         */
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        /**
         * Get the {@link GlobalVariable} instance
         */
        mGlobalVariable = (GlobalVariable) getApplication();

        /**
         * Set and start {@link #mCountDownTimer}
         */
        setCountDownTimer();
        mCountDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("StepCountService", "onDestroy");
        mCountDownTimer.cancel();
        registerRestartAlarm();
    }

    private void setCountDownTimer() {
        Log.i("StepCountService", "setCountDownTimer");
        mCountDownTimer = new CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

            }
        };
    }

    /**
     * Called when there is a new sensor event
     *
     * @param event to get the new sensor event from
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        mGlobalVariable.setStepCount((int) event.values[0]);
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
        Log.i("StepCountService", "onStartCommand");
        /**
         * When id is 0, notification is hidden from the user
         */
        startForeground(0, new Notification());

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /**
         * Don't show the notification when using startForeground()
         */
        Notification notification = new Notification.Builder(getApplicationContext()).setContentText("")
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle("").build();
        notificationManager.notify(startId, notification);
        notificationManager.cancel(startId);

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
