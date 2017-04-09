package com.example.android.stepcountdemo;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

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
        super.onCreate();
        /**
         * Get the {@link SensorManager} instance with the argument {@link SENSOR_SERVICE}
         */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /**
         * Get the default TYPE_STEP_COUNTER sensor
         */
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        checkSensor();

        /**
         * Get the {@link GlobalVariable} instance
         */
        mGlobalVariable = (GlobalVariable) getApplication();
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
        checkSensor();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Check if the TYPE_STEP_COUNTER sensor exists and if not, alert the user
     */
    public void checkSensor() {
        if (mSensor == null)
            Toast.makeText(this, getString(R.string.sensor_not_available), Toast.LENGTH_SHORT).show();
        else
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
}
