package com.example.android.moviebox;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
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

import com.example.android.moviebox.data.MoviesContract;
import com.example.android.moviebox.databinding.ActivityMovielistBinding;
import com.example.android.moviebox.models.Movie;
import com.example.android.moviebox.sync.SyncDbIntentService;
import com.example.android.moviebox.utilities.DataFormatUtils;
import com.example.android.moviebox.utilities.FetchFromDbTask;
import com.example.android.moviebox.utilities.FetchMoviesTask;
import com.example.android.moviebox.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FetchMoviesTask.FetchMoviesCallback, MovieListAdapter.MovieListAdapterOnClickHandler, FetchFromDbTask.FetchMovieFromDbCallback {

    private static final int FETCH_MOVIES_LOADER_ID = 0;
    public static final int FETCH_FAVORITE_MOVIE_LOADER_ID = 10;
    public static final String POPULAR_MOVIES = "popular";
    public static final String TOP_RATED_MOVIES = "top_rated";
    private static final String FAVORITE_MOVIES = "favorite";
    private MovieListAdapter mMovieListAdapter;
    private boolean mFirstMovieFetch = true; // TODO Raus damit!
    ActivityMovielistBinding mBinding;

    // TODO write service to keep db consistent and update ONLY missing movies
    // TODO shared preference variable for like/unlike


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
        syncDb();
    }


    /**
     * Loading & Persisting Movies
     */
    private void loadMovieData(String moviesChoice) {
        showMovieDataView();

        // Fetch data from the Internet
//        if (moviesChoice.equals(POPULAR_MOVIES) || (moviesChoice.equals(TOP_RATED_MOVIES))) {
//            LoaderManager.LoaderCallbacks<Movie[]> callback = new FetchMoviesTask(this, this, NetworkUtils.getPath(moviesChoice));
//            if(mFirstMovieFetch) {
//                getSupportLoaderManager().initLoader(FETCH_MOVIES_LOADER_ID, null, callback);
//            } else {
//                getSupportLoaderManager().restartLoader(FETCH_MOVIES_LOADER_ID, null, callback);
//            }
//        } // Fetch data from the db
//        else {
//            LoaderManager.LoaderCallbacks<Cursor> moviesCallback = new FetchFromDbTask(this, this);
//            getSupportLoaderManager().initLoader(FETCH_FAVORITE_MOVIE_LOADER_ID, null, moviesCallback);
//        }
    }

    private void syncDb() {
        Intent startSyncDbIntent = new Intent(this, SyncDbIntentService.class);
        startSyncDbIntent.setAction(SyncDbIntentService.ACTION_SYNC_MOVIE_DB);
        startService(startSyncDbIntent);
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

        switch(id) {
            case R.id.action_popular:
                mMovieListAdapter.setMovieData(null);
                loadMovieData(POPULAR_MOVIES);
                return true;
            case R.id.action_top_rated:
                mMovieListAdapter.setMovieData(null);
                loadMovieData(TOP_RATED_MOVIES);
                return true;
            case R.id.action_favorite:
                mMovieListAdapter.setMovieData(null);
                loadMovieData(FAVORITE_MOVIES);
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
        if(onOffSwitch) {
            mBinding.progressBarLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressBarLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * AsyncTaskLoader Callback
     */
    public void onTaskCompleted(Movie[] movieData) {
        toggleLoadingIndicator(false);
        if (movieData != null) {

            // TODO Change this block
            // Hier soll der DB Sync aufgerufen werden

            String[] fetchedMovieIds = new String[movieData.length];


            for(int i = 0; i < movieData.length; i++) {
                fetchedMovieIds[i] = movieData[i].getId();
            }


            if(mFirstMovieFetch) {
                mFirstMovieFetch = false;
            }



            showMovieDataView();
            mMovieListAdapter.setMovieData(movieData);


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
        }
    }

}
