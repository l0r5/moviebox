package com.example.android.moviebox.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.example.android.moviebox.data.MoviesContract;

import static com.example.android.moviebox.DetailActivity.FETCH_MOVIE_WITH_ID_LOADER_ID;
import static com.example.android.moviebox.MainActivity.FETCH_ALL_MOVIES_DB_LOADER_ID;


public class FetchFromDbTask implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = FetchFromDbTask.class.getSimpleName();
    private FetchMovieFromDbCallback mCallback;
    private Context mContext;
    private String mMovieId;

    public FetchFromDbTask(FetchMovieFromDbCallback callback, Context context) {
        this.mCallback = callback;
        this.mContext = context;
    }

    public FetchFromDbTask(FetchMovieFromDbCallback callback, Context context, String mMovieId) {
        this.mCallback = callback;
        this.mContext = context;
        this.mMovieId = mMovieId;
    }

    public interface FetchMovieFromDbCallback {
        void swapCursor(Cursor newCursor);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        final Uri uri;

        switch (loaderId) {
            case FETCH_MOVIE_WITH_ID_LOADER_ID:
                uri = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon()
                        .appendPath(mMovieId)
                        .build();
                break;
            case FETCH_ALL_MOVIES_DB_LOADER_ID:
                uri = MoviesContract.MoviesEntry.CONTENT_URI;
                break;
            default:
                throw new UnsupportedOperationException("Unknow loader id: " + loaderId);
        }

        return new AsyncTaskLoader<Cursor>(mContext) {
            Cursor mMovieData;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {

                    return mContext.getContentResolver().query(
                            uri,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCallback.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
