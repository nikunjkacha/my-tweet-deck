package com.klavergne.mytweetdeck;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.klavergne.mytweetdeck.tasks.DownloadImageTask;

import java.util.List;

import twitter4j.Status;

/**
 * Created by e103705 on 10/30/2014.
 */
public class TweetListAdapter extends ArrayAdapter<Status> {

    private Context context;
    private boolean useList = true;

    public TweetListAdapter(Context context, List<Status> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.context = context;
    }

    private class ViewHolder {
        TextView statusText;
        TextView userProfileName;
        TextView userProfileFullName;
        ImageView userProfilePic;
        TextView tweetHowLongAgo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Status item = getItem(position);
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            if (useList) {
                viewToUse = mInflater.inflate(R.layout.tweet_list_item, null);
            } else {
                viewToUse = mInflater.inflate(R.layout.tweet_grid_item, null);
            }
            holder = new ViewHolder();
            holder.statusText = (TextView) viewToUse.findViewById(R.id.statusText);
            holder.userProfileName = (TextView) viewToUse.findViewById(R.id.statusProfileUserName);
            holder.userProfileFullName = (TextView) viewToUse.findViewById(R.id.statusProfileUserFullName);
            holder.userProfilePic = (ImageView) viewToUse.findViewById(R.id.statusProfilePic);
            holder.tweetHowLongAgo = (TextView) viewToUse.findViewById(R.id.tweetHowLongAgo);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }
        holder.statusText.setText(item.getText());
        holder.userProfileName.setText('@' + item.getUser().getScreenName());
        holder.userProfileFullName.setText(item.getUser().getName());
        holder.tweetHowLongAgo.setText(formatRelativeTimeString(item.getCreatedAt().getTime()));

        new DownloadImageTask(holder.userProfilePic).execute(item.getUser().getProfileImageURLHttps());

        return viewToUse;
    }

    private String formatRelativeTimeString(long millis) {
        CharSequence relativeTimeSpanString = DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
        StringBuilder sb = new StringBuilder(relativeTimeSpanString);
        int pos = sb.indexOf(" min");
        if (pos > -1) {
            sb.delete(pos, sb.length());
            sb.append('m');
            return sb.toString();
        }
        pos = sb.indexOf(" hour");
        if (pos > -1) {
            sb.delete(pos, sb.length());
            sb.append('h');
            return sb.toString();
        }
        pos = sb.indexOf(" year");
        if (pos > -1) {
            sb.delete(pos, sb.length());
            sb.append('y');
            return sb.toString();
        }
        pos = sb.indexOf(" day");
        if (pos > -1) {
            sb.delete(pos, sb.length());
            sb.append('d');
            return sb.toString();
        }
        return sb.toString();
    }
}
