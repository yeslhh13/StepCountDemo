package com.example.android.stepcountdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TabHost;

import com.example.android.stepcountdemo.calendar.CalendarActivity;
import com.example.android.stepcountdemo.db.TreeContract;
import com.example.android.stepcountdemo.db.TreeDBHelper;
import com.example.android.stepcountdemo.setting.SettingActivity;

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

        /**
         * Create first tab
         */
        intent = new Intent(this, TreeActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.tab_first)).setIndicator(getString(R.string.tab_first)).setContent(intent);
        tabHost.addTab(spec);

//        /**
//         * Create diary tab
//         */
//        intent = new Intent(this, DiaryActivity.class);
//        spec = tabHost.newTabSpec(getString(R.string.tab_second)).setIndicator(getString(R.string.tab_second)).setContent(intent);
//        tabHost.addTab(spec);
//
        /**
         * Create calendar tab
         */
        intent = new Intent(this, CalendarActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.tab_third)).setIndicator(getString(R.string.tab_third)).setContent(intent);
        tabHost.addTab(spec);

//        /**
//         * Create donation tab
//         */
//        intent = new Intent(this, DonationActivity.class);
//        spec = tabHost.newTabSpec(getString(R.string.tab_fourth)).setIndicator(getString(R.string.tab_fourth)).setContent(intent);
//        tabHost.addTab(spec);
//
        /**
         * Create setting tab (환경설정 단어 넣어도 되는데 심심해서 해봤어...ㅎ..바꿔도 돼.........)
         */
        intent = new Intent(this, SettingActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.tab_fifth)).setIndicator(getString(R.string.tab_fifth)).setContent(intent);
        tabHost.addTab(spec);

        /**
         * Set the main tab to first tab(TreeActivity.class)
         */
        tabHost.setCurrentTab(0);

        registerMidnightAlarm();
    }

    private void registerMidnightAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) + 1, 0, 0, 0);

        Intent intent = new Intent(getApplicationContext(), StepCountReceiver.class)
                .setAction("ACTION.MIDNIGHT.StepCountService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        /**
         * Repeat Alarm
         */
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent;

        /**
         * Current calendar value
         */
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
    }

    private Uri getContentUri() {
        TreeDBHelper treeDBHelper = new TreeDBHelper(this);
        SQLiteDatabase db = treeDBHelper.getReadableDatabase();
        Uri contentUri = TreeContract.StepsEntry.CONTENT_URI;

        /**
         * Current calendar value
         */
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
