package com.example.android.moviebox.models;


import java.net.URL;

public class Trailer {


    private String mId;
    private String mIso6391;
    private String mIso31661;
    private String mKey;
    private String mName;
    private String mSite;
    private int mSize;
    private String mType;
    private URL mYoutubeUrl;

    public Trailer(String id, String iso6391, String iso31661, String key, String name, String site, int size, String type, URL youtubeUrl) {
        mId = id;
        mIso6391 = iso6391;
        mIso31661 = iso31661;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
        mType = type;
        mYoutubeUrl = youtubeUrl;
    }

    public String getId() {
        return mId;
    }

    public URL getYoutubeUrl() {
        return mYoutubeUrl;
    }

    public String getName() {
        return mName;
    }

}