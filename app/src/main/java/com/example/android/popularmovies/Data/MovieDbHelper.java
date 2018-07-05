package com.example.android.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ekzhu on 03.07.2018.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 4;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_MOVIE + "("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL,"
                + MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_MOVIE);
        onCreate(sqLiteDatabase);
    }
}
