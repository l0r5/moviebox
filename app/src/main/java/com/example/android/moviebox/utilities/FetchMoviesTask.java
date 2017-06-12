package com.example.android.moviebox.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.android.moviebox.data.MoviesContract;
import com.example.android.moviebox.models.Movie;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchMoviesTask implements LoaderManager.LoaderCallbacks<Movie[]> {

    private FetchMoviesCallback mDelegate;
    private Context mContext;
    private String mMoviesChoice;


    public FetchMoviesTask(FetchMoviesCallback callback, Context context, String moviesChoice) {
        this.mDelegate = callback;
        this.mContext = context;
        this.mMoviesChoice = moviesChoice;
    }

    public interface FetchMoviesCallback {
        void toggleLoadingIndicator(boolean onOffSwitch);

        void onTaskCompleted(Movie[] movies);
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Movie[]>(mContext) {

            Movie[] movieData = null;

            @Override
            protected void onStartLoading() {
                if (movieData != null) {
                    deliverResult(movieData);
                } else {
                    mDelegate.toggleLoadingIndicator(true);
                    forceLoad();
                }
            }

            @Override
            public Movie[] loadInBackground() {
                URL movieRequestUrl = NetworkUtils.buildUrl(mContext, mMoviesChoice);

                try {
                    String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                    Movie[] movieData = DataFormatUtils.getMovieObjectsFromJson(jsonMovieResponse);

                    return movieData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

            public void deliverResult(Movie[] data) {
                movieData = data;
                super.deliverResult(data);
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        mDelegate.onTaskCompleted(data);

        List<ContentValues> movieValues = new ArrayList<ContentValues>();
        for(Movie movie:data) {
            movieValues.add(DataFormatUtils.createMovieContentValues(movie));
        }

        mContext.getContentResolver().bulkInsert(
                MoviesContract.MoviesEntry.CONTENT_URI,
                movieValues.toArray(new ContentValues[movieValues.size()]));

    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }
}