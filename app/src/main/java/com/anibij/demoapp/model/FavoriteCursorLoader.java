package com.anibij.demoapp.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * Created by bsoren on 21-Jan-16.
 */
public class FavoriteCursorLoader extends AsyncTaskLoader<Cursor> {

    private static final String TAG = FavoriteCursorLoader.class.getSimpleName();

    private Context mContext;
    private DbHelper mDbHelper;

    public FavoriteCursorLoader(Context context, DbHelper dbHelper) {
        super(context);
        this.mContext = context;
        this.mDbHelper = dbHelper;
    }

    @Override
    protected void onStartLoading() {
        Log.e(TAG, ":::: onStartLoading");

        super.onStartLoading();
    }

    @Override
    public Cursor loadInBackground() {
        Log.e(TAG, ":::: loadInBackground");

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + StatusContract.TABLE + " where " + StatusContract.Column.IS_FAVOURITE + "=1", null);

        if(c.moveToFirst()){
            Log.d(TAG, "Cursor Count : "+c.getCount());
        }

        return c;
    }

    @Override
    public void deliverResult(Cursor data) {
        Log.e(TAG, ":::: deliverResult");

        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        Log.e(TAG, ":::: onStopLoading");

        super.onStopLoading();
    }
}
