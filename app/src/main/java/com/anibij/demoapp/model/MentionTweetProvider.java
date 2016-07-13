package com.anibij.demoapp.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by bsoren on 13-Jul-16.
 */
public class MentionTweetProvider extends ContentProvider {

    private static final String TAG = MentionTweetProvider.class.getSimpleName() ;
    private DbHelper mDbHelper;

    private final static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;

    static  {

        mUriMatcher.addURI(StatusContract.MENTION_AUTHORITY,
                StatusContract.MentionTweet.TABLE_NAME,StatusContract.MENTION_STATUS_DIR);
        mUriMatcher.addURI(StatusContract.AUTHORITY,
                StatusContract.MentionTweet.TABLE_NAME+"/#",StatusContract.MENTION_STATUS_ITEM);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        String where = "";
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(StatusContract.MentionTweet.TABLE_NAME);

        switch (mUriMatcher.match(uri)){
            case StatusContract.MENTION_STATUS_DIR :
                where = selection;
                break;
            case StatusContract.MENTION_STATUS_ITEM :
                where = StatusContract.MentionTweet.Column.ID
                        + "="
                        + uri.getLastPathSegment()
                        +((selection != null)?" and "+ selection +"= "+selectionArgs:"");
                break;
            default:
                throw new IllegalArgumentException("illegal uri : "+uri);
        }

        String order =  sortOrder == null ? StatusContract.DEFAULT_SORT :sortOrder;

        Cursor cursor =  queryBuilder.query(db,projection,selection,selectionArgs,null,null,order);

        // register for uri changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (mUriMatcher.match(uri)) {
            case StatusContract.MENTION_STATUS_DIR:
                return StatusContract.MENTION_STATUS_TYPE_DIR;
            case StatusContract.MENTION_STATUS_ITEM:
                return StatusContract.MENTION_STATUS_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri retUri = null;

        if(mUriMatcher.match(uri) != StatusContract.MENTION_STATUS_DIR){
            throw new IllegalArgumentException("illegal uri "+uri);
        }

        long rowId  = db.insertWithOnConflict(StatusContract.MentionTweet.TABLE_NAME, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);

        if(rowId != 1){
            long id = values.getAsLong(StatusContract.MentionTweet.Column.ID);
            retUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(retUri,null);
        }else{
            Log.d(TAG,"Insert unsuccessful ");
        }

        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        String where;

        switch(mUriMatcher.match(uri)){
            case StatusContract.MENTION_STATUS_DIR :
                where = (selection == null)?"1":selection;
                break;
            case StatusContract.MENTION_STATUS_ITEM:
                where = StatusContract.MentionTweet.Column.ID
                        + "="
                        +uri.getLastPathSegment()
                        + ((TextUtils.isEmpty(selection))?" ":" and "+selection);
                break;
            default:
                throw  new IllegalArgumentException("illegal uri"+uri);
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int ret = db.delete(StatusContract.MentionTweet.TABLE_NAME,where,selectionArgs);

        if(ret > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
