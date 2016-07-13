package com.anibij.demoapp.model;


import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.anibij.demoapp.Utils.AppPrefrences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsoren on 17-Oct-15.
 */
public class StatusListLoader extends AsyncTaskLoader<List<Status>> {


    private static final String TAG = StatusListLoader.class.getSimpleName();
    private Context mContext;
    private Uri mUri;
    private ContentResolver mContentResolver;
    private Cursor mCursor;
    private ContentObserver mContentObserver;
    private Handler mHandler;
    private SharedPreferences mSharedPreferences,mSharedPreferences2;
    private String contentProviderAuthority = "";

    List<Status> mStatues = new ArrayList<>();

    public StatusListLoader(Context context, Uri uri, ContentResolver contentResolver,Handler handler) {
        super(context);
        this.mContext = context;
        this.mUri = uri;
        this.mContentResolver = contentResolver;
        this.mHandler = handler;
        mSharedPreferences = new AppPrefrences(mContext).getInstance();
        mSharedPreferences2 = mContext.getSharedPreferences(AppPrefrences.PREF_NAME, 0);
        contentProviderAuthority = mUri.getAuthority();
    }

    @Override
    public List<Status> loadInBackground() {


        Log.d(TAG, "LoadInBackground");

        String[] projection = {StatusContract.Column.ID,
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
        };

        mCursor = mContentResolver.query(mUri, projection, null, null, StatusContract.Column.CREATED_AT + " DESC");

        List<Status> statusEntries = new ArrayList<>();
        int count = 0;

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                do {
                    long createdAt = mCursor.getLong(mCursor.getColumnIndex(StatusContract.Column.CREATED_AT));
                    String user = mCursor.getString(mCursor.getColumnIndex(StatusContract.Column.USER));
                    String id = mCursor.getString(mCursor.getColumnIndex(StatusContract.Column.ID));
                    String message = mCursor.getString(mCursor.getColumnIndex(StatusContract.Column.MESSAGE));
                    String profileImage = mCursor.getString(mCursor.getColumnIndex(StatusContract.Column.PROFILE_IMAGE));
                    String mediaImage = mCursor.getString(mCursor.getColumnIndex(StatusContract.Column.MEDIA_IMAGE));
                    String retweetBy =  mCursor.getString(mCursor.getColumnIndex(StatusContract.Column.RETWEET_BY));
                    int retweetCount = mCursor.getInt(mCursor.getColumnIndex(StatusContract.Column.RETWEET_COUNT));
                    int favCount = mCursor.getInt(mCursor.getColumnIndex(StatusContract.Column.FAV_COUNT));
                    String screenName = mCursor.getString(mCursor.getColumnIndex(StatusContract.Column.SCREEN_NAME));
                    int isFavouriteInt = mCursor.getInt(mCursor.getColumnIndex(StatusContract.Column.IS_FAVOURITE));
                    boolean isFavouriteBool = (isFavouriteInt == 1) ? true : false;

                    Status status = new Status(id, user, message, createdAt, profileImage, mediaImage, retweetBy, retweetCount, favCount, screenName);
                    status.setMoreItems(mCursor.getInt(mCursor.getColumnIndex(StatusContract.Column.MORE_ITEMS)));
                    status.setFavourite(isFavouriteBool);

                    statusEntries.add(status);

                    Log.d(TAG, "Loaded " + status);
                    count++;

                    if(count == 1){
                        if(contentProviderAuthority.equals("com.anibij.demoapp"))
                            mSharedPreferences2.edit().putLong(AppPrefrences.PREF_SINCE_ID,Long.valueOf(id)).commit();
                        else
                            mSharedPreferences2.edit().putLong(AppPrefrences.MENTION_PREF_SINCE_ID,Long.valueOf(id)).commit();
                        Log.d(TAG,"Since Id set to : "+id);
                    }

                } while (mCursor.moveToNext());
            }

            Log.d(TAG," No. of Items : "+count);
        } else {
            Log.d(TAG, "Cursor is NULL");
            mSharedPreferences2.edit().putLong(AppPrefrences.PREF_SINCE_ID,1000L).commit();
        }

        /*
        int newTweets = ((TimelineActivity)mContext).getTweetCount();
        String locations = mSharedPreferences.getString(AppPrefrences.PREF_LOAD_MORE_ITEM_LOCATIONS, "");
        StringBuilder sb = new StringBuilder(locations);

        if(newTweets>0) {
            statusEntries.add(newTweets, null);
            //locations = locations.concat(","+String.valueOf(newTweets));

        }
        Log.d(TAG,"Location: "+locations);

        if(!locations.isEmpty()){
            String[] loc = locations.split(",");
            for(String s:loc){
                if(!s.isEmpty()) {
                    int lc = Integer.valueOf(s);
                    statusEntries.add(lc+newTweets, null);
                    locations = locations.replace(String.valueOf(lc),String.valueOf(lc+newTweets));
                    Log.d(TAG, "Location not empty : "+lc);

                }
            }
        }
        locations = locations.concat(","+String.valueOf(newTweets));
        AppPrefrences.setPreference(AppPrefrences.PREF_LOAD_MORE_ITEM_LOCATIONS,sortLocations(locations));
        */

        return statusEntries;
    }


