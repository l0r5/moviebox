package com.example.android.moviebox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.moviebox.databinding.ActivityMovielistBinding;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.sync.SyncDbIntentService;
import com.example.android.moviebox.sync.SyncDbUtils;
import com.example.android.moviebox.utilities.DataFormatUtils;
import com.example.android.moviebox.utilities.FetchFromDbTask;
import com.example.android.moviebox.utilities.FetchMoviesTask;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements FetchMoviesTask.FetchMoviesCallback, MovieListAdapter.MovieListAdapterOnClickHandler, FetchFromDbTask.FetchMovieFromDbCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int FETCH_ALL_MOVIES_DB_LOADER_ID = 1;
    public static final String POPULAR_MOVIES = "popular";
    public static final String TOP_RATED_MOVIES = "top_rated";
    private static final String FAVORITE_MOVIES = "favorite";
    private MovieListAdapter mMovieListAdapter;
    ActivityMovielistBinding mBinding;
    Movie[] mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movielist);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.recyclerViewMovielist.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mBinding.recyclerViewMovielist.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mBinding.recyclerViewMovielist.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(this);
        mBinding.recyclerViewMovielist.setAdapter(mMovieListAdapter);

        SyncDbUtils.initialize(this);
        loadMovieData();

    }

    /**
     * Loading & Persisting Movies
     */
    private void loadMovieData() {
        showMovieDataView();
        getMoviesFromDb();
    }


    private void getMoviesFromDb() {
        LoaderManager.LoaderCallbacks<Cursor> moviesCallback = new FetchFromDbTask(this, this);
        getSupportLoaderManager().initLoader(FETCH_ALL_MOVIES_DB_LOADER_ID, null, moviesCallback);
    }

    private void selectMoviesForCategory(String category) {

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

    }



    /**
     * Menu
     */
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

    /**
     * Listeners & Implicit Intent
     */
    @Override
    public void onClick(Movie movieDetails) {
        Intent intentToStartActivity = new Intent(this, DetailActivity.class);
        intentToStartActivity.putExtra("movieDetailData", movieDetails);
        startActivity(intentToStartActivity);
    }


    /**
     * Activity related Methods
     */
    private void showMovieDataView() {
        mBinding.textViewErrorMessage.setVisibility(View.INVISIBLE);
        mBinding.recyclerViewMovielist.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mBinding.recyclerViewMovielist.setVisibility(View.INVISIBLE);
        mBinding.textViewErrorMessage.setVisibility(View.VISIBLE);
    }

    public void toggleLoadingIndicator(boolean onOffSwitch) {
        if (onOffSwitch) {
            mBinding.progressBarLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressBarLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * AsyncTaskLoader Callback
     */
    public void onTaskCompleted(Movie[] movieData) {
        toggleLoadingIndicator(false); // TODO muss woanders hin

        if (movieData != null) {
            mMovies = movieData;
            mMovieListAdapter.setMovieData(movieData);
            selectMoviesForCategory(POPULAR_MOVIES);
        } else {
            showErrorMessage();
        }
    }

    /**
     * Swaps Cursor and updates values from db
     */
    public void swapCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            Movie[] movieData = DataFormatUtils.getMoviesFromCursor(cursor);
            onTaskCompleted(movieData);
            cursor.close();
        } else {
            Log.d(TAG, "Error while loading");
        }
    }
}
