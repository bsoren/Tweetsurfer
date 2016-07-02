package com.anibij.demoapp.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class StatusContract {

	// DB specific constants
	// DB specific constants
	public static final String DB_NAME = "tweetsurfer_timeline.db"; // 1
	public static final int DB_VERSION = 5; // old 4
	public static final String TABLE = "status"; // 3

	public static final String UPDATE_INTERVAL = "UPDATE_INTERVAL";

	public static final String NEW_ITEMS = "com.anibij.demoapp.NEW_ITEMS";

	public static final String AUTHORITY = "com.anibij.demoapp";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE);
	public static final int STATUS_ITEM = 1;
	public static final int STATUS_DIR = 2;
	public static final String STATUS_TYPE_ITEM = "vnd.android.cursor.item/vnd.com.anibij.demoapp.provider.status";
	public static final String STATUS_TYPE_DIR = "vnd.android.cursor.dir/vnd.com.anibij.demoapp.provider.status";

	public static final String DEFAULT_SORT = Column.CREATED_AT + " DESC"; // 4

	public static final String STATUS_ID =  "STATUS_ID";

	public static final String TAB_FRAGMENT = "TAB_FRAGMENT";



	public class Column { // 5
		public static final String ID = BaseColumns._ID; // 6
		public static final String USER = "user";
		public static final String MESSAGE = "message";
		public static final String CREATED_AT = "created_at";
		public static final String PROFILE_IMAGE = "image_url";
		public static final String MEDIA_IMAGE = "media_url";
		public static final String MORE_ITEMS = "more_items";
		public static final String RETWEET_BY = "retweet_by";
		public static final String FAV_COUNT = "fav_count";
		public static final String RETWEET_COUNT = "retweet_count";
		public static final String SCREEN_NAME = "screen_name";
		public static final String IS_FAVOURITE = "is_favourite";
		public static final String IS_RETWEETED_BY_ME = "is_retweeted_by_me" ;
	}
}
