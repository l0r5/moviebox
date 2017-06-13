package com.example.android.moviebox;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.moviebox.data.MoviesContract;
import com.example.android.moviebox.databinding.MovieDetailBinding;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.models.Review;
import com.example.android.moviebox.models.Trailer;
import com.example.android.moviebox.utilities.DataFormatUtils;
import com.example.android.moviebox.utilities.FetchFromDbTask;
import com.example.android.moviebox.utilities.FetchMovieReviewsTask;
import com.example.android.moviebox.utilities.FetchMovieTrailersTask;
import com.squareup.picasso.Picasso;

public class DetailActivity extends MainActivity implements FetchMovieTrailersTask.FetchMovieTrailersCallback, FetchMovieReviewsTask.FetchMovieReviewsCallback, FetchFromDbTask.FetchMovieFavoriteCallback {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int FETCH_TRAILERS_LOADER_ID = 1;
    private static final int FETCH_REVIEWS_LOADER_ID = 2;
    private static final int FETCH_MOVIE_DETAIL_LOADER_ID = 3;

    private static final int FAVORITE_BUTTON_NOT_FAVORITE = 0;
    private static final int FAVORITE_BUTTON_FAVORITE = 1;

    MovieDetailBinding mBinding;
    private Movie mMovieDetails;
    private Review mMovieReviews;
    private Trailer mMovieTrailers;
    Cursor mMovieDataCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.movie_detail);

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

    }

    /**
     * Restarts DB Fetch in Case the App will Pause
     */
    @Override
    protected void onResume() {
        super.onResume();
        LoaderManager.LoaderCallbacks<Cursor> favoriteCallback = new FetchFromDbTask(this, this, mMovieDetails.getId());
        getSupportLoaderManager().restartLoader(FETCH_MOVIE_DETAIL_LOADER_ID, null, favoriteCallback);
    }

    /**
     * Toggle Button Text
     * */
    private void setFavoriteButtonText() {
        int favoriteValue = mMovieDetails.getFavorite();
        switch (favoriteValue) {
            case FAVORITE_BUTTON_NOT_FAVORITE:
                mBinding.buttonMovieDetailFavorite.setText(R.string.favorite);
                break;
            case FAVORITE_BUTTON_FAVORITE:
                mBinding.buttonMovieDetailFavorite.setText(R.string.no_favorite);
        }
    }

    /**
     *  Favorite Button Click Listener
     */
    public void onFavorMovie(View view) {
        int favoriteValue = mMovieDetails.getFavorite();
        ContentValues cv = new ContentValues();
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(mMovieDetails.getId()).build();

        switch (favoriteValue) {
            // Button will be set to Favorite
            case FAVORITE_BUTTON_NOT_FAVORITE:
                Toast.makeText(this, "Marked as favorite", Toast.LENGTH_SHORT).show();
                mMovieDetails.setFavorite(FAVORITE_BUTTON_FAVORITE);
                cv.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, FAVORITE_BUTTON_FAVORITE);
                break;
            // Button will be set to non-Favorite
            case FAVORITE_BUTTON_FAVORITE:
                Toast.makeText(this, "Marked as non-favorite", Toast.LENGTH_SHORT).show();
                mMovieDetails.setFavorite(FAVORITE_BUTTON_NOT_FAVORITE);
                cv.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, FAVORITE_BUTTON_NOT_FAVORITE);
                break;
        }

        setFavoriteButtonText();
        getContentResolver().update(uri, cv, MoviesContract.MoviesEntry.COLUMN_FAVORITE, null);

    }


    /**
     * Load Trailers and Reviews
     */
    private void loadDetailData() {
        LoaderManager.LoaderCallbacks<Trailer[]> trailerCallback = new FetchMovieTrailersTask(this, this, mMovieDetails.getId());
        LoaderManager.LoaderCallbacks<Review[]> reviewCallback = new FetchMovieReviewsTask(this, this, mMovieDetails.getId());
        LoaderManager.LoaderCallbacks<Cursor> favoriteCallback = new FetchFromDbTask(this, this, mMovieDetails.getId());
        getSupportLoaderManager().initLoader(FETCH_TRAILERS_LOADER_ID, null, trailerCallback);
        getSupportLoaderManager().initLoader(FETCH_REVIEWS_LOADER_ID, null, reviewCallback);
        getSupportLoaderManager().initLoader(FETCH_MOVIE_DETAIL_LOADER_ID, null, favoriteCallback);

    }

    /**
     * AsyncTaskLoader Callback
     */
    public void onTrailerTaskCompleted(Trailer[] trailerData) {
        if (trailerData != null) {
            for (Trailer trailer : trailerData) {
                Log.i("Trailer id: ", trailer.getId());
            }
        } else {
            Log.d("DetailActivity", "Trailer loading error");
        }
    }

    public void onReviewTaskCompleted(Review[] reviewData) {
        if (reviewData != null) {
            for (Review review : reviewData) {
                Log.i("Review id: ", review.getId());
            }
        } else {
            Log.d("DetailActivity", "Review loading error");
        }
    }

    /**
     * Swaps Cursor and updates values from db
     */
    public void swapCursor(Cursor newCursor) {
        if (newCursor != null && newCursor.moveToFirst()) {
            mMovieDataCursor = newCursor;
            mMovieDetails = DataFormatUtils.getMovieFromCursor(mMovieDataCursor);
            setFavoriteButtonText();
        }
    }


}

