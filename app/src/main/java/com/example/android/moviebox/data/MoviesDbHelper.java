package com.example.android.moviebox.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.moviebox.data.MoviesContract.MoviesEntry;

public class MoviesDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 5;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +

                        MoviesEntry._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MoviesEntry.COLUMN_MOVIE_ID         + " INTEGER NOT NULL, "                 +
                        MoviesEntry.COLUMN_TITLE            + " TEXT NOT NULL, "                    +
                        MoviesEntry.COLUMN_RELEASE_DATE     + " TEXT NOT NULL, "                    +
                        MoviesEntry.COLUMN_RATING           + " REAL NOT NULL, "                    +
                        MoviesEntry.COLUMN_DESCRIPTION      + " TEXT NOT NULL, "                    +
                        MoviesEntry.COLUMN_FAVORITE         + " INTEGER NOT NULL, "                 +
                        MoviesEntry.COLUMN_TOP_RATED        + " INTEGER NOT NULL, "                 +
                        MoviesEntry.COLUMN_POPULAR          + " INTEGER NOT NULL, "                 +
                        MoviesEntry.COLUMN_THUMBNAIL_URL    + " TEXT NOT NULL, "                      +
                        MoviesEntry.COLUMN_POSTER_URL       + " TEXT NOT NULL"                      +

                        ");";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
