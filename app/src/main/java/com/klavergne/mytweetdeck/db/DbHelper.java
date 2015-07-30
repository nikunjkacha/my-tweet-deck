package com.klavergne.mytweetdeck.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import twitter4j.Status;

/**
 * Created by Kevin LaVergne on 11/5/2014.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String TABLE = "timeline";
    private static final String TAG = DbHelper.class.getSimpleName();
    private static final String DB_NAME = "timeline.db";
    private static final int DB_VERSION = 2;
    // COLUMNS
    public static final String C_ID = BaseColumns._ID;
    public static final String C_CREATED_AT = "created_at";
    public static final String C_TEXT = "status";
    public static final String C_USER = "user";
    public static final String C_USER_FULL_NAME = "user_full_name";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static ContentValues statusToContentValues(Status status) {
        ContentValues ret = new ContentValues();
        ret.put(C_ID, status.getId());
        ret.put(C_CREATED_AT, status.getCreatedAt().getTime());
        ret.put(C_TEXT, status.getText());
        ret.put(C_USER, status.getUser().getScreenName());
        ret.put(C_USER_FULL_NAME, status.getUser().getName());
        return ret;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s (%s integer nor null primary key, %s integer, %s text, %s text, %s text);", TABLE, C_ID, C_CREATED_AT, C_TEXT, C_USER, C_USER_FULL_NAME);
        db.execSQL(sql);
        Log.d(TAG, "onCreate db: " + sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("onUpgrade from %s to %s", oldVersion, newVersion));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", TABLE));
        this.onCreate(db);
    }
}
