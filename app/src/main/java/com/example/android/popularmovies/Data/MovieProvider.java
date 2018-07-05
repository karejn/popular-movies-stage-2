package com.example.android.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by ekzhu on 03.07.2018.
 */

public class MovieProvider extends ContentProvider {
    public static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDbHelper;
    private static final int MOVIE = 100;
    private static final int MOVIE_WITH_ID = 200;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.MovieEntry.TABLE_MOVIE + "/*", MOVIE_WITH_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {

        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unsupported URI :" + uri);
        }

    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selArgs, String sort) {
        Cursor retCursor;

        try {


            switch (sUriMatcher.match(uri)) {
                case MOVIE: {
                    retCursor = mDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_MOVIE, projection, selection, selArgs, null, null, sort);
                    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return retCursor;

                }
                case MOVIE_WITH_ID: {
                    retCursor = mDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_MOVIE, projection, MovieContract.MovieEntry.COLUMN_ID, new String[]{String.valueOf(ContentUris.parseId(uri))}, null, null, sort);
                    retCursor.setNotificationUri(getContext().getContentResolver(), uri);

                    return retCursor;

                }
                default:
                    throw new UnsupportedOperationException("Unsupported Uri :" + uri);
            }

        } catch (SQLiteException ex) {
            Log.e(TAG, "Failed.");
            return null;
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_MOVIE, null, contentValues);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMoviesUri(_id);

                } else
                    throw new android.database.SQLException("Unsupported URI :" + uri);
                break;

            }
            default:
                throw new UnsupportedOperationException("Unknown URI :" + uri);


        }
        getContext().getContentResolver().notifyChange(uri, null);


        db.close();
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_MOVIE, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);

        }


        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_MOVIE, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI :" + uri);
        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;

    }
}
