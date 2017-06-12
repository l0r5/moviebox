package com.example.android.moviebox;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.models.Review;
import com.example.android.moviebox.models.Trailer;
import com.example.android.moviebox.utilities.FetchMovieReviewsTask;
import com.example.android.moviebox.utilities.FetchMovieTrailersTask;
import com.squareup.picasso.Picasso;

public class DetailActivity extends MainActivity implements FetchMovieTrailersTask.FetchMovieTrailersCallback, FetchMovieReviewsTask.FetchMovieReviewsCallback {

    private static final int FETCH_TRAILERS_LOADER_ID = 1;
    private static final int FETCH_REVIEWS_LOADER_ID = 2;
    private Movie mMovieDetails;
    private Review mMovieReviews;
    private Trailer mMovieTrailers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        ImageView thumbnail = (ImageView) findViewById(R.id.iv_detail_thumbnail);
        TextView movieReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_detail_release_date);
        TextView ratingTextView = (TextView) findViewById(R.id.tv_movie_detail_rating);
        TextView descriptionTextView = (TextView) findViewById(R.id.tv_movie_detail_description);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_movie_detail_title);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getExtras() != null) {
            mMovieDetails = intentThatStartedThisActivity.getParcelableExtra("movieDetailData");
            collapsingToolbar.setTitle(mMovieDetails.getTitle());
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedToolbar);
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedToolbar);
            Picasso.with(thumbnail.getContext()).load(mMovieDetails.getThumbnailUrlStr()).into(thumbnail);
            movieReleaseDateTextView.setText(mMovieDetails.getReleaseDate());
            ratingTextView.setText(mMovieDetails.getRating());
            descriptionTextView.setText(mMovieDetails.getDescription());
            loadDetailData(mMovieDetails.getId());
        }
    }

    /**
     * Load Trailers and Reviews
     */
    private void loadDetailData(String moviesChoice) {
        LoaderManager.LoaderCallbacks<Trailer[]> trailerCallback = new FetchMovieTrailersTask(this, this, mMovieDetails.getId());
        LoaderManager.LoaderCallbacks<Review[]> reviewCallback = new FetchMovieReviewsTask(this, this, mMovieDetails.getId());
        getSupportLoaderManager().initLoader(FETCH_TRAILERS_LOADER_ID, null, trailerCallback);
        getSupportLoaderManager().initLoader(FETCH_REVIEWS_LOADER_ID, null, reviewCallback);


    }

    /** AsyncTaskLoader Callback */
    public void onTrailerTaskCompleted(Trailer[] trailerData) {
        if (trailerData != null) {
            for(Trailer trailer:trailerData) {
                Log.i("Trailer id: ", trailer.getId());
            }
        } else {
            Log.d("DetailActivity", "Trailer loading error");
        }
    }

    public void onReviewTaskCompleted(Review[] reviewData) {
        if (reviewData != null) {
            for(Review review:reviewData) {
                Log.i("Review id: ", review.getId());
            }
        } else {
            Log.d("DetailActivity", "Review loading error");
        }
    }

}

