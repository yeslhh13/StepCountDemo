package com.example.android.stepcountdemo;

/**
 * Created by soyeon on 2017-05-14.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class DiaryWriteActivity extends AppCompatActivity {
    EditText mMemoEdit = null;
    Diary_Act mDiary_Act = new Diary_Act(this);

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);
        mMemoEdit = (EditText) findViewById(R.id.memo_edit);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_btn: {
                String memoData = mDiary_Act.load();
                mMemoEdit.setText(memoData);

                Toast.makeText(this, "불러오기 완료", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.save_btn: {
                String memoData = mMemoEdit.getText().toString();
                mDiary_Act.save(memoData);
                mMemoEdit.setText("");

                Toast.makeText(this, "저장완료", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.delete_btn: {
                mDiary_Act.delete();
                mMemoEdit.setText("");

                Toast.makeText(this, "삭제 완료", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

}