package com.klavergne.mytweetdeck.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.klavergne.mytweetdeck.R;

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
    private Context context;

    public GetTweetsTask(GetTweetsTaskCompleteListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected List<twitter4j.Status> doInBackground(Twitter... twitters) {
        if (twitters[0] != null) {
            try {
                final List<twitter4j.Status> timeline = twitters[0].getHomeTimeline();
                Log.d(this.getClass().getSimpleName(), context.getString(R.string.log_got_tweets, timeline.size()));
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
