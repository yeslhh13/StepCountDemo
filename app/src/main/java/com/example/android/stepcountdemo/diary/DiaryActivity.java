package com.example.android.stepcountdemo.diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.android.stepcountdemo.R;

/**
 * Created by soyeon on 2017-05-14.
 */

public class DiaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        Button b = (Button) findViewById(R.id.write_button);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiaryWriteActivity.class);
                startActivity(intent);
            }
        });
    }

}
