package com.example.android.moviebox.models;

import java.net.URL;


public class Review {

    private String mId;
    private String mAuthor;
    private String mContent;
    private URL mUrl;

    public Review(String id, String author, String content, URL url) {
        mId = id;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    public String getId() {
        return mId;
    }
}
