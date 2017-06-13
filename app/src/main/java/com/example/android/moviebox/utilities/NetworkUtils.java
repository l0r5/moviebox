package com.example.android.moviebox.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.moviebox.R;
import com.example.android.moviebox.models.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.example.android.moviebox.MainActivity.POPULAR_MOVIES;
import static com.example.android.moviebox.MainActivity.TOP_RATED_MOVIES;


public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3";
    private static final String MOVIE_PATH = "movie";
    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH ="top_rated";
    private static final String API_KEY_PARAM = "api_key";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    private static final String IMAGE_SIZE_PATH = "w185";
    private static final String VIDEOS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com";
    private static final String YOUTUBE_WATCH_PATH ="watch";
    private static final String YOUTUBE_VIDEO_KEY_PARAM = "v";

    static URL buildUrl (Context context, String moviesChoice) {

        Uri builtUri = null;
        URL url = null;
        String apiKey = context.getString(R.string.THE_MOVIE_DB_API_KEY);

        if(moviesChoice.equals(POPULAR_PATH)) {
            builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                    .appendPath(MOVIE_PATH)
                    .appendPath(POPULAR_PATH)
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
        } else if(moviesChoice.equals(TOP_RATED_PATH)) {
            builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                    .appendPath(MOVIE_PATH)
                    .appendPath(TOP_RATED_PATH)
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build();
        }

        if(builtUri!=null) {
            try {
                url = new URL(builtUri.toString());
                Log.v(TAG, "Built API URL: " + url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return url;
    }

    static URL buildThumbnailUrl(String imagePath) {

        URL url = null;

        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE_PATH)
                .appendPath(imagePath)
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    static URL buildTrailerUrl(Context context, String movieId) {

        URL url = null;
        String apiKey = context.getString(R.string.THE_MOVIE_DB_API_KEY);

        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendPath(VIDEOS_PATH)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        if(builtUri!=null) {
            try {
                url = new URL(builtUri.toString());
                Log.v(TAG, "Built Movie Trailer URL: " + url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    static URL buildReviewUrl(Context context, String movieId) {

        URL url = null;
        String apiKey = context.getString(R.string.THE_MOVIE_DB_API_KEY);

        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(MOVIE_PATH)
                .appendPath(movieId)
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        if(builtUri!=null) {
            try {
                url = new URL(builtUri.toString());
                Log.v(TAG, "Built Movie Review URL: " + url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    static URL buildYoutubeTrailerUrl(String key) {

        URL url = null;

        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendPath(YOUTUBE_WATCH_PATH)
                .appendQueryParameter(YOUTUBE_VIDEO_KEY_PARAM, key)
                .build();
        if(builtUri!=null) {
            try {
                url = new URL(builtUri.toString());
                Log.v(TAG, "Built YouTube URL: " + url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return url;
    }

    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getPath(String selectedMovies) {
        switch(selectedMovies) {
            case POPULAR_MOVIES:
                return POPULAR_PATH;
            case TOP_RATED_MOVIES:
                return TOP_RATED_PATH;
            default:
                throw new UnsupportedOperationException("Unknow string: " + selectedMovies);
        }
    }
}
