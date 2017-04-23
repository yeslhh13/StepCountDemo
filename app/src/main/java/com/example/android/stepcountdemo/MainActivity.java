package com.example.android.stepcountdemo;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Kat on 2017-03-24
 * <p>
 * Main activity
 * Detect user's step count and show it to the user
 * <p>
 * TODO: find a method to change the TextView immediately when the sensor changes
 */

public class MainActivity extends AppCompatActivity {
    /**
     * TextView to show step count to the user
     */
    private TextView stepCountView;
    /**
     * {@link GlobalVariable} to get the step count value
     */
    private GlobalVariable mGlobalVariable;
    /**
     * {@link StepCountReceiver} to restart the service when the task is killed
     */
    private StepCountReceiver mStepCountReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stepCountView = (TextView) findViewById(R.id.step_count);
        /**
         * Get the {@link GlobalVariable} instance
         */
        mGlobalVariable = (GlobalVariable) getApplication();
        mStepCountReceiver = new StepCountReceiver();

        /**
         * Register service on the broadcast and start {@link StepCountService}
         */
        Intent intent = new Intent(getApplicationContext(), StepCountService.class);
        IntentFilter intentFilter = new IntentFilter("com.example.android.stepcountdemo.StepCountService");
        registerReceiver(mStepCountReceiver, intentFilter);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        stepCountView.setText(String.valueOf(mGlobalVariable.getStepCount()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stepCountView.setText(String.valueOf(mGlobalVariable.getStepCount()));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stepCountView.setText(String.valueOf(mGlobalVariable.getStepCount()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        stepCountView.setText(String.valueOf(mGlobalVariable.getStepCount()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "onDestroy");
        unregisterReceiver(mStepCountReceiver);
    }
}
