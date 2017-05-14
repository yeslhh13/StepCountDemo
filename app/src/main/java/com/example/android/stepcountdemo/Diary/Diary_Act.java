package com.example.android.stepcountdemo.Diary;

/**
 * Created by soyeon on 2017-05-14.
 */

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Diary_Act {

    private static final String FILE_NAME = "Diary.txt";
    Context mContext = null;

    public Diary_Act(DiaryWriteActivity diaryActivity) {
    }

    public void Diary_Act(Context context) {
        mContext = context;
    }

    public void save(String strData) {
        if (strData == null || strData.equals(" ")) {
            return;
        }

        FileOutputStream fosMemo = null;

        try {
            fosMemo = mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fosMemo.write(strData.getBytes());
            fosMemo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String load() {
        try {
            FileInputStream fisMemo = mContext.openFileInput(FILE_NAME);
            byte[] memoData = new byte[fisMemo.available()];
            while (fisMemo.read(memoData) != -1) {
            }
            return new String(memoData);
        } catch (IOException e) {
        }
        return "";
    }

    public void delete() {
        mContext.deleteFile(FILE_NAME);
    }
}

