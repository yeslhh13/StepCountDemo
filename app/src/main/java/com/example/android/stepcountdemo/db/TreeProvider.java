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
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_MAIN + "/#", MAIN_ID);
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_DIARY + "/#", DIARY_ID);
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_DONATION + "/#", DONATION_ID);
        mUriMatcher.addURI(TreeContract.CONTENT_AUTHORITY, TreeContract.PATH_STEPS + "/#", STEPS_ID);
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

    /**
     * Insert a tree into the database with the given content values
     *
     * @return a new content URI for the specific row in the database
     */
    private Uri insertTree(Uri uri, ContentValues values) {
        /**
         * Check that the tree type is not null
         */
        String tree_type = values.getAsString(TreeContract.MainEntry.COLUMN_TREE_TYPE);
        if (tree_type == null)
            throw new IllegalArgumentException("트리의 종류를 선택해주세요!");

        /**
         * Get the writable database
         */
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        /**
         * Insert the new tree with the given values
         * If the ID is -1, then the insertion is failed
         */
        long id = database.insert(TreeContract.MainEntry.TABLE_NAME, null, values);
        if (id == -1)
            return null;

        /**
         * Notify all listeners that the data has changed for the tree content URI
         */
        getContext().getContentResolver().notifyChange(uri, null);

        /**
         * Return the new URI withe the ID of the newly inserted row appended at the end
         */
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update trees in the database with the given content values
     *
     * @return the number of rows that were successfully updated
     */
    private int updateTree(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        /**
         * If the key is present, check that the value is not null
         */
        if (values.containsKey(TreeContract.MainEntry.COLUMN_TREE_NAME)) {
            String tree_name = values.getAsString(TreeContract.MainEntry.COLUMN_TREE_NAME);
            if (tree_name == null)
                throw new IllegalArgumentException("이름을 입력해주세요!");
        }

        if (values.containsKey(TreeContract.MainEntry.COLUMN_TREE_TYPE)) {
            String tree_type = values.getAsString(TreeContract.MainEntry.COLUMN_TREE_TYPE);
            if (tree_type == null)
                throw new IllegalArgumentException("종류를 선택해주세요!");
        }

        if (values.containsKey(TreeContract.MainEntry.COLUMN_TREE_LEVEL)) {
            Integer tree_level = values.getAsInteger(TreeContract.MainEntry.COLUMN_TREE_LEVEL);
            if (tree_level != null && tree_level < 0)
                throw new IllegalArgumentException("레벨을 입력해주세요!");
        }

        /**
         * If there are no values to update, then don't try to update the database
         */
        if (values.size() == 0)
            return 0;

        /**
         * Otherwise, get the writable database to update the data
         */
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        /**
         * Perform the update on the database and get the number of rows affected
         */
        int rowsUpdated = database.update(TreeContract.MainEntry.TABLE_NAME, values, selection, selectionArgs);
        /**
         * If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed
         */
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        /**
         * Return the number of rows updated
         */
        return rowsUpdated;
    }

    /**
     * Insert a diary content into the database with the given content values
     *
     * @return a new content URI for the specific row in the database
     */
    private Uri insertDiary(Uri uri, ContentValues values) {
        String diary_title = values.getAsString(TreeContract.DiaryEntry.COLUMN_DIARY_TITLE);
        if (diary_title == null)
            throw new IllegalArgumentException("제목을 입력해주세요!");

        String diary_content = values.getAsString(TreeContract.DiaryEntry.COLUMN_DIARY_CONTENT);
        if (diary_content == null)
            throw new IllegalArgumentException("내용을 입력해주세요!");

        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        long id = database.insert(TreeContract.DiaryEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);

        if (id == -1)
            return null;

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update diary contents in the database with the given content values
     *
     * @return the number of rows that were successfully updated
     */
    private int updateDiary(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(TreeContract.DiaryEntry.COLUMN_DIARY_TITLE)) {
            String diary_title = values.getAsString(TreeContract.DiaryEntry.COLUMN_DIARY_TITLE);
            if (diary_title == null)
                throw new IllegalArgumentException("이름을 입력해주세요!");
        }

        if (values.containsKey(TreeContract.DiaryEntry.COLUMN_DIARY_CONTENT)) {
            String diary_content = values.getAsString(TreeContract.DiaryEntry.COLUMN_DIARY_CONTENT);
            if (diary_content == null)
                throw new IllegalArgumentException("내용을 입력해주세요!");
        }

        if (values.containsKey(TreeContract.DiaryEntry.COLUMN_DIARY_IMAGE)) {
            byte[] diary_image = values.getAsByteArray(TreeContract.DiaryEntry.COLUMN_DIARY_IMAGE);
            if (diary_image == null)
                throw new IllegalArgumentException("사진을 등록해주세요!");
        }

        if (values.size() == 0)
            return 0;

        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        int rowsUpdated = database.update(TreeContract.DiaryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    /**
     * Insert a donation content into the database with the given content values
     *
     * @return a new content URI for the specific row in the database
     */
    private Uri insertDonation(Uri uri, ContentValues values) {
        String donation_title = values.getAsString(TreeContract.DonationEntry.COLUMN_DONATION_TITLE);
        if (donation_title == null)
            throw new IllegalArgumentException("제목을 입력해주세요!");

        byte[] donation_picture = values.getAsByteArray(TreeContract.DonationEntry.COLUMN_DONATION_IMAGE);
        if (donation_picture == null)
            throw new IllegalArgumentException("사진을 등록해주세요!");

        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        long id = database.insert(TreeContract.DonationEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);

        if (id == -1)
            return null;

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update donation contents in the database with the given content values
     *
     * @return the number of rows that were successfully updated
     */
    private int updateDonation(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(TreeContract.DonationEntry.COLUMN_DONATION_TITLE)) {
            String donation_title = values.getAsString(TreeContract.DonationEntry.COLUMN_DONATION_TITLE);
            if (donation_title == null)
                throw new IllegalArgumentException("제목을 입력해주세요!");
        }

        if (values.containsKey(TreeContract.DonationEntry.COLUMN_DONATION_MONEY)) {
            Integer donation_money = values.getAsInteger(TreeContract.DonationEntry.COLUMN_DONATION_MONEY);
            if (donation_money != null && donation_money < 0)
                throw new IllegalArgumentException("금액을 입력해주세요!");
        }

        if (values.containsKey(TreeContract.DonationEntry.COLUMN_DONATION_IMAGE)) {
            byte[] donation_picture = values.getAsByteArray(TreeContract.DonationEntry.COLUMN_DONATION_IMAGE);
            if (donation_picture == null)
                throw new IllegalArgumentException("사진을 등록해주세요!");
        }

        if (values.size() == 0)
            return 0;

        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        int rowsUpdated = database.update(TreeContract.DonationEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    /**
     * Insert a step value into the database with the given content values
     *
     * @return a new content URI for the specific row in the database
     */
    private Uri insertStep(Uri uri, ContentValues values) {
        String steps_date = values.getAsString(TreeContract.StepsEntry.COLUMN_STEPS_DATE);
        if (steps_date == null)
            throw new IllegalArgumentException("날짜를 선택해주세요!");

        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        long id = database.insert(TreeContract.StepsEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);

        if (id == -1)
            return null;

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update step values in the database with the given content values
     *
     * @return the number of rows that were successfully updated
     */
    private int updateStep(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(TreeContract.StepsEntry.COLUMN_STEPS_DATE)) {
            String steps_date = values.getAsString(TreeContract.StepsEntry.COLUMN_STEPS_DATE);
            if (steps_date == null)
                throw new IllegalArgumentException("날짜를 선택해주세요!");
        }

        if (values.containsKey(TreeContract.StepsEntry.COLUMN_STEPS_VALUE)) {
            Integer steps_value = values.getAsInteger(TreeContract.StepsEntry.COLUMN_STEPS_VALUE);
            if (steps_value != null && steps_value < 0)
                throw new IllegalArgumentException("걸음수를 입력해주세요!");
        }

        if (values.size() == 0)
            return 0;

        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        int rowsUpdated = database.update(TreeContract.StepsEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}