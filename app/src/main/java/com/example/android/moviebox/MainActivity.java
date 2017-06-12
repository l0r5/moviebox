package com.example.android.moviebox;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
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

import com.example.android.moviebox.databinding.ActivityMovielistBinding;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.utilities.FetchMoviesTask;
import com.example.android.moviebox.utilities.NetworkUtils;



public class MainActivity extends AppCompatActivity implements FetchMoviesTask.FetchMoviesCallback, MovieListAdapter.MovieListAdapterOnClickHandler {

    private static final int FETCH_MOVIES_LOADER_ID = 0;
    private static final String POPULAR_MOVIES = NetworkUtils.getPopularPath();
    private static final String TOP_RATED_MOVIES = NetworkUtils.getTopRatedPath();
    private MovieListAdapter mMovieListAdapter;
    private boolean mFirstMovieFetch = true;
    ActivityMovielistBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movielist);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mBinding.recyclerViewMovielist.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else{
            mBinding.recyclerViewMovielist.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mBinding.recyclerViewMovielist.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(this);
        mBinding.recyclerViewMovielist.setAdapter(mMovieListAdapter);

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
        mBinding.textViewErrorMessage.setVisibility(View.INVISIBLE);
        mBinding.recyclerViewMovielist.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mBinding.recyclerViewMovielist.setVisibility(View.INVISIBLE);
        mBinding.textViewErrorMessage.setVisibility(View.VISIBLE);
    }

    public void toggleLoadingIndicator(boolean onOffSwitch) {
        if(onOffSwitch) {
            mBinding.progressBarLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressBarLoadingIndicator.setVisibility(View.INVISIBLE);
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
