package com.example.android.moviebox.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MoviesProvider extends ContentProvider {

    public static final int CODE_ALL_MOVIES = 100;
    public static final int CODE_MOVIE_WITH_ID= 101;
    public static final int CODE_POPULAR_MOVIES = 102;
    public static final int CODE_TOP_RATED_MOVIES = 103;
    public static final int CODE_FAVORITE_MOVIES = 104;


    private MoviesDbHelper mMovieDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, CODE_ALL_MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" + MoviesContract.PATH_POPULAR, CODE_POPULAR_MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" + MoviesContract.PATH_TOP_RATED, CODE_TOP_RATED_MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" + MoviesContract.PATH_FAVORITE, CODE_FAVORITE_MOVIES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        switch(sUriMatcher.match(uri)) {
            case CODE_ALL_MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for(ContentValues value : values) {
                        long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }


    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch(match) {
            case CODE_ALL_MOVIES:
                retCursor = db.query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MOVIE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                String selectionId = "movie_id=?";
                String[] selectionIdArgs = new String[]{movieId};

                retCursor = db.query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selectionId,
                        selectionIdArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_POPULAR_MOVIES:
                String selectionPopular = "popular=?";
                String isPopular = "1";
                String[] selectionPopularArgs = new String[]{isPopular};

                retCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selectionPopular,
                        selectionPopularArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_TOP_RATED_MOVIES:
                String selectionTopRated = "top_rated=?";
                String isTopRated = "1";
                String[] selectionTopRatedArgs = new String[]{isTopRated};

                retCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selectionTopRated,
                        selectionTopRatedArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FAVORITE_MOVIES:
                String selectionFavorite = "favorite=?";
                String isFavorite = "1";
                String[] selectionFavoriteArgs = new String[]{isFavorite};

                retCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selectionFavorite,
                        selectionFavoriteArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numRowsDeleted;

        switch (sUriMatcher.match(uri)) {

            case CODE_ALL_MOVIES:
                numRowsDeleted = mMovieDbHelper.getWritableDatabase().delete(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @NonNull ContentValues values, @NonNull String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int rowsUpdated = 0;
        int match = sUriMatcher.match(uri);

        switch(match) {
            case CODE_MOVIE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                String mSelection = "movie_id=?";
                String[] mSelectionArgs = new String[]{movieId};

                db.update(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        values,
                        mSelection,
                        mSelectionArgs);

                rowsUpdated++;

                if (rowsUpdated > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsUpdated;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
    }


}
