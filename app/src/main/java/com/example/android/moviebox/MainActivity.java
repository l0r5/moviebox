package com.example.android.moviebox;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.utilities.FetchMoviesTask;
import com.example.android.moviebox.utilities.NetworkUtils;



public class MainActivity extends AppCompatActivity implements FetchMoviesTask.FetchMoviesCallback, MovieListAdapter.MovieListAdapterOnClickHandler {

    private static final int FETCH_MOVIES_LOADER_ID = 0;
    private static final String POPULAR_MOVIES = NetworkUtils.getPopularPath();
    private static final String TOP_RATED_MOVIES = NetworkUtils.getTopRatedPath();
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private MovieListAdapter mMovieListAdapter;
    private TextView mErrorMessageDisplay;
    private boolean mFirstMovieFetch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movielist);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mRecyclerView.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(this);
        mRecyclerView.setAdapter(mMovieListAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadMovieData(POPULAR_MOVIES);
    }

    /** Menu methods */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movielist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_popular) {
            mMovieListAdapter.setMovieData(null);
            loadMovieData(POPULAR_MOVIES);
            return true;
        } else if (id == R.id.action_top_rated) {
            mMovieListAdapter.setMovieData(null);
            loadMovieData(TOP_RATED_MOVIES);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Listeners & Implicit Intent*/
    @Override
    public void onClick(Movie movieDetails) {
        Intent intentToStartActivity = new Intent(this, DetailActivity.class);
        intentToStartActivity.putExtra("movieDetailData", movieDetails);
        startActivity(intentToStartActivity);
    }

    /** Loading & Persisting Movies*/
    private void loadMovieData(String moviesChoice) {
        showMovieDataView();
        LoaderManager.LoaderCallbacks<Movie[]> callback = new FetchMoviesTask(this, this, moviesChoice);
        if(mFirstMovieFetch) {
            getSupportLoaderManager().initLoader(FETCH_MOVIES_LOADER_ID, null, callback);
        } else {
            getSupportLoaderManager().restartLoader(FETCH_MOVIES_LOADER_ID, null, callback);
        }
    }


    /** Activity related Methods */
    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public void toggleLoadingIndicator(boolean onOffSwitch) {
        if(onOffSwitch) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }


    /** AsyncTaskLoader Callback */
    public void onTaskCompleted(Movie[] movieData) {
        toggleLoadingIndicator(false);
        if (movieData != null) {
            if(mFirstMovieFetch) {
                mFirstMovieFetch = false;
            }
            showMovieDataView();
            mMovieListAdapter.setMovieData(movieData);
        } else {
            showErrorMessage();
        }
    }

}
