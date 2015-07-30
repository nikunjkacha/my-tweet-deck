package com.klavergne.mytweetdeck;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.klavergne.mytweetdeck.db.DbHelper;

/**
 * Created by Kevin LaVergne on 11/9/2014.
 */
public class TimelineAdapter extends SimpleCursorAdapter {
    static final String[] from = {DbHelper.C_CREATED_AT, DbHelper.C_USER, DbHelper.C_TEXT, DbHelper.C_USER_FULL_NAME};
    static final int[] to = {R.id.tweetHowLongAgo, R.id.statusProfileUserName, R.id.statusText, R.id.statusProfileUserFullName};

    /**
     * Constructor
     */
    public TimelineAdapter(Context context, Cursor c) {
        super(context, R.layout.tweet_list_item, c, from, to, 0);
    }

    /**
     * This is where data is mapped to its view
     */
    @Override
    public void bindView(@NonNull View row, Context context, @NonNull Cursor cursor) {
        super.bindView(row, context, cursor);

        // Get the individual pieces of data
        long createdAt = cursor.getLong(cursor
                .getColumnIndex(DbHelper.C_CREATED_AT));

        // Find views by id
        TextView textCreatedAt = (TextView) row.findViewById(R.id.tweetHowLongAgo);

        // Apply custom transformations
        textCreatedAt.setText(formatRelativeTimeString(createdAt));
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
