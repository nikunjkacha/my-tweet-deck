package com.klavergne.mytweetdeck;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by e103705 on 11/3/2014.
 */
public class NewTweetFragment extends Fragment implements TextWatcher {

    public static final String FRAGMENT_TAG = "new_tweet_fragement";
    public static final String ARG_USER_NAME = "user_name";
    public static final String ARG_USER_FULL_NAME = "user_full_name";
    public static final String ARG_USER_ID = "user_id";
    public static final String ARG_ORIGINAL_TWEET_TEXT = "original_tweet_text";

    public static final int MAX_TWEET_LENGTH = 140;

    private String userName;
    private String userFullName;
    private String userId;
    private String originalTweetText;

    private View rootView = null;
    private EditText tweetText = null;
    private TextView remainingChars = null;
    private TextView textViewUserName = null;
    private TextView textViewUserFullName = null;

    private OnFragmentInteractionListener listener;

    public NewTweetFragment() {
        // do nothing; required no-parameter constructor
    }

    public static NewTweetFragment newInstance(String username, String fullName, String userId, String originalTweetText) {
        NewTweetFragment fragment = new NewTweetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, username);
        args.putString(ARG_USER_FULL_NAME, fullName);
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_ORIGINAL_TWEET_TEXT, originalTweetText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        userName = args.getString(ARG_USER_NAME);
        userFullName = args.getString(ARG_USER_FULL_NAME);
        userId = args.getString(ARG_USER_ID);
        originalTweetText = args.getString(ARG_ORIGINAL_TWEET_TEXT);

        if (rootView == null) {
            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_new_tweet, container, false);

            textViewUserName = (TextView) rootView.findViewById(R.id.newTweetUserName);
            textViewUserFullName = (TextView) rootView.findViewById(R.id.newTweetUserFullName);
            tweetText = (EditText) rootView.findViewById(R.id.newTweetText);
            remainingChars = (TextView) rootView.findViewById(R.id.charsLeft);

            // set this as a listener for text changed events on the EditText
            tweetText.addTextChangedListener(this);
        }

        if (originalTweetText != null) {
            tweetText.setText(getString(R.string.retweet_text) + originalTweetText);
        }
        textViewUserName.setText(userName);
        textViewUserFullName.setText(userFullName);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        int curLength = editable.length();
        int remaining = MAX_TWEET_LENGTH - curLength;

        remainingChars.setText(Integer.toString(remaining));
        if (remaining < 0) {
            remainingChars.setTextColor(getResources().getColor(R.color.holo_red_light));
        } else {
            remainingChars.setTextColor(getResources().getColor(R.color.secondary_text_dark));
        }
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
        public void sendTweet(String text);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.new_tweet, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.send_tweet:
                Log.d(this.getClass().getSimpleName(), "Send option selected");
                listener.sendTweet(tweetText.getText().toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // The following are not used.

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        // not used, but must implement
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        // not used, but must implement
    }
}
