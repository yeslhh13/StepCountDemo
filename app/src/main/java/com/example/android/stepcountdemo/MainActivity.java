package com.example.android.stepcountdemo;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Kat on 2017-03-24
 * <p>
 * Main activity
 * Detect user's step count and show it to the user
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    /**
     * SensorManager to get the access of the device's sensors
     */
    private SensorManager mSensorManager;
    /**
     * TextView to show step count to the user
     */
    private TextView stepCountView;
    /**
     * To check whether the activity is running(to change the TextView)
     */
    private boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCountView = (TextView) findViewById(R.id.step_count);

        /**
         * Get the {@link SensorManager} instance with the argument {@link SENSOR_SERVICE}
         */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    /**
     * Called when there is a new sensor event
     *
     * @param event to get the new sensor event from
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isActivityRunning)
        /**
         * Get the step count value of the SensorEvent and change the {@link stepCountView}
         * only when the activity is running
         */
            stepCountView.setText(String.valueOf((int) event.values[0]));
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
    protected void onResume() {
        super.onResume();
        /**
         * Set the {@link isActivityRunning} to true as the activity is running
         */
        isActivityRunning = true;

        /**
         * Get the default TYPE_STEP_COUNTER sensor
         */
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (sensor == null)
        /**
         * If there is no TYPE_STEP_COUNTER sensor, alert the user
         */
            Toast.makeText(this, getString(R.string.sensor_not_available), Toast.LENGTH_SHORT).show();
        else
        /**
         * If there is, then register {@link SensorEventListener} for given sensor({@link sensor})
         * at the given sampling frequency {@link SensorManager.SENSOR_DELAY_FASTEST} to get the fastest result
         */
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * Set the {@link isActivityRunning} to false as the activity is not running
         */
        isActivityRunning = false;
    }
}
