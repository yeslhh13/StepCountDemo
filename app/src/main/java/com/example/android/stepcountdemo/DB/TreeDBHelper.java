package com.example.android.stepcountdemo.DB;

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
     * TODO:Name it
     */
    public static final String DATABASE_NAME = "";
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
        /**
         * TODO:Create a String that contains the SQL statement to create table
         */
//        String SQL_CREATE_TABLE=
        /**
         * TODO:execute SQL statement
         */
//        db.execSQL(SQL_CREATE_TABLE);
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