//    public StatusListLoader(Context context) {
//        super(context);
//    }

    @Override
    public void onCanceled(List<Status> statuses) {
        super.onCanceled(statuses);
        releaseResources(statuses);
//        if (mCursor != null) {
//
//            mCursor.close();
//        }
    }

    @Override
    public void deliverResult(List<Status> statuses) {

        Log.d(TAG, "Inside deliverResult");
        if (isReset()) {

            Log.d(TAG,"Is Reset...");
            // The Loader has been reset; ignore the result and invalidate the data.
//            if (mStatues != null) {
//                //releaseResources(statuses);
//                mCursor.close();
//            }

            releaseResources(statuses);
            return;

        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.

        List<Status> oldStatusList = mStatues;
        mStatues = statuses;

        if (isStarted()) {
            Log.d(TAG,"Is Started...");
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(statuses);
        }

//        if (mStatues == null || mStatues.size() == 0) {
//            Log.d(TAG, "****** No data *******");
//        }

        // Invalidate the old data as we don't need it any more.
        if (oldStatusList != null && oldStatusList != statuses) {
            releaseResources(oldStatusList);
        }
    }

        private void releaseResources(List<Status> statuses) {
//        if (mCursor != null) {
//            mCursor.close();
//        }
    }

    @Override
    protected void onStartLoading() {

        Log.d(TAG,"onStartLoading");

        if (mStatues != null) {

            // Deliver any previously loaded data immediately.
            Log.d(TAG,"calling deliverResult...");
            deliverResult(mStatues);
        }

//Start watching for changes in the app data.
//        if (mStatusObserver == null) {
//            Log.d(TAG, "register StatusObserver");
//            mStatusObserver = new StatusObserver(this);
//
//        }

//        Handler mHandler = new Handler();
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                onContentChanged();
//                Log.d(TAG,"Content Changed Notified to Loader");
//            }
//        });
//        if(mContentObserver == null) {
//            mContentObserver = new StatusContentObserver2(new Handler());
//            mContentResolver.registerContentObserver(StatusContract.CONTENT_URI, false, mContentObserver);
//            Log.d(TAG, "register StatusContentObserver");
//        }
//
//        if(mContentObserver == null) {
//            mContentObserver = new StatusContentObserver2(new Handler());
//            mContentResolver.registerContentObserver(StatusContract.CONTENT_URI, false, mContentObserver);
//            Log.d(TAG, "register StatusContentObserver");
//        }




        if (takeContentChanged() || mStatues == null) {

            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            Log.d(TAG, " Content has changed!!!");
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {

        Log.d(TAG,"onStopLoading..");

        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {

        Log.d(TAG, "onReset");
        // Ensure the loader has been stopped.
        onStopLoading();

         //At this point we can release the resources associated with 'mStatues'.
        if (mStatues != null) {
            releaseResources(mStatues);
            mStatues = null;
        }

        // stop monitoring
//        if(mContentObserver != null){
//            mContentResolver.unregisterContentObserver(mContentObserver);
//            Log.d(TAG, "Unregister StatusContentObserver");
//            mContentObserver = null;
//        }


    }



    /*********************************************************************/
    /** (4) Observer which receives notifications when the data changes **/
    /*********************************************************************/

    // NOTE: Implementing an observer is outside the scope of this post (this example
    // uses a made-up "SampleObserver" to illustrate when/where the observer should
    // be initialized).

    // The observer could be anything so long as it is able to detect content changes
    // and report them to the loader with a call to onContentChanged(). For example,
    // if you were writing a Loader which loads a list of all installed applications
    // on the device, the observer could be a BroadcastReceiver that listens for the
    // ACTION_PACKAGE_ADDED intent, and calls onContentChanged() on the particular
    // Loader whenever the receiver detects that a new application has been installed.
    // Please don’t hesitate to leave a comment if you still find this confusing! :)
    //private SampleObserver mObserver;

    class StatusContentObserver2 extends ContentObserver {

        private final String TAG = StatusContentObserver2.class.getSimpleName();
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public StatusContentObserver2(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG, "onContentChanged Called.");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);

            onContentChanged();
            Log.d(TAG, "onContentChanged with URI Called.");
        }
    }

}
