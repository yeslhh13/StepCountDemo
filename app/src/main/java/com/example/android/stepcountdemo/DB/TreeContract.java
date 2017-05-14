package com.example.android.stepcountdemo.DB;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Kat on 2017-05-14.
 * <p>
 * API contract
 */

public final class TreeContract {
    /**
     * Name for the entire content provider
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.stepcountdemo";
    /**
     * Use {@link #CONTENT_AUTHORITY} to create the base of all URI's which will use to contract the content provider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path appended to base content URI for possible URI
     */
    public static final String PATH_STEPS = "steps";

    /**
     * To prevent from accidentally instantiating the contract class
     */
    public TreeContract() {
    }

    /**
     * Inner class that defines constant values for the steps DB table
     * Each entry in the table represents a single day value
     */
    public static final class TreeEntry implements BaseColumns {
        /**
         * The content Uri to access the steps data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STEPS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of steps
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STEPS;
        /**
         * Name of DB table for steps
         */
        public static final String TABLE_NAME = "steps";
        /**
         * TODO:make final static String values for the table column name
         */
    }
}
