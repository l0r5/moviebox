package com.example.android.moviebox.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.utilities.DataFormatUtils;
import com.example.android.moviebox.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.android.moviebox.ui.MainActivity.POPULAR_MOVIES;
import static com.example.android.moviebox.ui.MainActivity.TOP_RATED_MOVIES;


class SyncDbTask {

    private static final String TAG = SyncDbTask.class.getSimpleName();

    synchronized static void syncMovies(Context context) {

        Log.i(TAG, "Start DB synchronization");

        // Fetch Movies from TheMovieDB
        Movie[] fetchedPopularMovies = SyncDbUtils.fetchMovies(context, NetworkUtils.getPath(POPULAR_MOVIES));
        Movie[] fetchedTopRatedMovies = SyncDbUtils.fetchMovies(context, NetworkUtils.getPath(TOP_RATED_MOVIES));

        if (fetchedPopularMovies == null || fetchedTopRatedMovies == null) { // if network connection fails, get data from db
            // Get all persisted Movies from the Db
            Cursor cursor = SyncDbUtils.getAllMoviesFromDb(context);
            cursor.moveToFirst();
            cursor.close();
        } else { // else compare the two data sets

            // Make one Movie Array out of the fetched single parts
            Movie[] allFetchedMovies = Movie.concatMovies(fetchedPopularMovies, fetchedTopRatedMovies);

            // Check if Db is empty -> if yes, fill in the fetched Data
            if (SyncDbUtils.checkIfTableIsEmpty(context)) {
                ContentValues[] contentValuesFetchedMovies = DataFormatUtils.getContentValuesArrayFromMovieArray(allFetchedMovies);
                SyncDbUtils.fillEmptyTable(context, contentValuesFetchedMovies);
                return;
            }

            // Get all persisted Movies from the Db
            Cursor cursor = SyncDbUtils.getAllMoviesFromDb(context);
            cursor.moveToFirst();
            Movie[] allPersistedMovies = DataFormatUtils.getMoviesFromCursor(cursor);
            cursor.close();

            // Create Arraylists of the two data sets
            ArrayList<Movie> persistedMoviesList = new ArrayList<Movie>(Arrays.asList(allPersistedMovies));
            ArrayList<Movie> fetchedMoviesList = new ArrayList<Movie>(Arrays.asList(allFetchedMovies));

            // check if there is new data
            if (persistedMoviesList.containsAll(fetchedMoviesList)) {
                Log.i(TAG, "The persisted Movie Data is up-to-date");
            } else {
                // Compate the two datasets and persist the new version to the db
                updateDb(context, fetchedMoviesList);
            }
        }
    }

    private static void updateDb(Context context, ArrayList<Movie> fetchedMoviesList) {

        // Check if the user already has favorite movies
        Cursor cursor = SyncDbUtils.getFavoriteMoviesFromDb(context);
        if (cursor == null || cursor.getCount() == 0) {
            return;
        }

        // fetch all movies that are marked as favorite and save them in a list
        cursor.moveToFirst();
        Movie[] favoriteMovies = DataFormatUtils.getMoviesFromCursor(cursor);
        cursor.close();
        ArrayList<Movie> favoriteMoviesList = new ArrayList<Movie>(Arrays.asList(favoriteMovies));

        // Compare and concat the fetched & favorite
        ArrayList<Movie> resultMoviesList = SyncDbUtils.concatMovieList(fetchedMoviesList, favoriteMoviesList);
        Movie[] resultMovies = resultMoviesList.toArray(new Movie[resultMoviesList.size()]);

        // Delete Table
        SyncDbUtils.deleteTable(context);

        // bulk insert into table
        ContentValues[] resultContentValues = DataFormatUtils.getContentValuesArrayFromMovieArray(resultMovies);
        SyncDbUtils.fillEmptyTable(context, resultContentValues);
    }
}



