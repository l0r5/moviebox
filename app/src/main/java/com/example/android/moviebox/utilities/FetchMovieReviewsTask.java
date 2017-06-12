package com.example.android.moviebox.utilities;


import android.support.v4.app.LoaderManager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.android.moviebox.models.Review;

import java.net.URL;



public class FetchMovieReviewsTask implements LoaderManager.LoaderCallbacks<Review[]> {

    private FetchMovieReviewsCallback mCallback;
    private Context mContext;
    private String mMovieId;

    public FetchMovieReviewsTask(FetchMovieReviewsCallback callback,Context context, String movieId) {
        this.mCallback = callback;
        this.mContext = context;
        this.mMovieId = movieId;
    }

    public interface FetchMovieReviewsCallback {
        void onReviewTaskCompleted(Review[] reviewData);
    }

    @Override
    public Loader<Review[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Review[]>(mContext) {

            Review[] reviewData = null;

            @Override
            protected void onStartLoading() {
                if (reviewData != null) {
                    deliverResult(reviewData);
                } else {
                    // TODO loading indicator
                    forceLoad();
                }
            }

            @Override
            public Review[] loadInBackground() {
                URL reviewRequestUrl = NetworkUtils.buildReviewUrl(mContext, mMovieId);

                try {
                    String jsonReviewResponse = NetworkUtils.getResponseFromHttpUrl(reviewRequestUrl);

                    Review[] reviewData = DataFormatUtils.getReviewObjectsFromJson(jsonReviewResponse);

                    return reviewData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Review[] data) {
                reviewData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Review[]> loader, Review[] data) {
        mCallback.onReviewTaskCompleted(data);
    }

    @Override
    public void onLoaderReset(Loader<Review[]> loader) {

    }
}
