package com.example.android.stepcountdemo.diary;

/**
 * Created by soyeon on 2017-05-14.
 * 2017-09-24 다이어리 위에 저장버튼만 만드는거랑 날짜 표시 추가~
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stepcountdemo.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DiaryWriteActivity extends AppCompatActivity implements View.OnClickListener {
    //시간설정변수
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy년 MM월 dd일  HH:mm:ss");

    EditText mMemoEdit = null;
    Diary_Act mDiary_Act = new Diary_Act(this);

    private TextView timeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        mMemoEdit = (EditText) findViewById(R.id.memo_edit);

        timeTextView = (TextView) findViewById(R.id.timeText);

        setTimeText();

    }

    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.load_btn: {
//                String memoData = mDiary_Act.load();
//                mMemoEdit.setText(memoData);
//
//                Toast.makeText(this, "불러오기 완료", Toast.LENGTH_LONG).show();
//                break;
//            }
            case R.id.save_btn: {
                String memoData = mMemoEdit.getText().toString();
                mDiary_Act.save(memoData);
                mMemoEdit.setText("");

                Toast.makeText(this, "저장완료", Toast.LENGTH_LONG).show();
                break;
            }
//            case R.id.delete_btn: {
//                mDiary_Act.delete();
//                mMemoEdit.setText("");
//
//                Toast.makeText(this, "삭제 완료", Toast.LENGTH_LONG).show();
//                break;
//            }
        }
    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    private void setTimeText() {
        timeTextView.setText(String.valueOf(getTime()));
    }

}