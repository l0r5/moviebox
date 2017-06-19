package com.example.android.moviebox.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.moviebox.R;
import com.example.android.moviebox.data.MoviesContract;
import com.example.android.moviebox.databinding.ActivityMovieDetailBinding;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.models.Review;
import com.example.android.moviebox.models.Trailer;
import com.example.android.moviebox.utilities.FetchMovieTrailerTask;
import com.example.android.moviebox.utilities.DbTaskHandler;
import com.example.android.moviebox.utilities.FetchMovieReviewTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;

public class DetailActivity extends MainActivity implements TrailerListAdapter.TrailerListAdapterOnClickHandler,FetchMovieTrailerTask.FetchMovieTrailerCallback, FetchMovieReviewTask.FetchMovieReviewCallback, DbTaskHandler.FetchMovieFromDbCallback {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int FETCH_TRAILERS_LOADER_ID = 2;
    private static final int FETCH_REVIEWS_LOADER_ID = 3;
    private static final int BUTTON_NOT_FAVORITE = 0;
    private static final int BUTTON_FAVORITE = 1;
    private TrailerListAdapter mTrailerListAdapter;

    ActivityMovieDetailBinding mBinding;
    private Movie mMovieDetails;
    private Review mMovieReviews;
    private Trailer mMovieTrailers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getExtras() != null) {
            mMovieDetails = intentThatStartedThisActivity.getParcelableExtra("movieDetailData");
            loadDetailData();
        }
        mBinding.collapsingToolbarMovieDetailTitle.setTitle(mMovieDetails.getTitle());
        mBinding.collapsingToolbarMovieDetailTitle.setExpandedTitleTextAppearance(R.style.ExpandedToolbar);
        mBinding.collapsingToolbarMovieDetailTitle.setCollapsedTitleTextAppearance(R.style.CollapsedToolbar);
        Picasso.with(mBinding.imageViewDetailThumbnail.getContext()).load(mMovieDetails.getThumbnailUrlStr()).into(mBinding.imageViewDetailThumbnail);
        mBinding.textViewMovieDetailReleaseDate.setText(mMovieDetails.getReleaseDate());
        mBinding.textViewMovieDetailRating.setText(mMovieDetails.getRating());
        mBinding.textViewMovieDetailDescription.setText(mMovieDetails.getDescription());
        setFavoriteButtonText();

        mBinding.recyclerViewTrailerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerViewTrailerList.setHasFixedSize(true);
        mTrailerListAdapter = new TrailerListAdapter(this);
        mBinding.recyclerViewTrailerList.setAdapter(mTrailerListAdapter);

    }


    /**
     * Load Trailers and Reviews
     */
    private void loadDetailData() {

        //load trailers
        LoaderManager.LoaderCallbacks<Trailer[]> trailerCallback = new FetchMovieTrailerTask(this, this, mMovieDetails.getId());
        getSupportLoaderManager().initLoader(FETCH_TRAILERS_LOADER_ID, null, trailerCallback);

        //loader reviews
        LoaderManager.LoaderCallbacks<Review[]> reviewCallback = new FetchMovieReviewTask(this, this, mMovieDetails.getId());
        getSupportLoaderManager().initLoader(FETCH_REVIEWS_LOADER_ID, null, reviewCallback);

    }

    /**
     * AsyncTaskLoader Callback
     */
    public void onReviewTaskCompleted(Review[] reviewData) {
        if (reviewData != null) {
            for (Review review : reviewData) {
                Log.i("Review id: ", review.getId());
            }
        } else {
            Log.d("DetailActivity", "Review loading error");
        }
    }

    @Override
    public void onTrailerTaskCompleted(Trailer[] trailerData) {
        if (trailerData != null) {
            mTrailerListAdapter.setTrailerData(trailerData);
            for (Trailer trailer : trailerData) {
                Log.i("Trailer id: ", trailer.getId());
            }
        } else {
            Log.d("DetailActivity", "Trailer loading error");
        }
    }

    /**
     * Toggle Button Text
     */
    private void setFavoriteButtonText() {
        int favoriteValue = mMovieDetails.getFavorite();
        switch (favoriteValue) {
            case BUTTON_NOT_FAVORITE:
                mBinding.buttonMovieDetailFavorite.setText(R.string.favorite);
                break;
            case BUTTON_FAVORITE:
                mBinding.buttonMovieDetailFavorite.setText(R.string.no_favorite);
                break;
            default:
                throw new UnsupportedOperationException("Unknow integer: " + favoriteValue);
        }
    }

    /**
     *  Favorite Button Click Listener
     */
    public void onClickFavorite(View view) {
        int favoriteValue = mMovieDetails.getFavorite();
        ContentValues cv = new ContentValues();
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(mMovieDetails.getId()).build();

        switch (favoriteValue) {
            // Button will be set to Favorite
            case BUTTON_NOT_FAVORITE:
                Toast.makeText(this, "Marked as favorite", Toast.LENGTH_SHORT).show();
                mMovieDetails.setFavorite(BUTTON_FAVORITE);
                cv.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, BUTTON_FAVORITE);
                break;
            // Button will be set to non-Favorite
            case BUTTON_FAVORITE:
                Toast.makeText(this, "Marked as non-favorite", Toast.LENGTH_SHORT).show();
                mMovieDetails.setFavorite(BUTTON_NOT_FAVORITE);
                cv.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, BUTTON_NOT_FAVORITE);
                break;
            default:
                throw new UnsupportedOperationException("Unknow integer: " + favoriteValue);
        }
        setFavoriteButtonText();
        getContentResolver().update(uri, cv, MoviesContract.MoviesEntry.COLUMN_FAVORITE, null);
    }

    @Override
    public void onTrailerClick(Trailer trailerDetails) {
        Intent intentToStartYoutubeTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerDetails.getYoutubeUrl().toString()));
        startActivity(intentToStartYoutubeTrailer);
    }
}

