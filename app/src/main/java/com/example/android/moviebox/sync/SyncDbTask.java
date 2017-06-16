package com.example.android.moviebox.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.moviebox.data.MoviesContract;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.utilities.DataFormatUtils;
import com.example.android.moviebox.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.android.moviebox.MainActivity.POPULAR_MOVIES;
import static com.example.android.moviebox.MainActivity.TOP_RATED_MOVIES;


class SyncDbTask {

    synchronized static void syncMovies(Context context) {

        Movie[] fetchedPopularMovies = fetchMovies(context, NetworkUtils.getPath(POPULAR_MOVIES));
        Movie[] fetchedTopRatedMovies = fetchMovies(context, NetworkUtils.getPath(TOP_RATED_MOVIES));

        Cursor allMoviesCursor = null;
        Cursor favoriteMoviesCursor = null;

        if (fetchedPopularMovies != null && fetchedTopRatedMovies != null) {
            Movie[] allFetchedMovies = Movie.concatMovies(fetchedPopularMovies, fetchedTopRatedMovies);

            if (allFetchedMovies.length != 0) {

                ContentValues[] contentValuesFetchedMovies = DataFormatUtils.getContentValuesArrayFromMovieArray(allFetchedMovies);

                allMoviesCursor = getAllMoviesFromDb(context);

                if (allMoviesCursor != null) {
                    allMoviesCursor.moveToFirst();
                    if (checkIfTableIsEmpty(allMoviesCursor)) {
                        fillEmptyTable(context, contentValuesFetchedMovies);
                    } else {
                        Movie[] allPersistedMovies = DataFormatUtils.getMoviesFromCursor(allMoviesCursor);

                        // Create Arraylist from DB
                        ArrayList<Movie> persistedMoviesList = new ArrayList<Movie>(Arrays.asList(allPersistedMovies));

                        // Create Arraylist from Fetched Movies
                        ArrayList<Movie> fetchedMoviesList = new ArrayList<Movie>(Arrays.asList(allFetchedMovies));


                        if (!(persistedMoviesList.containsAll(fetchedMoviesList))) {
                            favoriteMoviesCursor = getFavoriteMoviesFromDb(context);

                            if (favoriteMoviesCursor != null) {
                                allMoviesCursor.moveToFirst();
                                Movie[] favoriteMovies = DataFormatUtils.getMoviesFromCursor(favoriteMoviesCursor);

                                ArrayList<Movie> favoriteMoviesList = new ArrayList<Movie>(Arrays.asList(favoriteMovies));

                                ArrayList<Movie> resultMoviesList = concatMovieList(fetchedMoviesList, favoriteMoviesList);

                                ContentValues[] resultContentValues = DataFormatUtils.getContentValuesArrayFromMovieArray(resultMoviesList.toArray(new Movie[resultMoviesList.size()]));

                                Log.d("Bla", "" + resultMoviesList);

                                deleteTable(context);

                                // bulk insert into table
                                fillEmptyTable(context, resultContentValues);

                            }
                        }
                    }
                }
            } // TODO if fetch not possible, load data straight from db
        }

        if(allMoviesCursor != null) {
            allMoviesCursor.close();
        }

        if(favoriteMoviesCursor != null) {
            favoriteMoviesCursor.close();
        }


    }

    private static ArrayList<Movie> concatMovieList(ArrayList<Movie> movieListFetched, ArrayList<Movie> movieListFavorite) {

        for(Movie movieFav : movieListFavorite) {
            for(Movie movieFetched : movieListFetched) {
                if(movieFav.getId().equals(movieFetched.getId())) {
                    movieListFetched.remove(movieFetched);
                    movieListFetched.add(movieFav);
                }
            }
        }
        return movieListFetched;
    }

    private static void deleteTable(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null);

    }

    private static void fillEmptyTable(Context context, ContentValues[] values) {
        ContentResolver resolver = context.getContentResolver();
        resolver.bulkInsert(
                MoviesContract.MoviesEntry.CONTENT_URI,
                values);

    }

    private static boolean checkIfTableIsEmpty(Cursor cursor) {
        return (!(cursor.moveToFirst()) || cursor.getCount() == 0);
    }

    private static Cursor getFavoriteMoviesFromDb(Context context) {
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

    private static Cursor getAllMoviesFromDb(Context context) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.query(
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    private static Movie[] fetchMovies(Context context, String moviesChoicePath) {
        try {
            URL movieRequestUrl = NetworkUtils.buildUrl(context, moviesChoicePath);
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            Movie[] movieData = DataFormatUtils.getMovieObjectsFromJson(jsonMovieResponse);
            return movieData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
