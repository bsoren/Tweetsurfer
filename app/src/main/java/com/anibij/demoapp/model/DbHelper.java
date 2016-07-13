package com.anibij.demoapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	public static final String TAG = DbHelper.class.getSimpleName();

	public DbHelper(Context context) {
		super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
	}

	// Called only once first time we create the database
	@Override
        public void onCreate(SQLiteDatabase db) {
            String statusTable  = String.format("create table %s (%s int primary key, %s text,%s text, %s long,%s text,%s text,%s int,%s text,%s int,%s int,%s text,%s int)",
                        StatusContract.TABLE,
                        StatusContract.Column.ID,
                        StatusContract.Column.USER,
                        StatusContract.Column.MESSAGE,
                        StatusContract.Column.CREATED_AT,
                        StatusContract.Column.PROFILE_IMAGE,
                        StatusContract.Column.MEDIA_IMAGE,
                        StatusContract.Column.MORE_ITEMS,
                        StatusContract.Column.RETWEET_BY,
                        StatusContract.Column.RETWEET_COUNT,
                    StatusContract.Column.FAV_COUNT,
                    StatusContract.Column.SCREEN_NAME,
                    StatusContract.Column.IS_FAVOURITE
                );

		String mentionStatusTable = String.format("create table %s (%s int primary key, %s text,%s text, %s long,%s text,%s text,%s int,%s text,%s int,%s int,%s text,%s int)",
				StatusContract.MentionTweet.TABLE_NAME,
				StatusContract.MentionTweet.Column.ID,
				StatusContract.MentionTweet.Column.USER,
				StatusContract.MentionTweet.Column.MESSAGE,
				StatusContract.MentionTweet.Column.CREATED_AT,
				StatusContract.MentionTweet.Column.PROFILE_IMAGE,
				StatusContract.MentionTweet.Column.MEDIA_IMAGE,
				StatusContract.MentionTweet.Column.MORE_ITEMS,
				StatusContract.MentionTweet.Column.RETWEET_BY,
				StatusContract.MentionTweet.Column.RETWEET_COUNT,
				StatusContract.MentionTweet.Column.FAV_COUNT,
				StatusContract.MentionTweet.Column.SCREEN_NAME,
				StatusContract.MentionTweet.Column.IS_FAVOURITE
		);
                        // 3
                Log.d(TAG, "onCreate with SQL: " + statusTable);
				Log.d(TAG, "onCreate with SQL: " + mentionStatusTable);
                db.execSQL(statusTable); // 4
				db.execSQL(mentionStatusTable);
        }

	// Gets called whenever existing version != new version, i.e. schema changed
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 5
		// Typically you do ALTER TABLE ...
            Log.d(TAG,"****** Dropping Table ****** ");
            db.execSQL("drop table if exists " + StatusContract.TABLE);
		    db.execSQL("drop table if exists " + StatusContract.MentionTweet.TABLE_NAME);
            onCreate(db);
//            String query = String.format("alter table %s add column %s", StatusContract.TABLE, StatusContract.Column.IS_FAVOURITE);
//            db.execSQL(query);

	}

}	