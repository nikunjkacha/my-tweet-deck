package com.klavergne.mytweetdeck;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.klavergne.mytweetdeck.tasks.DownloadImageTask;
import com.klavergne.mytweetdeck.tasks.FavoriteTweetTask;
import com.klavergne.mytweetdeck.tasks.RetweetTask;
import com.klavergne.mytweetdeck.tasks.UnfavoriteTweetTask;

import java.util.Date;

import twitter4j.Status;
import twitter4j.Twitter;

public class TweetFragment extends Fragment implements RetweetTask.RetweetTaskCompleteListener, FavoriteTweetTask.FavoriteTweetTaskCompleteListener, UnfavoriteTweetTask.UnfavoriteTweetTaskCompleteListener {

    public static final String FRAGMENT_TAG = "tweet_fragement_detail";
    public static final String ARG_TWEET = "tweet";
    public static final String ARG_TWITTER = "twitter";

    java.text.DateFormat dateFormat = null;
    java.text.DateFormat timeFormat = null;

    private Twitter twitter = null;

    private Status status;

    private View rootView = null;

    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        status = (Status) getArguments().getSerializable(ARG_TWEET);
        twitter = (Twitter) getArguments().getSerializable(ARG_TWITTER);

        if (rootView == null) {
            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_tweet_detail, container, false);
        }

        updateTweetView(status);
        return rootView;
    }

    private class ViewHolder {
        TextView statusText;
        TextView userProfileName;
        TextView userProfileFullName;
        ImageView userProfilePic;
        TextView tweetDateAndTime;
        TextView tweetRetweetCount;
        TextView tweetFavoritesCount;
        Button rtButton;
        Button favButton;
        Switch favSwitch;
    }

    private ViewHolder holder;

    public void updateTweetView(Status status) {
        if (holder == null) {
            holder = new ViewHolder();
            holder.statusText = (TextView) rootView.findViewById(R.id.tweetText);
            holder.userProfileName = (TextView) rootView.findViewById(R.id.tweetUserName);
            holder.userProfileFullName = (TextView) rootView.findViewById(R.id.tweetUserFullName);
            holder.userProfilePic = (ImageView) rootView.findViewById(R.id.tweetProfilePic);
            holder.tweetDateAndTime = (TextView) rootView.findViewById(R.id.tweetDateAndTime);
            holder.tweetRetweetCount = (TextView) rootView.findViewById(R.id.tweetRetweetCount);
            holder.tweetFavoritesCount = (TextView) rootView.findViewById(R.id.tweetFavoritesCount);
            holder.rtButton = (Button) rootView.findViewById(R.id.retweetButton);
            holder.favButton = (Button) rootView.findViewById(R.id.favoriteButton);
            holder.rtButton.setOnClickListener(v -> retweet());
            holder.favButton.setOnClickListener(v -> favorite());
            holder.favSwitch = (Switch) rootView.findViewById(R.id.favSwitch);
            holder.favSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> changeFavoriteState(isChecked));
        }

        if (status != null) {
            holder.statusText.setText(status.getText());
            holder.userProfileFullName.setText(status.getUser().getName());
            holder.userProfileName.setText('@' + status.getUser().getScreenName());
            holder.tweetRetweetCount.setText(Integer.toString(status.getRetweetCount()));
            holder.tweetFavoritesCount.setText(Integer.toString(status.getFavoriteCount()));

            if (dateFormat == null || timeFormat == null) {
                dateFormat = DateFormat.getDateFormat(getActivity());
                timeFormat = DateFormat.getTimeFormat(getActivity());
            }
            Date createdAt = status.getCreatedAt();
            holder.tweetDateAndTime.setText(dateFormat.format(createdAt) + getString(R.string.time_date_separator) + timeFormat.format(createdAt));

            if (status.isFavorited()) {
                holder.favButton.setEnabled(false);
                holder.favSwitch.setChecked(true);
            } else {
                holder.favButton.setEnabled(true);
                holder.favSwitch.setChecked(false);
            }
            if (status.isRetweetedByMe()) {
                holder.rtButton.setEnabled(false);
            }

            new DownloadImageTask(holder.userProfilePic).execute(status.getUser().getProfileImageURLHttps());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putSerializable(ARG_TWEET, status);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void retweet() {
        Log.d(this.getClass().getSimpleName(), getString(R.string.log_retweeting, status.getId()));
        new RetweetTask(twitter, this).execute(status.getId());
    }

    public void favorite() {
        Log.d(this.getClass().getSimpleName(), getString(R.string.log_favoriting_tweet, status.getId()));
        new FavoriteTweetTask(twitter, this).execute(status.getId());
    }

    private void changeFavoriteState(boolean isChecked) {
        Log.d(this.getClass().getSimpleName(), getString(R.string.log_favorite_state_change, isChecked, status.getId()));
        //Log.d(this.getClass().getSimpleName(), "Changing favorite state to : " + isChecked + " for: " + status.getId());
        if (isChecked) {
            new FavoriteTweetTask(twitter, this).execute(status.getId());
        } else {
            new UnfavoriteTweetTask(twitter, this).execute(status.getId());
        }
    }

    @Override
    public void retweetComplete(Boolean success) {
        if (!success) {
            Toast.makeText(this.getActivity(), R.string.retweet_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void favoriteComplete(Boolean success) {
        if (!success) {
            Toast.makeText(this.getActivity(), R.string.favorite_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void unfavoriteComplete(Boolean success) {
        if (!success) {
            Toast.makeText(this.getActivity(), R.string.unfavorite_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
