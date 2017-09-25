package com.example.android.stepcountdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.TabHost;

import com.example.android.stepcountdemo.calendar.CalendarActivity;
import com.example.android.stepcountdemo.db.TreeContract;
import com.example.android.stepcountdemo.db.TreeDBHelper;
import com.example.android.stepcountdemo.setting.SettingActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by Kat on 2017-04-28
 * <p>
 * Main Tab activity
 */

public class MainActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        // Create first tab
        intent = new Intent(this, TreeActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.tab_first)).setIndicator(getString(R.string.tab_first)).setContent(intent);
        tabHost.addTab(spec);

        // Create calendar tab
        intent = new Intent(this, CalendarActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.tab_second)).setIndicator(getString(R.string.tab_second)).setContent(intent);
        tabHost.addTab(spec);

        // Create setting tab
        intent = new Intent(this, SettingActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.tab_third)).setIndicator(getString(R.string.tab_third)).setContent(intent);
        tabHost.addTab(spec);

        // Set the main tab to first tab(TreeActivity.class)
        tabHost.setCurrentTab(0);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //체크박스 값에따라 알람 여부 결정
        if (prefs.getBoolean("alarm_setting", true)) {
            registerMidnightAlarm();
        } else {
            unregisterMidnightAlarm();
        }
    }

    private void registerMidnightAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) + 1, 0, 0, 0);

        Intent intent = new Intent(getApplicationContext(), StepCountReceiver.class)
                .setAction("ACTION.MIDNIGHT.StepCountService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        // Repeat Alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
    }

    private void unregisterMidnightAlarm() {
        Intent intent = new Intent(this, StepCountReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (sender != null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(sender);
            sender.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent;

        // Current calendar value
        Calendar calendar = Calendar.getInstance();
        String date_value = String.valueOf(calendar.get(Calendar.YEAR)) + String.valueOf(calendar.get(Calendar.MONTH))
                + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        if (getContentUri() == null)
            sendBroadcast(new Intent("ACTION.DESTROY.StepCountService").putExtra("date", date_value));
        else {
            intent = new Intent(getApplicationContext(), StepCountReceiver.class)
                    .setAction("ACTION.DESTROY.StepCountService").setData(getContentUri()).putExtra("date", date_value);
            sendBroadcast(intent);
        }

        // Save current tree info to internal storage
        final TreeDBHelper dbHelper = new TreeDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT " + TreeContract.MainEntry._ID + " FROM " + TreeContract.MainEntry.TABLE_NAME, null);
            cursor.moveToLast();
            int tree_id = cursor.getInt(0);
            cursor.close();

            GlobalVariable mGlobalVariable = (GlobalVariable) getApplication();

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getString(R.string.txt_file_name));
            FileOutputStream fos = new FileOutputStream(file);

            String text = String.valueOf(tree_id) + "\n" + String.valueOf(mGlobalVariable.getTreeStep());
            fos.write(text.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri getContentUri() {
        TreeDBHelper treeDBHelper = new TreeDBHelper(this);
        SQLiteDatabase db = treeDBHelper.getReadableDatabase();
        Uri contentUri = TreeContract.StepsEntry.CONTENT_URI;

        // Current calendar value
        Calendar calendar = Calendar.getInstance();
        String date_value = String.valueOf(calendar.get(Calendar.YEAR)) + String.valueOf(calendar.get(Calendar.MONTH))
                + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        Cursor cursor = db.rawQuery("SELECT " + TreeContract.StepsEntry._ID
                + " FROM " + TreeContract.StepsEntry.TABLE_NAME + " WHERE "
                + TreeContract.StepsEntry.COLUMN_STEPS_DATE + "='" + date_value + "';", null);

        int id;

        if (cursor.getCount() == 0)
            return null;
        else {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }

        cursor.close();

        return ContentUris.withAppendedId(contentUri, id);
    }
}
