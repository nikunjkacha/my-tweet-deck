package com.klavergne.mytweetdeck.tasks;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by Kevin LaVergne on 11/5/2014.
 */
public class SendTweetTask extends AsyncTask<String, Void, Boolean> {

    private Twitter twitter;
    private SendTweetsTaskCompleteListener listener;

    public SendTweetTask(Twitter twitter, SendTweetsTaskCompleteListener listener) {
        this.twitter = twitter;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            twitter.tweets().updateStatus(strings[0]);
            return true;
        } catch (TwitterException e) {
            Log.e(this.getClass().getSimpleName(), e.getErrorMessage(), e);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        listener.onSendTweetComplete(success);
    }

    public static interface SendTweetsTaskCompleteListener {
        public void onSendTweetComplete(Boolean success);
    }
}
