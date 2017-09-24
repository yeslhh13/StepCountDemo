package com.example.android.stepcountdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Created by Kat on 2017-04-28
 * <p>
 * Loading activity
 * <p>
 */

public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ImageView imageView = (ImageView) findViewById(R.id.loading_image);

        int randInt = (int) (Math.random() * 5);
        switch (randInt) {
            case 1:
                imageView.setImageResource(R.drawable.main4);
                break;
            case 2:
                imageView.setImageResource(R.drawable.main5);
                break;
            case 3:
                imageView.setImageResource(R.drawable.main6);
                break;
            case 4:
                imageView.setImageResource(R.drawable.main7);
                break;
            default:
                break;
        }

        startLoading();
    }

    /**
     * Starts loading using {@link Handler}
     * Loads for 2 seconds(2000 milliseconds)
     */
    public void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
