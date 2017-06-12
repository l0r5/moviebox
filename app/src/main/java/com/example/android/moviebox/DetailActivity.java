package com.example.android.moviebox;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviebox.models.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        Movie mMovieDetails;
        ImageView thumbnail = (ImageView) findViewById(R.id.iv_detail_thumbnail);
        TextView movieReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_detail_release_date);
        TextView ratingTextView = (TextView) findViewById(R.id.tv_movie_detail_rating);
        TextView descriptionTextView = (TextView) findViewById(R.id.tv_movie_detail_description);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_movie_detail_title);

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.getExtras() != null) {
            mMovieDetails = intentThatStartedThisActivity.getParcelableExtra("movieDetailData");
            collapsingToolbar.setTitle(mMovieDetails.getTitle());
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedToolbar);
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedToolbar);
            Picasso.with(thumbnail.getContext()).load(mMovieDetails.getThumbnailUrlStr()).into(thumbnail);
            movieReleaseDateTextView.setText(mMovieDetails.getReleaseDate());
            ratingTextView.setText(mMovieDetails.getRating());
            descriptionTextView.setText(mMovieDetails.getDescription());
        }
    }
}
