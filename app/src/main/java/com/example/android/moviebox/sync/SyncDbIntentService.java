package com.example.android.moviebox.sync;

import android.app.IntentService;
import android.content.Intent;

public class SyncDbIntentService extends IntentService {

    public static final String ACTION_SYNC_MOVIE_DB = "synchronizing-movie-db";

    public SyncDbIntentService() {
        super("SyncDbIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (ACTION_SYNC_MOVIE_DB.equals(action)) {
            SyncDbTask.syncMovies(this);
        }
    }
}