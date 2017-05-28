package com.example.android.stepcountdemo.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Kat on 2017-05-14.
 * <p>
 * {@link ContentProvider}for step counting
 */

public class TreeProvider extends ContentProvider {
    /**
     * Setup UriMatcher codes for the content URIs
     */
    private static final int MAIN = 100;
    private static final int MAIN_ID = 200;

    private static final int DIARY = 300;
    private static final int DIARY_ID = 400;

    private static final int DONATION = 500;
    private static final int DONATION_ID = 600;

    private static final int STEPS = 700;
    private static final int STEPS_ID = 800;

    /**
     * {@link UriMatcher} object to match a content URI to a corresponding code
     * The input passed in to the constructor represents the code to return for the root URI
     */
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Static initializer
     * Run the first time anything is called from this class
     */
    static {
        /**
         * Provide access to MULTIPLE rows of the table
         */
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_MAIN, MAIN);
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_DIARY, DIARY);
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_DONATION, DONATION);
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_STEPS, STEPS);

        /**
         * Provide access to SINGLE row of the table
         */
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_MAIN, MAIN_ID);
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_DIARY, DIARY_ID);
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_DONATION, DONATION_ID);
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_STEPS, STEPS_ID);
    }

    /**
     * DB helper object
     */
    private TreeDBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new TreeDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        /**
         * Get readable database
         */
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        /**
         * This will hold the result of the query
         */
        Cursor cursor;

        /**
         * Figure out if the URI matcher can match the URI to a specific code
         */
        int match = mUriMatcher.match(uri);

        /**
         * Query the product table directly with the given projection, selection, selection arguments, and sort order
         *
         * If the match is #MAIN, #DIARY, #DONATION, #STEPS the cursor could contain multiple rows of the table
         *
         * If not(_ID), extract the ID from the URI and for every "?" in the selection,
         * we need to have an element in the selection arguments that will fill in the "?"
         * Since we have 1 question mark in the selection, we have 1 String in the selection arguments' String array
         */
        switch (match) {
            case MAIN:
                cursor = database.query(TreeContract.MainEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MAIN_ID:
                selection = TreeContract.MainEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TreeContract.MainEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case DIARY:
                cursor = database.query(TreeContract.DiaryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case DIARY_ID:
                selection = TreeContract.DiaryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TreeContract.DiaryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case DONATION:
                cursor = database.query(TreeContract.DonationEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case DONATION_ID:
                selection = TreeContract.DonationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TreeContract.DonationEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case STEPS:
                cursor = database.query(TreeContract.StepsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case STEPS_ID:
                selection = TreeContract.StepsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TreeContract.StepsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        /**
         * Set notification URI on the cursor
         * If the data at this URI changes, then we know we need to update the Cursor
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MAIN:
                return TreeContract.MainEntry.CONTENT_LIST_TYPE;
            case MAIN_ID:
                return TreeContract.MainEntry.CONTENT_ITEM_TYPE;
            case DIARY:
                return TreeContract.DiaryEntry.CONTENT_LIST_TYPE;
            case DIARY_ID:
                return TreeContract.DiaryEntry.CONTENT_ITEM_TYPE;
            case DONATION:
                return TreeContract.DonationEntry.CONTENT_LIST_TYPE;
            case DONATION_ID:
                return TreeContract.DonationEntry.CONTENT_ITEM_TYPE;
            case STEPS:
                return TreeContract.StepsEntry.CONTENT_LIST_TYPE;
            case STEPS_ID:
                return TreeContract.StepsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case MAIN:
                return insertTree(uri, values);
            case DIARY:
                return insertDiary(uri, values);
            case DONATION:
                return insertDonation(uri, values);
            case STEPS:
                return insertStep(uri, values);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        /**
         * Get writable database
         */
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        /**
         * Track the number of rows that were deleted
         */
        int rowsDeleted;

        final int match = mUriMatcher.match(uri);
        /**
         * If the match is #MAIN, #DIARY, #DONATION, #STEPS delete all rows that match the selection and selection arguments
         * If not(_ID), delete a single row given by the ID in the URI
         */
        switch (match) {
            case MAIN:
                rowsDeleted = database.delete(TreeContract.MainEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MAIN_ID:
                selection = TreeContract.MainEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TreeContract.MainEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DIARY:
                rowsDeleted = database.delete(TreeContract.DiaryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DIARY_ID:
                selection = TreeContract.DiaryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TreeContract.DiaryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DONATION:
                rowsDeleted = database.delete(TreeContract.DonationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DONATION_ID:
                selection = TreeContract.DonationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TreeContract.DonationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STEPS:
                rowsDeleted = database.delete(TreeContract.StepsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STEPS_ID:
                selection = TreeContract.StepsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TreeContract.StepsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion not supported for " + uri);
        }

        /**
         * If 1 or more rows were deleted, then notify all listeners that the data at the given URI has changed
         */
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        /**
         * Return the number of rows deleted
         */
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = mUriMatcher.match(uri);
        /**
         * For the _ID matcher, extract the ID from the URI to know which row to update
         * Selection will be "_ID=?" and selection arguments will be a String array containing the actual ID
         */
        switch (match) {
            case MAIN:
                return updateTree(uri, values, selection, selectionArgs);
            case MAIN_ID:
                selection = TreeContract.MainEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTree(uri, values, selection, selectionArgs);
            case DIARY:
                return updateDiary(uri, values, selection, selectionArgs);
            case DIARY_ID:
                selection = TreeContract.DiaryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateDiary(uri, values, selection, selectionArgs);
            case DONATION:
                return updateDonation(uri, values, selection, selectionArgs);
            case DONATION_ID:
                selection = TreeContract.DonationEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateDonation(uri, values, selection, selectionArgs);
            case STEPS:
                return updateStep(uri, values, selection, selectionArgs);
            case STEPS_ID:
                selection = TreeContract.StepsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStep(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for " + uri);
        }
    }

    private Uri insertTree(Uri uri, ContentValues values) {
        return null;
    }

    private int updateTree(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private Uri insertDiary(Uri uri, ContentValues values) {
        return null;
    }

    private int updateDiary(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private Uri insertDonation(Uri uri, ContentValues values) {
        return null;
    }

    private int updateDonation(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private Uri insertStep(Uri uri, ContentValues values) {
        return null;
    }

    private int updateStep(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}