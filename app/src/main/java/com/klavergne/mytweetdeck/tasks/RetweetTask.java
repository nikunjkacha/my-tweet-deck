package com.klavergne.mytweetdeck.tasks;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Kevin LaVergne on 11/5/2014.
 */
public class RetweetTask extends AsyncTask<Long, Void, Boolean> {

    private Twitter twitter;
    private RetweetTaskCompleteListener listener;

    public RetweetTask(Twitter twitter, RetweetTaskCompleteListener listener) {
        this.twitter = twitter;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Long... ids) {
        try {
            twitter.retweetStatus(ids[0]);
            return true;
        } catch (TwitterException e) {
            Log.e(this.getClass().getSimpleName(), e.getErrorMessage(), e);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        listener.retweetComplete(success);
    }

    public interface RetweetTaskCompleteListener {
        void retweetComplete(Boolean success);
    }
}
