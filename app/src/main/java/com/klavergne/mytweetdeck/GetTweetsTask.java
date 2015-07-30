package com.klavergne.mytweetdeck;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by e103705 on 11/1/2014.
 */
public class GetTweetsTask extends AsyncTask<Twitter, Void, List<Status>> {

    private GetTweetsTaskCompleteListener listener;

    public GetTweetsTask(GetTweetsTaskCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<twitter4j.Status> doInBackground(Twitter... twitters) {
        if (twitters[0] != null) {
            try {
                final List<twitter4j.Status> timeline = twitters[0].getHomeTimeline();
                Log.d(this.getClass().getSimpleName(), "Got " + timeline.size() + " tweets");
                return timeline;
            } catch (TwitterException e) {
                Log.e(this.getClass().getSimpleName(), "Error getting timeline: " + e.getLocalizedMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    @Override
    protected void onPostExecute(List<twitter4j.Status> statuses) {
        listener.onGetTweetsComplete(statuses);
    }

    public static interface GetTweetsTaskCompleteListener {
        public void onGetTweetsComplete(List<twitter4j.Status> statuses);
    }
}
