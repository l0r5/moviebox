package com.example.android.moviebox.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.example.android.moviebox.R;
import com.example.android.moviebox.data.MoviesContract;
import com.example.android.moviebox.databinding.ActivityMovieDetailBinding;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.models.Review;
import com.example.android.moviebox.models.Trailer;
import com.example.android.moviebox.utilities.DataFormatUtils;
import com.example.android.moviebox.utilities.FetchMovieTrailerTask;
import com.example.android.moviebox.utilities.DbTaskHandler;
import com.example.android.moviebox.utilities.FetchMovieReviewTask;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;

public class DetailActivity extends MainActivity implements TrailerListAdapter.TrailerListAdapterOnClickHandler, FetchMovieTrailerTask.FetchMovieTrailerCallback, FetchMovieReviewTask.FetchMovieReviewCallback, DbTaskHandler.FetchMovieFromDbCallback {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int FETCH_TRAILERS_LOADER_ID = 2;
    private static final int FETCH_REVIEWS_LOADER_ID = 3;
    private static final int BUTTON_NOT_FAVORITE = 0;
    private static final int BUTTON_FAVORITE = 1;
    private static final String STATE_MOVIE_DETAILS_KEY = "movie-details-key";
    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;


    ActivityMovieDetailBinding mBinding;
    private Movie mMovieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set Databinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getExtras() != null) {
            mMovieDetails = intentThatStartedThisActivity.getParcelableExtra(INTENT_MOVIE_DETAIL_KEY);
            loadDetailData();
        }

        // Switch toolbars
        setSupportActionBar(mBinding.toolbarMovieDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set Collapsing Toolbar
        Picasso.with(mBinding.imageViewDetailThumbnail.getContext()).load(mMovieDetails.getPosterUrlStr()).into(mBinding.backdropToolbarImage);
        mBinding.collapsingToolbarMovieDetail.setTitle(mMovieDetails.getTitle());
        mBinding.collapsingToolbarMovieDetail.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        // Set Content
        Picasso.with(mBinding.imageViewDetailThumbnail.getContext()).load(mMovieDetails.getThumbnailUrlStr()).into(mBinding.imageViewDetailThumbnail);
        mBinding.textViewMovieDetailTitle.setText(mMovieDetails.getTitle());
        mBinding.textViewMovieDetailReleaseDate.setText(mMovieDetails.getReleaseDate());
        mBinding.textViewMovieDetailRating.setText(mMovieDetails.getRating());
        mBinding.textViewMovieDetailDescription.setText(mMovieDetails.getDescription());
        setFavoriteButton();

        // Set Trailer List
        mBinding.recyclerViewTrailerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mTrailerListAdapter = new TrailerListAdapter(this);
        mBinding.recyclerViewTrailerList.setAdapter(mTrailerListAdapter);

        // Set Review List
        mBinding.recyclerViewReviewList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mReviewListAdapter = new ReviewListAdapter();
        mBinding.recyclerViewReviewList.setAdapter(mReviewListAdapter);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMovieDetails != null) {
            outState.putParcelable(STATE_MOVIE_DETAILS_KEY, mMovieDetails);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_MOVIE_DETAILS_KEY)) {
            mMovieDetails = savedInstanceState.getParcelable(STATE_MOVIE_DETAILS_KEY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_detail_menu, menu);
        return true;
    }


    private void loadDetailData() {
        //load trailers
        LoaderManager.LoaderCallbacks<Trailer[]> trailerCallback = new FetchMovieTrailerTask(this, this, mMovieDetails.getId());
        getSupportLoaderManager().initLoader(FETCH_TRAILERS_LOADER_ID, null, trailerCallback);

        //loader reviews
        LoaderManager.LoaderCallbacks<Review[]> reviewCallback = new FetchMovieReviewTask(this, this, mMovieDetails.getId());
        getSupportLoaderManager().initLoader(FETCH_REVIEWS_LOADER_ID, null, reviewCallback);
    }


    public void onReviewTaskCompleted(Review[] reviewData) {
        if (reviewData != null) {
            mReviewListAdapter.setReviewData(reviewData);
        } else {
            Log.e(TAG, "Review loading error");
        }
    }


    @Override
    public void onTrailerTaskCompleted(Trailer[] trailerData) {
        if (trailerData != null) {
            mTrailerListAdapter.setTrailerData(trailerData);
        } else {
            Log.e(TAG, "Trailer loading error");
        }
    }

    private void setFavoriteButton() {
        int favoriteValue = mMovieDetails.getFavorite();
        switch (favoriteValue) {
            case BUTTON_NOT_FAVORITE:
                mBinding.fabFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                mBinding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.heart_red));
                break;
            case BUTTON_FAVORITE:
                mBinding.fabFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                mBinding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.heart_white));
                break;
            default:
                throw new UnsupportedOperationException("Unknow integer: " + favoriteValue);
        }
    }

    public void onClickFavorite(View view) {
        int favoriteValue = mMovieDetails.getFavorite();
        ContentValues cv = new ContentValues();
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(mMovieDetails.getId()).build();

        switch (favoriteValue) {
            // Button will be set to Favorite
            case BUTTON_NOT_FAVORITE:
                Toast.makeText(this, R.string.favorite_toast_text, Toast.LENGTH_SHORT).show();
                mMovieDetails.setFavorite(BUTTON_FAVORITE);
                cv.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, BUTTON_FAVORITE);
                break;
            // Button will be set to non-Favorite
            case BUTTON_FAVORITE:
                Toast.makeText(this, R.string.no_favorite_toast_text, Toast.LENGTH_SHORT).show();
                mMovieDetails.setFavorite(BUTTON_NOT_FAVORITE);
                cv.put(MoviesContract.MoviesEntry.COLUMN_FAVORITE, BUTTON_NOT_FAVORITE);
                break;
            default:
                throw new UnsupportedOperationException("Unknow integer: " + favoriteValue);
        }
        setFavoriteButton();
        getContentResolver().update(uri, cv, MoviesContract.MoviesEntry.COLUMN_FAVORITE, null);
    }

    @Override
    public void onTrailerClick(Trailer trailerDetails) {
        Intent intentToStartYoutubeTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerDetails.getYoutubeUrl().toString()));
        if (intentToStartYoutubeTrailer.resolveActivity(getPackageManager()) != null) {
            startActivity(intentToStartYoutubeTrailer);
        }
    }

}

