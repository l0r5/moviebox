package com.example.android.moviebox.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.moviebox.MainActivity;
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

    private static final String TAG = SyncDbTask.class.getSimpleName();

    synchronized static void syncMovies(Context context) {

        Movie[] fetchedPopularMovies = fetchMovies(context, NetworkUtils.getPath(POPULAR_MOVIES));
        Movie[] fetchedTopRatedMovies = fetchMovies(context, NetworkUtils.getPath(TOP_RATED_MOVIES));

        if (fetchedPopularMovies != null && fetchedTopRatedMovies != null) {
            Movie[] allFetchedMovies = Movie.concatMovies(fetchedPopularMovies, fetchedTopRatedMovies);

            if (allFetchedMovies.length != 0) {

                ContentValues[] contentValuesFetchedMovies = DataFormatUtils.getContentValuesArrayFromMovieArray(allFetchedMovies);

                Cursor allMoviesCursor = getAllMoviesFromDb(context);

                if (allMoviesCursor != null) {
                    allMoviesCursor.moveToFirst();
                    if (checkIfTableIsEmpty(allMoviesCursor)) {
                        fillEmptyTable(context, contentValuesFetchedMovies);
                        allMoviesCursor.close();
                    } else {
                        Movie[] allPersistedMovies = DataFormatUtils.getMoviesFromCursor(allMoviesCursor);
                        allMoviesCursor.close();

                        // Create Arraylist from DB
                        ArrayList<Movie> persistedMoviesList = new ArrayList<Movie>(Arrays.asList(allPersistedMovies));

                        // Create Arraylist from Fetched Movies
                        ArrayList<Movie> fetchedMoviesList = new ArrayList<Movie>(Arrays.asList(allFetchedMovies));


                        if (!(persistedMoviesList.containsAll(fetchedMoviesList))) {
                            Cursor favoriteMoviesCursor = getFavoriteMoviesFromDb(context);

                            if (favoriteMoviesCursor != null) {
                                favoriteMoviesCursor.moveToFirst();
                                Movie[] favoriteMovies = DataFormatUtils.getMoviesFromCursor(favoriteMoviesCursor);
                                favoriteMoviesCursor.close();

                                ArrayList<Movie> favoriteMoviesList = new ArrayList<Movie>(Arrays.asList(favoriteMovies));


                                ArrayList<Movie> resultMoviesList = concatMovieList(fetchedMoviesList, favoriteMoviesList);



                                Movie[] resultMovies = resultMoviesList.toArray(new Movie[resultMoviesList.size()]);

                                ContentValues[] resultContentValues = DataFormatUtils.getContentValuesArrayFromMovieArray(resultMovies);


                                deleteTable(context);

                                // bulk insert into table
                                fillEmptyTable(context, resultContentValues);

                            }
                        }
                    }
                }
            } // TODO if fetch not possible, load data straight from db
        }
    }

    private static ArrayList<Movie> concatMovieList(ArrayList<Movie> movieListFetched, ArrayList<Movie> movieListFavorite) {

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
            setCategory(movieData, moviesChoicePath);
            return movieData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void setCategory (Movie[] movies, String category) {
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


}
