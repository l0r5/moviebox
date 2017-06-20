package com.example.android.moviebox.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.moviebox.R;
import com.example.android.moviebox.databinding.ActivityMovieListBinding;

import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.sync.SyncDbUtils;
import com.example.android.moviebox.utilities.DataFormatUtils;
import com.example.android.moviebox.utilities.DbTaskHandler;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements MovieListAdapter.MovieListAdapterOnClickHandler, DbTaskHandler.FetchMovieFromDbCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int GET_ALL_MOVIES_DB_LOADER_ID = 1;
    public static final String POPULAR_MOVIES = "popular";
    public static final String TOP_RATED_MOVIES = "top_rated";
    public static final String FAVORITE_MOVIES = "favorite";
    public static final String MOVIE_DETAIL_CALLBACK_KEY = "movie-detail-callback";
    private static final boolean LOADING_INDICATOR_ON = true;
    private static final boolean LOADING_INDICATOR_OFF = false;
    private MovieListAdapter mMovieListAdapter;
    ActivityMovieListBinding mBinding;
    Movie[] mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_list);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.recyclerViewMovieList.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mBinding.recyclerViewMovieList.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mBinding.recyclerViewMovieList.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(this);
        mBinding.recyclerViewMovieList.setAdapter(mMovieListAdapter);

        SyncDbUtils.initialize(this);
        loadMovieData();

        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_DETAIL_CALLBACK_KEY)) {
                String allPreviousLifecycleCallbacks = savedInstanceState
                        .getString(MOVIE_DETAIL_CALLBACK_KEY);
                Log.i(TAG, allPreviousLifecycleCallbacks);
            }
        }
    }


    private void loadMovieData() {
        toggleLoadingIndicator(LOADING_INDICATOR_ON);
        showMovieDataView();
        getMoviesFromDb();
    }


    private void getMoviesFromDb() {
        LoaderManager.LoaderCallbacks<Cursor> moviesCallback = new DbTaskHandler(this, this);
        getSupportLoaderManager().initLoader(GET_ALL_MOVIES_DB_LOADER_ID, null, moviesCallback);
    }

    public void selectMoviesForCategory(String category) {
        ArrayList<Movie> allMoviesList = new ArrayList<>(Arrays.asList(mMovies));
        ArrayList<Movie> categoryList = new ArrayList<>();
        Movie[] categoryMovies;

        switch (category) {
            case POPULAR_MOVIES:
                for (Movie movie : allMoviesList) {
                    if (movie.getPopular() == 1) {
                        categoryList.add(movie);
                    }
                }
                categoryMovies = categoryList.toArray(new Movie[categoryList.size()]);
                mMovieListAdapter.setMovieData(categoryMovies);
                break;

            case TOP_RATED_MOVIES:
                for (Movie movie : allMoviesList) {
                    if (movie.getTopRated() == 1) {
                        categoryList.add(movie);
                    }
                }

                categoryMovies = categoryList.toArray(new Movie[categoryList.size()]);
                mMovieListAdapter.setMovieData(categoryMovies);
                break;

            case FAVORITE_MOVIES:
                for (Movie movie : allMoviesList) {
                    if (movie.getFavorite() == 1) {
                        categoryList.add(movie);
                    }
                }
                categoryMovies = categoryList.toArray(new Movie[categoryList.size()]);
                mMovieListAdapter.setMovieData(categoryMovies);
                break;
            default:
                throw new UnsupportedOperationException("Unknow movies category: " + category);
        }

        // No favorites, yet
        if(categoryMovies.length == 0) {
            mBinding.textViewEmptyFavorites.setVisibility(View.VISIBLE);
        } else {
            mBinding.textViewEmptyFavorites.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movielist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_popular:
                mMovieListAdapter.setMovieData(null);
                selectMoviesForCategory(POPULAR_MOVIES);
                return true;
            case R.id.action_top_rated:
                mMovieListAdapter.setMovieData(null);
                selectMoviesForCategory(TOP_RATED_MOVIES);
                return true;
            case R.id.action_favorite:
                mMovieListAdapter.setMovieData(null);
                selectMoviesForCategory(FAVORITE_MOVIES);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movieDetails) {
        Intent intentToStartActivity = new Intent(this, DetailActivity.class);
        intentToStartActivity.putExtra("movieDetailData", movieDetails);
        startActivity(intentToStartActivity);
    }


    private void showMovieDataView() {
        mBinding.textViewErrorMessage.setVisibility(View.INVISIBLE);
        mBinding.recyclerViewMovieList.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mBinding.recyclerViewMovieList.setVisibility(View.INVISIBLE);
        mBinding.textViewErrorMessage.setVisibility(View.VISIBLE);
    }

    public void toggleLoadingIndicator(boolean onOffSwitch) {
        if (onOffSwitch) {
            mBinding.progressBarLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressBarLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }


    public void onTaskCompleted(Movie[] movieData) {
        toggleLoadingIndicator(LOADING_INDICATOR_OFF);
        if (movieData != null) {
            mMovies = movieData;
            mMovieListAdapter.setMovieData(movieData);
            selectMoviesForCategory(POPULAR_MOVIES);
        } else {
            showErrorMessage();
        }
    }

    public void swapCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            Movie[] movieData = DataFormatUtils.getMoviesFromCursor(cursor);
            cursor.close();
            onTaskCompleted(movieData);
        }
    }
}
