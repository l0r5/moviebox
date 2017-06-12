package com.example.android.moviebox;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviebox.databinding.MovieDetailBinding;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.models.Review;
import com.example.android.moviebox.models.Trailer;
import com.example.android.moviebox.utilities.FetchMovieReviewsTask;
import com.example.android.moviebox.utilities.FetchMovieTrailersTask;
import com.squareup.picasso.Picasso;

public class DetailActivity extends MainActivity implements FetchMovieTrailersTask.FetchMovieTrailersCallback, FetchMovieReviewsTask.FetchMovieReviewsCallback {

    private static final int FETCH_TRAILERS_LOADER_ID = 1;
    private static final int FETCH_REVIEWS_LOADER_ID = 2;
    MovieDetailBinding mBinding;
    private Movie mMovieDetails;
    private Review mMovieReviews;
    private Trailer mMovieTrailers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.movie_detail);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getExtras() != null) {
            mMovieDetails = intentThatStartedThisActivity.getParcelableExtra("movieDetailData");
            mBinding.collapsingToolbarMovieDetailTitle.setTitle(mMovieDetails.getTitle());
            mBinding.collapsingToolbarMovieDetailTitle.setExpandedTitleTextAppearance(R.style.ExpandedToolbar);
            mBinding.collapsingToolbarMovieDetailTitle.setCollapsedTitleTextAppearance(R.style.CollapsedToolbar);
            Picasso.with(mBinding.imageViewDetailThumbnail.getContext()).load(mMovieDetails.getThumbnailUrlStr()).into(mBinding.imageViewDetailThumbnail);
            mBinding.textViewMovieDetailReleaseDate.setText(mMovieDetails.getReleaseDate());
            mBinding.textViewMovieDetailRating.setText(mMovieDetails.getRating());
            mBinding.textViewMovieDetailDescription.setText(mMovieDetails.getDescription());
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

