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


public class FetchFromDbTask implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = FetchFromDbTask.class.getSimpleName();
    private FetchMovieFavoriteCallback mCallback;
    private Context mContext;
    private String mMovieId;

    public FetchFromDbTask(FetchMovieFavoriteCallback callback, Context context, String mMovieId) {
        this.mCallback = callback;
        this.mContext = context;
        this.mMovieId = mMovieId;
    }

    public interface FetchMovieFavoriteCallback {
        void swapCursor(Cursor newCursor);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {


        return new AsyncTaskLoader<Cursor>(mContext) {
            Cursor mMovieData;
            @Override
            protected void onStartLoading() {
                if(mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(mMovieId).build();
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
