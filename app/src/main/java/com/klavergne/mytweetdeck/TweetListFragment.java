package com.klavergne.mytweetdeck;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;


import com.klavergne.mytweetdeck.db.DbHelper;
import com.klavergne.mytweetdeck.tasks.GetTweetsTask;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class TweetListFragment extends ListFragment implements GetTweetsTask.GetTweetsTaskCompleteListener, ListView.OnItemClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TWITTER_ARG = "twitter";

    private View rootView = null;

    private Twitter twitter;

    private OnTweetSelectedListener callback;

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private TimelineAdapter timelineAdapter;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView listView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private TweetListAdapter adapter;

    public static TweetListFragment newInstance(Twitter twitter) {
        TweetListFragment fragment = new TweetListFragment();
        Bundle args = new Bundle();
        args.putSerializable(TWITTER_ARG, twitter);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TweetListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(this.getClass().getSimpleName(), "Fragment Created");

        if (getArguments() != null) {
            twitter = (Twitter) getArguments().getSerializable(TWITTER_ARG);
        }

        if (savedInstanceState == null) {
            new GetTweetsTask(this, this.getActivity()).execute(twitter);
        }

        setHasOptionsMenu(true);

        dbHelper = new DbHelper(getActivity());
        db = dbHelper.getReadableDatabase();
        cursor = db.query(DbHelper.TABLE, null, null, null, null, null, DbHelper.C_CREATED_AT + " DESC");
        timelineAdapter = new TimelineAdapter(getActivity(), cursor);
        setListAdapter(timelineAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "Fragment onCreateView Called");

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_tweet_list, container, false);

            // Set the adapter
            listView = (AbsListView) rootView.findViewById(android.R.id.list);
            listView.setAdapter(adapter);

            // Set OnItemClickListener so we can be notified on item clicks
            listView.setOnItemClickListener(this);

            adapter = new TweetListAdapter(getActivity(), new ArrayList<Status>());
            setListAdapter(adapter);
        } else {
            //((ViewGroup)rootView.getParent()).removeView(rootView);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(this.getClass().getSimpleName(), "Fragment Activity Created");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(this.getClass().getSimpleName(), "Fragment Attached");
        try {
            callback = (OnTweetSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(this.getClass().getSimpleName(), "Fragment Detached");
        callback = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(this.getClass().getSimpleName(), "Selected item at position: " + position + "; id: " + id);
        Status status = (Status) getListAdapter().getItem(position);
        callback.onTweetSelected(status);
    }

    @Override
    public void onGetTweetsComplete(List<Status> statuses) {
        adapter.addAll(statuses);
        if (!statuses.isEmpty()) {
            adapter.notifyDataSetChanged();
            getListView().invalidateViews();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO implement this
    }

    public interface OnTweetSelectedListener {
        void onTweetSelected(Status status);

        void startNewTweetFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean result = false;
        if (id == R.id.action_new_tweet) {
            callback.startNewTweetFragment();
            result = true;
        } else if (id == R.id.action_settings) {
            result = true;
        }
        if (!result) {
            result = super.onOptionsItemSelected(item);
        }
        return result;
    }

}
