package com.example.android.moviebox.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;


public class Movie implements Parcelable {

    private long mId;
    private String mTitle;
    private String mReleaseDate;
    private String mRating;
    private String mDescription;
    private String mReview;
    private Boolean mFavorite;
    private String mThumbnailUrlStr;
    private String mTrailerUrlStr;


    public Movie(long id, String title, URL thumbnailUrl, String releaseDate, String rating, String description) {
        mId = id;
        mTitle = title;
        mThumbnailUrlStr = thumbnailUrl.toString();
        mReleaseDate = releaseDate;
        mRating = rating;
        mDescription = description;
    }

    /** getter */
    public long getId() {
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

    public String getDescription() {
        return mDescription;
    }


    /** Parcelable */
    private Movie(Parcel in) {
        mId = in.readLong();
        mTitle = in.readString();
        mThumbnailUrlStr = in.readString();
        mReleaseDate = in.readString();
        mRating = in.readString();
        mDescription = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mId);
        out.writeString(mTitle);
        out.writeString(mThumbnailUrlStr);
        out.writeString(mReleaseDate);
        out.writeString(mRating);
        out.writeString(mDescription);
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
