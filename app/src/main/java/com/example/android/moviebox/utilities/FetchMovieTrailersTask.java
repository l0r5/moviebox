package com.example.android.moviebox.utilities;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.android.moviebox.models.Trailer;

import java.net.URL;

public class FetchMovieTrailersTask implements LoaderManager.LoaderCallbacks<Trailer[]> {

    private FetchMovieTrailersCallback mCallback;
    private Context mContext;
    private String mMovieId;

    public FetchMovieTrailersTask(FetchMovieTrailersCallback callback,Context context, String movieId) {
        this.mCallback = callback;
        this.mContext = context;
        this.mMovieId = movieId;
    }

    public interface FetchMovieTrailersCallback {
        void onTrailerTaskCompleted(Trailer[] trailerData);
    }

    @Override
    public Loader<Trailer[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Trailer[]>(mContext) {

            Trailer[] trailerData = null;

            @Override
            protected void onStartLoading() {
                if (trailerData != null) {
                    deliverResult(trailerData);
                } else {
                    // TODO loading indicator
                    forceLoad();
                }
            }

            @Override
            public Trailer[] loadInBackground() {
                URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(mContext, mMovieId);

                try {
                    String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl);

                    Trailer[] trailerData = OpenJsonUtils.getTrailerObjectsFromJson(jsonTrailerResponse);

                    return trailerData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Trailer[] data) {
                trailerData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Trailer[]> loader, Trailer[] data) {
        mCallback.onTrailerTaskCompleted(data);
    }

    @Override
    public void onLoaderReset(Loader<Trailer[]> loader) {

    }
}
