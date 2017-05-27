package com.example.android.stepcountdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kat on 2017-05-14.
 * <p>
 * DB helper for Step Counting
 * Manages DB creation and version management
 */

public class TreeDBHelper extends SQLiteOpenHelper {
    /**
     * Name of the DB file
     */
    public static final String DATABASE_NAME = "greenTree.db";
    /**
     * DB version
     * To change the DB schema, you must increment the DB version
     */

    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link TreeDBHelper}
     *
     * @param context of the app
     */

    public TreeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Called when DB is created for the first time
     *
     * @param db to be created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE_MAIN = "CREATE TABLE " + TreeContract.MainEntry.TABLE_NAME
                + " (" + TreeContract.MainEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TreeContract.MainEntry.COLUMN_TREE_TYPE + " TEXT NOT NULL, "
                + TreeContract.MainEntry.COLUMN_TREE_NAME + " TEXT, "
                + TreeContract.MainEntry.COLUMN_TREE_LEVEL + " INTEGER NOT NULL DEFAULT 0);";

        String SQL_CREATE_TABLE_DIARY = "CREATE TABLE " + TreeContract.DiaryEntry.TABLE_NAME
                + " (" + TreeContract.DiaryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TreeContract.DiaryEntry.COLUMN_DIARY_TITLE + " TEXT NOT NULL, "
                + TreeContract.DiaryEntry.COLUMN_DIARY_CONTENT + " TEXT NOT NULL, "
                + TreeContract.DiaryEntry.COLUMN_DIARY_IMAGE + " BLOB);";

        String SQL_CREATE_TABLE_DONATION = "CREATE TABLE " + TreeContract.DonationEntry.TABLE_NAME
                + " (" + TreeContract.DonationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TreeContract.DonationEntry.COLUMN_DONATION_TITLE + " TEXT NOT NULL, "
                + TreeContract.DonationEntry.COLUMN_DONATION_MONEY + " INTEGER NOT NULL DEFAULT 0, "
                + TreeContract.DonationEntry.COLUMN_DONATION_IMAGE + " BLOB NOT NULL);";

        String SQL_CREATE_TABLE_STEPS = "CREATE TABLE " + TreeContract.StepsEntry.TABLE_NAME
                + " (" + TreeContract.StepsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TreeContract.StepsEntry.COLUMN_STEPS_DATE + " TEXT NOT NULL, "
                + TreeContract.StepsEntry.COLUMN_STEPS_VALUE + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_TABLE_MAIN);
        db.execSQL(SQL_CREATE_TABLE_DIARY);
        db.execSQL(SQL_CREATE_TABLE_DONATION);
        db.execSQL(SQL_CREATE_TABLE_STEPS);
    }

    /**
     * Called when DB needs to be upgraded
     * As the DB is still at version 1, there's nothing to do be done here
     *
     * @param db to be upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
