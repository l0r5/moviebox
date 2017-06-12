package com.example.android.moviebox.utilities;


import com.example.android.moviebox.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


class OpenMovieJsonUtils {

    public static Movie[] getMovieObjectsFromJson(String movieJsonString) throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_RATING = "vote_average";
        final String TMDB_DESCRIPTION = "overview";

        JSONObject jsonMovieObject = new JSONObject(movieJsonString);
        JSONArray jsonMovieArray = jsonMovieObject.getJSONArray(TMDB_RESULTS);
        Movie[] parsedMovieData = new Movie[jsonMovieArray.length()];

        // build movie objects array
        for(int i = 0; i < jsonMovieArray.length(); i++) {

            long id;
            String title;
            String posterPath;
            URL posterUrl;
            String releaseDate;
            String rating;
            String description;

            JSONObject results = jsonMovieArray.getJSONObject(i);

            // get movie attributes from json
            id = results.getLong(TMDB_ID);
            title = results.getString(TMDB_TITLE);
            posterPath = results.getString(TMDB_POSTER_PATH);
            posterPath = posterPath.substring(1);
            posterUrl = NetworkUtils.buildThumbnailUrl(posterPath);
            releaseDate = results.getString(TMDB_RELEASE_DATE);
            rating = results.getString(TMDB_RATING);
            description = results.getString(TMDB_DESCRIPTION);

            // build movie object
            Movie movie = new Movie(id, title, posterUrl, releaseDate, rating, description);

            parsedMovieData[i] = movie;
        }

        return parsedMovieData;
    }

}
