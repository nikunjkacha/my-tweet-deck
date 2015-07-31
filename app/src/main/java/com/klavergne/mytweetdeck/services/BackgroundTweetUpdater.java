package com.klavergne.mytweetdeck.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.klavergne.mytweetdeck.tasks.GetTweetsTask;
import com.klavergne.mytweetdeck.R;
import com.klavergne.mytweetdeck.db.DbHelper;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by e103705 on 11/5/2014.
 */
public class BackgroundTweetUpdater extends Service {

    public static final String EXTRA_TWITTER = "twitter";
    private static final String TAG = BackgroundTweetUpdater.class.getSimpleName();
    private Twitter twitter;
    private Handler handler;
    private Updater updater;
    private SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        // setup handler
        handler = new Handler();

        // Initialize DB
        DbHelper dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updater);
        db.close();
        Log.d(TAG, "Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Started");
        super.onStartCommand(intent, flags, startId);

        twitter = (Twitter) intent.getSerializableExtra(EXTRA_TWITTER);

        updater = new Updater();
        handler.post(updater);
        return START_STICKY;
    }

    // Initializes twitter, if needed
    private Twitter getTwitter() {
        if (twitter == null) {
            twitter = TwitterFactory.getSingleton();
            twitter.setOAuthConsumer(getString(R.string.consumerKey), getString(R.string.consumerSecret));
            twitter.setOAuthAccessToken(new AccessToken(getString(R.string.accessToken), getString(R.string.accessTokenSecret)));
        }
        return twitter;
    }

    /**
     * Updates the database from twitter.com data
     */
    class Updater implements Runnable, GetTweetsTask.GetTweetsTaskCompleteListener {
        static final long DELAY = 100000L; // 100 seconds

        public void run() {
            Log.d(BackgroundTweetUpdater.TAG, getString(R.string.log_updating_tweets));

            // get timeline in separate thread
            new GetTweetsTask(this, getApplicationContext()).execute(getTwitter());

            // Set this to run again later
            handler.postDelayed(this, DELAY);
        }

        @Override
        public void onGetTweetsComplete(List<Status> timeline) {
            long result;
            long insertCount = 0;
            for (Status status : timeline) {
                ContentValues values = DbHelper.statusToContentValues(status);
                // Insert will throw exceptions for duplicate IDs
                try {
                    result = db.insertOrThrow(DbHelper.TABLE, null, values);
                    if (result < 0) {
                        Log.e(TAG, getString(R.string.log_db_insert_error));
                    }
                    insertCount++;
                } catch (SQLiteConstraintException e) {
                    // this is ok. we already have it in our db
                    //Log.d(TAG, "Primary key already exists: " + status.getId());
                } catch (SQLException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }
            }
            Log.d(TAG, getString(R.string.log_inserted_tweets_into_db, timeline.size(), insertCount));
        }
    }
}
