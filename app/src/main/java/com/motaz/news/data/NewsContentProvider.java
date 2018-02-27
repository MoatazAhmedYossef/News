package com.motaz.news.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import static com.motaz.news.data.NewsContract.ReadLaterNewsEntry.TABLE_NAME;

/**
 * Created by moatz on 14/02/18.
 */

public class NewsContentProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int READ_LATER_NEWS = 100;
    private static final int READ_LATER_WITH_ID = 101;

    private static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(NewsContract.AUTHORITY, NewsContract.PATH_READ_LATER_NEWS, READ_LATER_NEWS);
        uriMatcher.addURI(NewsContract.AUTHORITY, NewsContract.PATH_READ_LATER_NEWS + "/#", READ_LATER_WITH_ID);
        return uriMatcher;
    }

    private NewsDbHelper mNewsDbHelper;
    @Override
    public boolean onCreate() {
        mNewsDbHelper = new NewsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mNewsDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case READ_LATER_NEWS:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mNewsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned
        switch (match) {
            case READ_LATER_NEWS:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(TABLE_NAME, null, contentValues);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(NewsContract.ReadLaterNewsEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String where, @Nullable String[] deleteConditions) {
        final SQLiteDatabase db = mNewsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int newsDeleted;
        switch (match) {
            case READ_LATER_NEWS:
                newsDeleted = db.delete(TABLE_NAME,where,deleteConditions);
                break;
            case READ_LATER_WITH_ID:
                String id = uri.getPathSegments().get(1);
                newsDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (newsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return newsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mNewsDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int newsUpdated; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case READ_LATER_NEWS:
                // Get the task ID from the URI path
                // Use selections/selectionArgs to filter for this ID
                newsUpdated = db.update(TABLE_NAME, contentValues,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (newsUpdated != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return newsUpdated;
    }

    //TODO
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}
