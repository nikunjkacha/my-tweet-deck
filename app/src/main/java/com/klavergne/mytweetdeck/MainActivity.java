package com.klavergne.mytweetdeck;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.klavergne.mytweetdeck.services.BackgroundTweetUpdater;
import com.klavergne.mytweetdeck.tasks.SendTweetTask;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class MainActivity extends Activity implements TweetListFragment.OnTweetSelectedListener, TweetFragment.OnFragmentInteractionListener, NewTweetFragment.OnFragmentInteractionListener, SendTweetTask.SendTweetsTaskCompleteListener {

    static final Twitter twitter = TwitterFactory.getSingleton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twitter.setOAuthConsumer(getString(R.string.consumerKey), getString(R.string.consumerSecret));
        twitter.setOAuthAccessToken(new AccessToken(getString(R.string.accessToken), getString(R.string.accessTokenSecret)));

        if (savedInstanceState == null) {
            TweetListFragment fragment = TweetListFragment.newInstance(twitter);
            getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }

        Intent intent = new Intent(this, BackgroundTweetUpdater.class);
        intent.putExtra(BackgroundTweetUpdater.EXTRA_TWITTER, twitter);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onTweetSelected(Status status) {

        TweetFragment tweetFragment = (TweetFragment) getFragmentManager().findFragmentByTag(TweetFragment.FRAGMENT_TAG);

        if (tweetFragment == null) {
            // 1-pane view
            tweetFragment = new TweetFragment();
            Bundle args = new Bundle();
            args.putSerializable(TweetFragment.ARG_TWEET, status);
            args.putSerializable(TweetFragment.ARG_TWITTER, twitter);
            tweetFragment.setArguments(args);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, tweetFragment, TweetFragment.FRAGMENT_TAG);
            transaction.addToBackStack(null);

            transaction.commit();
        } else {
            // 2-pane view
            tweetFragment.updateTweetView(status);
        }
    }

    @Override
    public void startNewTweetFragment() {
        NewTweetFragment fragment = (NewTweetFragment) getFragmentManager().findFragmentByTag(NewTweetFragment.FRAGMENT_TAG);

        if (fragment == null) {
            fragment = NewTweetFragment.newInstance(getString(R.string.owner), getString(R.string.sample_fullname), getString(R.string.ownerId), null);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, fragment, NewTweetFragment.FRAGMENT_TAG);
            transaction.addToBackStack(null);

            transaction.commit();
        } else {
            // TODO update the existing fragment
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO implement this
    }

    @Override
    public void sendTweet(String text) {
        // TODO implement this
        getFragmentManager().popBackStack();
        new SendTweetTask(twitter, this).execute(text);
    }

    @Override
    public void onSendTweetComplete(Boolean success) {
        Toast.makeText(this, (success ? R.string.send_tweet_successful : R.string.send_tweet_unsuccessful), Toast.LENGTH_LONG).show();
    }
}
