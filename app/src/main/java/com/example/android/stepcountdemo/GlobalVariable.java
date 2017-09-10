package com.example.android.stepcountdemo;

import android.app.Application;

/**
 * Created by Kat on 2017-04-09.
 * <p>
 * Stores variable used in all activities
 */

public class GlobalVariable extends Application {
    /**
     * Stores step count value
     */
    private int mStepCount = 0;
    /**
     * Stores tree step count value
     */
    private int mTreeStep = 0;

    public int getStepCount() {
        return mStepCount;
    }

    public void setStepCount(int stepCount) {
        this.mStepCount = stepCount;
    }

    public int getTreeStep() {
        return mTreeStep;
    }

    public void setTreeStep(int mTreeStep) {
        this.mTreeStep = mTreeStep;
    }

    public void increaseCount() {
        this.mStepCount++;
        this.mTreeStep++;
    }

    public void resetStepCount() {
        this.mStepCount = 0;
    }

    public void resetTreeStep() {
        this.mTreeStep = 0;
    }
}
