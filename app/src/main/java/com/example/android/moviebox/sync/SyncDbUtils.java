package com.example.android.moviebox.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.moviebox.data.MoviesContract;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.utilities.DataFormatUtils;
import com.example.android.moviebox.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import static com.example.android.moviebox.ui.MainActivity.POPULAR_MOVIES;
import static com.example.android.moviebox.ui.MainActivity.TOP_RATED_MOVIES;


public class SyncDbUtils {

    private static boolean sInitialized;

    synchronized public static void initialize(@NonNull final Context context) {

        if(sInitialized) return;

        sInitialized = true;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                startImmediateSync(context);


                return null;
            }
        }.execute();
    }

    private static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, SyncDbIntentService.class);
        intentToSyncImmediately.setAction(SyncDbIntentService.ACTION_SYNC_MOVIE_DB);
        context.startService(intentToSyncImmediately);
    }

    static Movie[] fetchMovies(Context context, String moviesChoicePath) {
        try {
            URL movieRequestUrl = NetworkUtils.buildUrl(context, moviesChoicePath);
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            Movie[] movieData = DataFormatUtils.getMovieObjectsFromJson(jsonMovieResponse);
            setCategory(movieData, moviesChoicePath);
            return movieData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static Cursor getAllMoviesFromDb(Context context) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.query(
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    static Cursor getFavoriteMoviesFromDb(Context context) {
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon()
                .appendPath("favorite")
                .build();
        ContentResolver resolver = context.getContentResolver();
        return resolver.query(
                uri,
                null,
                null,
                null,
                null
        );
    }

    static void setCategory (Movie[] movies, String category) {
        switch(category) {
            case TOP_RATED_MOVIES:
                for(Movie movie : movies) {
                    movie.setTopRated(1);
                }
                break;
            case POPULAR_MOVIES:
                for(Movie movie : movies) {
                    movie.setPopular(1);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown category: " + category);
        }
    }

    static ArrayList<Movie> concatMovieList(ArrayList<Movie> movieListFetched, ArrayList<Movie> movieListFavorite) {

        ArrayList<Movie> resultList = new ArrayList<Movie>();
        resultList.addAll(movieListFetched);

        for(Movie movieFav : movieListFavorite) {
            for(Movie movieFetched : movieListFetched) {
                if(movieFav.getId().equals(movieFetched.getId())) {
                    resultList.remove(movieFetched);
                }
            }
        }
        resultList.addAll(movieListFavorite);
        return resultList;
    }

    static void deleteTable(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null);

    }

    static void fillEmptyTable(Context context, ContentValues[] values) {
        ContentResolver resolver = context.getContentResolver();
        resolver.bulkInsert(
                MoviesContract.MoviesEntry.CONTENT_URI,
                values);

    }

    static boolean checkIfTableIsEmpty(Context context) {
        Cursor cursor = SyncDbUtils.getAllMoviesFromDb(context);
        boolean empty = false;
        if(null == cursor || cursor.getCount() == 0) {
            empty = true;
        }
        cursor.close();
        return empty;
    }

}
