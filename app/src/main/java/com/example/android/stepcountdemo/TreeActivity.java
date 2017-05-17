package com.example.android.stepcountdemo;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Kat on 2017-03-24
 * <p>
 * Tree activity
 * Detect user's step count and show it to the user with the state of the tree
 */

public class TreeActivity extends AppCompatActivity {
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
    /**
     * ImageView to show the tree state to the user
     */
    private ImageView treeImage;
    /**
     * Handler to show the user's step count in real time
     */
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);

        stepCountView = (TextView) findViewById(R.id.step_count);
        treeImage = (ImageView) findViewById(R.id.treeImage);

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

        /**
         * Create Handler instance by the default constructor and start loop
         */
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setViews();
                Log.i("HandlerMessage", "Handler running!");

                this.sendEmptyMessageDelayed(0, 1000);
            }
        };

        mHandler.sendEmptyMessage(1);
    }

    /**
     * stop the Handler loop when paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(0);
    }

    /**
     * restart the Handler loop when restarted
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        mHandler.sendEmptyMessage(1);
    }

    /**
     * restart the Handler loop when resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(1);
    }

    /**
     * stop the Handler loop when destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TreeActivity", "onDestroy");
        unregisterReceiver(mStepCountReceiver);
        mHandler.removeMessages(0);
    }

    /**
     * stop the Handler loop when stopped
     */
    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeMessages(1);
    }

    /**
     * Decide which image to use when the step count changes
     *
     * @param stepCount to decide which drawable ID has to be returned
     * @return drawable ID needed to change the ImageView
     */
    public int getDrawableIDByStepCount(int stepCount) {
        int drawableID;

        if (stepCount < 300)
            drawableID = R.drawable.cherryblossom;
        else if (stepCount < 1000)
            drawableID = R.drawable.cherryblossom_2;
        else if (stepCount < 2500)
            drawableID = R.drawable.cherryblossom_3;
        else if (stepCount < 5000)
            drawableID = R.drawable.cherryblossom_4;
        else
            drawableID = R.drawable.cherryblossom_5;

        return drawableID;
    }

    /**
     * method to change the TextView and the ImageView
     */
    public void setViews() {
        stepCountView.setText(String.valueOf(mGlobalVariable.getStepCount()));
        treeImage.setImageResource(getDrawableIDByStepCount(mGlobalVariable.getStepCount()));
    }
}
