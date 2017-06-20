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
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.android.moviebox.ui.MainActivity.POPULAR_MOVIES;
import static com.example.android.moviebox.ui.MainActivity.TOP_RATED_MOVIES;


public class SyncDbUtils {

    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    private static final String MOVIEBOX_SYNC_TAG = "moviebox-sync";

    static void scheduleFirebaseJobDispatcher(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncMoviesJob = dispatcher.newJobBuilder()
                .setService(FirebaseJobService.class)
                .setTag(MOVIEBOX_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS, SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncMoviesJob);

    }

    synchronized public static void initialize(@NonNull final Context context) {

        if(sInitialized) return;

        sInitialized = true;

        scheduleFirebaseJobDispatcher(context);

       new Thread(new Runnable() {
            @Override
            public void run() {
                startImmediateSync(context);

            }
        }).start();
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
