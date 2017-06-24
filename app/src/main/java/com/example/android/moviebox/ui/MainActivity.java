package com.example.android.moviebox.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
    public static final String STATE_MOVIES_KEY = "moviesKey";
    public static final String STATE_CATEGORY_KEY = "categoryKeyState";
    public static final String INTENT_MOVIE_DETAIL_KEY = "movieDetailKey";
    public static final String INTENT_CATEGORY_KEY = "categoryKeyIntent";
    private static final boolean LOADING_INDICATOR_ON = true;
    private static final boolean LOADING_INDICATOR_OFF = false;
    private MovieListAdapter mMovieListAdapter;
    private ActivityMovieListBinding mBinding;
    private Movie[] mMovies;
    private String mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_list);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.recyclerViewMovieList.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mBinding.recyclerViewMovieList.setLayoutManager(new GridLayoutManager(this, 4));
        }

        if (savedInstanceState == null) {
            mCategory = POPULAR_MOVIES;
            setActivityTitle(mCategory);
        }

        mBinding.recyclerViewMovieList.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(this);
        mBinding.recyclerViewMovieList.setAdapter(mMovieListAdapter);

        SyncDbUtils.initialize(this);
        loadMovieData();
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(mMovies != null) {
            selectMoviesForCategory();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMovies != null) {
            outState.putParcelableArray(STATE_MOVIES_KEY, mMovies);
        }

        if (mCategory != null) {
            outState.putString(STATE_CATEGORY_KEY, mCategory);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_MOVIES_KEY)) {
            mMovies = (Movie[]) savedInstanceState.getParcelableArray(STATE_MOVIES_KEY);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_CATEGORY_KEY)) {
            mCategory = savedInstanceState.getString(STATE_CATEGORY_KEY);
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

    public void selectMoviesForCategory() {
        ArrayList<Movie> allMoviesList = new ArrayList<>(Arrays.asList(mMovies));
        ArrayList<Movie> categoryList = new ArrayList<>();
        Movie[] categoryMovies;

        switch (mCategory) {
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
                throw new UnsupportedOperationException("Unknow movies category: " + mCategory);
        }

        setActivityTitle(mCategory);

        // No favorites, yet
        if (categoryMovies.length == 0) {
            mBinding.textViewEmptyFavorites.setVisibility(View.VISIBLE);
        } else {
            mBinding.textViewEmptyFavorites.setVisibility(View.INVISIBLE);
        }

    }

    private void setActivityTitle(String category) {
        switch(category) {
            case POPULAR_MOVIES:
                setTitle("Popular Movies");
                break;
            case TOP_RATED_MOVIES:
                setTitle("Top Rated Movies");
                break;
            case FAVORITE_MOVIES:
                setTitle("Favorite Movies");
                break;
            default:
                throw new UnsupportedOperationException("Unknown movies category: " + mCategory);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_popular:
                mCategory = POPULAR_MOVIES;
                mMovieListAdapter.setMovieData(null);
                selectMoviesForCategory();
                return true;
            case R.id.action_top_rated:
                mCategory = TOP_RATED_MOVIES;
                mMovieListAdapter.setMovieData(null);
                selectMoviesForCategory();
                return true;
            case R.id.action_favorite:
                mCategory = FAVORITE_MOVIES;
                mMovieListAdapter.setMovieData(null);
                selectMoviesForCategory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movieDetails) {
        Intent intentToStartActivity = new Intent(this, DetailActivity.class);
        intentToStartActivity.putExtra(INTENT_MOVIE_DETAIL_KEY, movieDetails);
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
        } else {
            showErrorMessage();
        }
    }

    public void swapCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            Movie[] movieData = DataFormatUtils.getMoviesFromCursor(cursor);
            onTaskCompleted(movieData);
        }
    }
}
