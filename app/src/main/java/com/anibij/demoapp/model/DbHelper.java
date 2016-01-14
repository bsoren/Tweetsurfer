package com.anibij.demoapp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper { // 1
	public static final String TAG = DbHelper.class.getSimpleName();

	public DbHelper(Context context) {
		super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
		// 2
	}

	// Called only once first time we create the database
	@Override
        public void onCreate(SQLiteDatabase db) {
                String sql = String.format("create table %s (%s int primary key, %s text,%s text, %s long,%s text,%s text,%s int,%s text,%s int,%s int)",
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
                        StatusContract.Column.FAV_COUNT
                );
                        // 3
                Log.d(TAG, "onCreate with SQL: " + sql);
                db.execSQL(sql); // 4
        }

	// Gets called whenever existing version != new version, i.e. schema changed
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 5
		// Typically you do ALTER TABLE ...
            Log.d(TAG,"****** Dropping Table ****** ");
		db.execSQL("drop table if exists " + StatusContract.TABLE);
		onCreate(db);

	}

}	