package com.anibij.demoapp.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class DirectMessageProvider extends ContentProvider {

    private DbHelper mDbHelper;
    private SQLiteDatabase db;

    private static final UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);

    static {

        matcher.addURI(StatusContract.DIRECT_MESSAGE_AUTHORITY,
                StatusContract.DirectMessage.TABLE_NAME,StatusContract.DIRECT_MESSAGE_DIR);

        matcher.addURI(StatusContract.DIRECT_MESSAGE_AUTHORITY,StatusContract.DirectMessage.TABLE_NAME
        +"/#",StatusContract.DIRECT_MESSAGE_ITEM);

        matcher.addURI(StatusContract.DIRECT_MESSAGE_AUTHORITY,StatusContract.DirectMessage.TABLE_NAME
        +"/GROUP_BY_SENDER",StatusContract.DIRECT_MESSAGE_GROUP_BY_SENDER);
    }

    public DirectMessageProvider(){

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = mDbHelper.getWritableDatabase();
        String where = null;
        switch (matcher.match(uri)){
            case StatusContract.DIRECT_MESSAGE_DIR:
                where = selection == null ? "1" : selection;
                break;
            case StatusContract.DIRECT_MESSAGE_ITEM:
                where =  StatusContract.DirectMessage.Column.ID
                        + " = "
                        + uri.getLastPathSegment()
                        +(TextUtils.isEmpty(selection)?"":"and ("+ selection + ")");
                break;
            default:
                throw  new IllegalArgumentException("Illegal uri "+uri);
        }

        int deletedCount = db.delete(StatusContract.DirectMessage.TABLE_NAME, where, selectionArgs);

        if(deletedCount > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return deletedCount;
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)){
            case StatusContract.DIRECT_MESSAGE_DIR :
                return StatusContract.DIRECT_MESSAGE_TYPE_DIR;
            case StatusContract.DIRECT_MESSAGE_ITEM:
                return StatusContract.DIRECT_MESSAGE_TYPE_ITEM;
            case StatusContract.DIRECT_MESSAGE_GROUP_BY_SENDER:
                return StatusContract.DIRECT_MESSAGE_TYPE_DIR;
            default:
                throw  new IllegalArgumentException("Illegal uri : "+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri retUri = null;
        db = mDbHelper.getWritableDatabase();

        if(matcher.match(uri) != StatusContract.DIRECT_MESSAGE_DIR){
            throw new IllegalArgumentException("illegal uri : "+uri);
        }

        long id = db.insertWithOnConflict(StatusContract.DirectMessage.TABLE_NAME,null, values, SQLiteDatabase.CONFLICT_IGNORE);

        if(id != -1){
            long _id = values.getAsLong(StatusContract.DirectMessage.Column.ID);
            retUri = ContentUris.withAppendedId(uri,id);
            getContext().getContentResolver().notifyChange(retUri,null);
        }


        return retUri;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String where;
        String groupBy = null;
        String having = null;
        db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder =  new SQLiteQueryBuilder();
        queryBuilder.setTables(StatusContract.DirectMessage.TABLE_NAME);

        switch (matcher.match(uri)){
            case StatusContract.DIRECT_MESSAGE_DIR:
                where = selection;
                break;
            case StatusContract.DIRECT_MESSAGE_ITEM:
                where = StatusContract.DirectMessage.Column.ID
                        + " = "
                        + uri.getLastPathSegment()
                        + (TextUtils.isEmpty(selection)?"": " and ("+ selection + " )");
                break;
            case StatusContract.DIRECT_MESSAGE_GROUP_BY_SENDER:
                where = selection;
                groupBy = StatusContract.DirectMessage.Column.SENDER_ID;
                having = StatusContract.DirectMessage.Column.CREATED_AT + "=MAX("+StatusContract.DirectMessage.Column.CREATED_AT +")";
                break;
            default:
                throw new IllegalArgumentException("illegal uri :"+uri);
        }

        String orderBy = (sortOrder != null? sortOrder : StatusContract.DirectMessage.Column.CREATED_AT + " DESC ");
        Cursor cursor = queryBuilder.query(db, projection, where,selectionArgs,groupBy, having,orderBy);

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return  cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
