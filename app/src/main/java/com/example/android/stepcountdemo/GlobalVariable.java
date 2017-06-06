package com.example.android.stepcountdemo;

import android.app.Application;

/**
 * Created by Kat on 2017-04-09.
 * <p>
 * Stores variable used in all activities
 */

public class GlobalVariable extends Application {
    /**
     * stores step count value
     */
    private int mStepCount = 0;

    public int getStepCount() {
        return mStepCount;
    }

    public void setStepCount(int stepCount) {
        this.mStepCount = stepCount;
    }

    public void increaseStepCount() {
        this.mStepCount++;
    }

    public void resetStepCount() {
        this.mStepCount = 0;
    }
}
