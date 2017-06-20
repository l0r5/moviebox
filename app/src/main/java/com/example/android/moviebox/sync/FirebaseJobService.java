package com.example.android.moviebox.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class FirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchMoviesTask;


    @Override
    public boolean onStartJob(final JobParameters job) {

        mFetchMoviesTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Context context = getApplicationContext();
                SyncDbTask.syncMovies(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mFetchMoviesTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(mFetchMoviesTask != null) {
            mFetchMoviesTask.cancel(true);
        }
        return true;
    }
}
