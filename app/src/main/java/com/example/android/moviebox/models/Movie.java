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
    private String mThumbnailUrlStr;


    public Movie(String id, String title, URL thumbnailUrl, String releaseDate, String rating, String description) {
        mId = id;
        mTitle = title;
        mThumbnailUrlStr = thumbnailUrl.toString();
        mReleaseDate = releaseDate;
        mRating = rating;
        mDescription = description;
        mFavorite = 0;
    }

    /** getter */
    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getThumbnailUrlStr() {
        return mThumbnailUrlStr;
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

    public String getDescription() {
        return mDescription;
    }


    /** Parcelable */
    private Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mThumbnailUrlStr = in.readString();
        mReleaseDate = in.readString();
        mRating = in.readString();
        mDescription = in.readString();
        mFavorite = in.readInt();
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
        out.writeString(mReleaseDate);
        out.writeString(mRating);
        out.writeString(mDescription);
        out.writeInt(mFavorite);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
