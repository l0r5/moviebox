package com.example.android.moviebox.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;


public class Movie implements Parcelable {

    private String mId;
    private String mTitle;
    private String mReleaseDate;
    private String mRating;
    private String mDescription;
    private int mFavorite; //values can be 0 (false) and 1 (true)
    private int mTopRated;
    private int mPopular;
    private String mThumbnailUrlStr;
    private String mPosterUrlStr;


    public Movie(String id, String title, URL thumbnailUrl, URL posterUrl, String releaseDate, String rating, String description) {
        mId = id;
        mTitle = title;
        mThumbnailUrlStr = thumbnailUrl.toString();
        mPosterUrlStr = posterUrl.toString();
        mReleaseDate = releaseDate;
        mRating = rating;
        mDescription = description;
        mFavorite = 0;
        mTopRated = 0;
        mPopular = 0;
    }

    public Movie(String id, String title, String thumbnailUrl, String posterUrl, String releaseDate, String rating, String description, int favorite, int topRated, int popular) {
        mId = id;
        mTitle = title;
        mThumbnailUrlStr = thumbnailUrl;
        mPosterUrlStr = posterUrl;
        mReleaseDate = releaseDate;
        mRating = rating;
        mDescription = description;
        mFavorite = favorite;
        mTopRated = topRated;
        mPopular = popular;

    }

    /**
     * getter
     */
    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getThumbnailUrlStr() {
        return mThumbnailUrlStr;
    }

    public String getPosterUrlStr() {
        return mPosterUrlStr;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getRating() {
        return mRating;
    }

    public int getFavorite() {
        return mFavorite;
    }

    public int getTopRated() {
        return mTopRated;
    }

    public int getPopular() {
        return mPopular;
    }

    public String getDescription() {
        return mDescription;
    }

    /**
     * setter
     */
    public void setFavorite(int newFavorite) {
        mFavorite = newFavorite;
    }

    public void setTopRated(int newTopRated) {
        mTopRated = newTopRated;
    }

    public void setPopular(int newPopular) {
        mPopular = newPopular;
    }

    public static Movie[] concatMovies(Movie[] moviesFirst, Movie[] moviesSecond) {
        Movie[] combinedMovies = new Movie[moviesFirst.length + moviesSecond.length];
        System.arraycopy(moviesFirst, 0, combinedMovies, 0, moviesFirst.length);
        System.arraycopy(moviesSecond, 0, combinedMovies, moviesFirst.length, moviesSecond.length);
        return combinedMovies;
    }


    /**
     * Parcelable
     */
    private Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mThumbnailUrlStr = in.readString();
        mPosterUrlStr = in.readString();
        mReleaseDate = in.readString();
        mRating = in.readString();
        mDescription = in.readString();
        mFavorite = in.readInt();
        mPopular = in.readInt();
        mTopRated = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mId);
        out.writeString(mTitle);
        out.writeString(mThumbnailUrlStr);
        out.writeString(mPosterUrlStr);
        out.writeString(mReleaseDate);
        out.writeString(mRating);
        out.writeString(mDescription);
        out.writeInt(mFavorite);
        out.writeInt(mTopRated);
        out.writeInt(mPopular);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {


        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
