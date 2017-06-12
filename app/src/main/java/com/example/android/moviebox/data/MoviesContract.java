package com.example.android.moviebox.data;

import android.provider.BaseColumns;

public class MoviesContract {

    public static final class MoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_TRAILER_URL = "trailer_url";

    }
}
