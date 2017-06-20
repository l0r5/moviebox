package com.example.android.moviebox.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.android.moviebox.data.MoviesContract;

import static com.example.android.moviebox.ui.MainActivity.GET_ALL_MOVIES_DB_LOADER_ID;



public class DbTaskHandler implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DbTaskHandler.class.getSimpleName();
    private FetchMovieFromDbCallback mCallback;
    private Context mContext;


    public DbTaskHandler(FetchMovieFromDbCallback callback, Context context) {
        this.mCallback = callback;
        this.mContext = context;
    }


    public interface FetchMovieFromDbCallback {
        void swapCursor(Cursor newCursor);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        final Uri uri;

        switch (loaderId) {

            case GET_ALL_MOVIES_DB_LOADER_ID:
                uri = MoviesContract.MoviesEntry.CONTENT_URI;
                return new CursorLoader(mContext,
                        uri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCallback.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallback.swapCursor(null);
    }
}
