package com.example.android.moviebox.utilities;


import android.content.ContentValues;

import com.example.android.moviebox.data.MoviesContract;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.models.Review;
import com.example.android.moviebox.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

final class DataFormatUtils {

    static Movie[] getMovieObjectsFromJson(String movieJsonString) throws JSONException {

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

            String id;
            String title;
            String posterPath;
            URL posterUrl;
            String releaseDate;
            String rating;
            String description;

            JSONObject results = jsonMovieArray.getJSONObject(i);

            // get movie attributes from json
            id = results.getString(TMDB_ID);
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


    static Trailer[] getTrailerObjectsFromJson(String trailerJsonString) throws JSONException {


        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_ISO_639_1 = "iso_639_1";
        final String TMDB_ISO_3166_1 = "iso_3166_1";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";
        final String TMDB_SITE = "site";
        final String TMDB_SIZE = "size";
        final String TMDB_TYPE = "type";


        JSONObject jsonTrailerObject = new JSONObject(trailerJsonString);
        JSONArray jsonTrailerArray = jsonTrailerObject.getJSONArray(TMDB_RESULTS);
        Trailer[] parsedTrailerData = new Trailer[jsonTrailerArray.length()];

        // build trailer objects array
        for(int i = 0; i < jsonTrailerArray.length(); i++) {

            String id;
            String iso6391;
            String iso31661;
            String key;
            String name;
            String site;
            int size;
            String type;
            URL youtubeUrl;

            JSONObject results = jsonTrailerArray.getJSONObject(i);

            // get trailer attributes from json
            id = results.getString(TMDB_ID);
            iso6391 = results.getString(TMDB_ISO_639_1);
            iso31661 = results.getString(TMDB_ISO_3166_1);
            key = results.getString(TMDB_KEY);
            name = results.getString(TMDB_NAME);
            site = results.getString(TMDB_SITE);
            size = results.getInt(TMDB_SIZE);
            type = results.getString(TMDB_TYPE);

            //build youtube url
            if(site.equals("YouTube")) {
                youtubeUrl = NetworkUtils.buildYoutubeTrailerUrl(key);
            } else {
                youtubeUrl = null;
            }

            // build trailer object
            Trailer trailer = new Trailer(id, iso6391, iso31661, key, name, site, size, type, youtubeUrl);

            parsedTrailerData[i] = trailer;
        }

        return parsedTrailerData;
    }

    static Review[] getReviewObjectsFromJson(String reviewJsonString) throws JSONException {


        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_URL = "url";


        JSONObject jsonReviewObject = new JSONObject(reviewJsonString);
        JSONArray jsonReviewArray = jsonReviewObject.getJSONArray(TMDB_RESULTS);
        Review[] parsedReviewData = new Review[jsonReviewArray.length()];

        // build review objects array
        for(int i = 0; i < jsonReviewArray.length(); i++) {

            String id;
            String author;
            String content;
            URL url = null;

            JSONObject results = jsonReviewArray.getJSONObject(i);

            // get trailer attributes from json
            id = results.getString(TMDB_ID);
            author = results.getString(TMDB_AUTHOR);
            content = results.getString(TMDB_CONTENT);
            try {
                url = new URL(results.getString(TMDB_URL));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // build review object
            Review review = new Review(id, author, content, url);


            parsedReviewData[i] = review;
        }

        return parsedReviewData;
    }

    static ContentValues createMovieContentValues(Movie movie) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getId());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_RATING, movie.getRating());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_DESCRIPTION, movie.getDescription());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, movie.getFavorite());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_THUMBNAIL_URL, movie.getThumbnailUrlStr());
        return movieValues;
    }


}
