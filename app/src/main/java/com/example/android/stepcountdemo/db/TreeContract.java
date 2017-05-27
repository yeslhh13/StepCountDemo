package com.example.android.stepcountdemo.db;

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
    private static final String CONTENT_AUTHORITY = "com.example.android.stepcountdemo";
    /**
     * Possible path appended to base content URI for possible URI
     */
    private static final String PATH_MAIN = "tree";
    private static final String PATH_DIARY = "diary";
    private static final String PATH_DONATION = "donation";
    private static final String PATH_STEPS = "steps";
    /**
     * Use {@link #CONTENT_AUTHORITY} to create the base of all URI's which will use to contract the content provider
     */
    private static final Uri CONTENT_URI_MAIN = Uri.parse("content://" + CONTENT_AUTHORITY + "/" + PATH_MAIN);
    private static final Uri CONTENT_URI_DIARY = Uri.parse("content://" + CONTENT_AUTHORITY + "/" + PATH_DIARY);
    private static final Uri CONTENT_URI_DONATION = Uri.parse("content://" + CONTENT_AUTHORITY + "/" + PATH_DONATION);
    private static final Uri CONTENT_URI_STEPS = Uri.parse("content://" + CONTENT_AUTHORITY + "/" + PATH_STEPS);

    /**
     * To prevent from accidentally instantiating the contract class
     */
    public TreeContract() {
    }

    /**
     * Inner class that defines constant values for the tree DB table
     * Each entry in the table represents a single tree value
     */
    public static final class MainEntry implements BaseColumns {
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of trees
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MAIN;
        /**
         * Name of DB table for main trees
         */
        public static final String TABLE_NAME = PATH_MAIN;
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TREE_TYPE = "treeType";
        public final static String COLUMN_TREE_NAME = "name";
        public final static String COLUMN_TREE_LEVEL = "level";
        /**
         * The content Uri to access the tree data in the provider
         */
        private static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_URI_MAIN, PATH_MAIN);
    }

    /**
     * Inner class that defines constant values for the diary DB table
     * Each entry in the table represents a single day value
     */
    public static final class DiaryEntry implements BaseColumns {
        /**
         * The content Uri to access the diary data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_URI_DIARY, PATH_DIARY);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of diary
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DIARY;
        /**
         * Name of DB table for steps
         */
        public static final String TABLE_NAME = PATH_DIARY;

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_DIARY_TITLE = "title";
        public final static String COLUMN_DIARY_CONTENT = "content";
        public final static String COLUMN_DIARY_IMAGE = "image";
    }

    /**
     * Inner class that defines constant values for the donation DB table
     * Each entry in the table represents a single donation value
     */
    public static final class DonationEntry implements BaseColumns {
        /**
         * The content Uri to access the donation data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_URI_DONATION, PATH_DONATION);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of donations
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DONATION;
        /**
         * Name of DB table for donation
         */
        public static final String TABLE_NAME = PATH_DONATION;

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_DONATION_TITLE = "title";
        public final static String COLUMN_DONATION_MONEY = "money";
        public final static String COLUMN_DONATION_IMAGE = "image";
    }

    /**
     * Inner class that defines constant values for the steps DB table
     * Each entry in the table represents a single day value
     */
    public static final class StepsEntry implements BaseColumns {
        /**
         * The content Uri to access the steps data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_URI_STEPS, PATH_STEPS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of steps
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STEPS;
        /**
         * Name of DB table for steps
         */
        public static final String TABLE_NAME = PATH_STEPS;

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_STEPS_DATE = "date";
        public final static String COLUMN_STEPS_VALUE = "value";
    }
}
