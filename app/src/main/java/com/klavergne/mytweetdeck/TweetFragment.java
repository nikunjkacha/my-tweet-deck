package com.klavergne.mytweetdeck;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

import twitter4j.Status;

public class TweetFragment extends Fragment {

    public static final String FRAGMENT_TAG = "tweet_fragement_detail";
    public static final String ARG_TWEET = "tweet";

    private static final String TIME_DATE_SEPARATOR = " - ";

    java.text.DateFormat dateFormat = null;
    java.text.DateFormat timeFormat = null;

    private Status status;

    private View rootView = null;

    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        status = (Status) getArguments().getSerializable(ARG_TWEET);

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
            holder.tweetDateAndTime.setText(timeFormat.format(createdAt) + TIME_DATE_SEPARATOR + dateFormat.format(createdAt));

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

}
