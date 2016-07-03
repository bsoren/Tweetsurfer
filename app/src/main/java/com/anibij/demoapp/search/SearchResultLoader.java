package com.anibij.demoapp.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.anibij.demoapp.Utils.AppPrefrences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsoren on 03-Jul-16.
 */
public class SearchResultLoader extends AsyncTaskLoader<List<com.anibij.demoapp.model.Status>> {

    private static final String TAG = SearchResultLoader.class.getSimpleName();
    private static final int TWEETS_NUMBER = 50 ;
    private static final int MAX_SEARCH_RESULT = 50;
    private Context mContext;
    private String searchText;
    private List<com.anibij.demoapp.model.Status> mStatues;


    private static SharedPreferences mSharedPreferences;

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    private static final String consumerKey = "o7kn8lHPoThttJhOejus6r1wJ";
    private static final String consumerSecret = "EfL1dRYw0xw6lWYogM4A7kuwCSwl2eeCINA746qTT28SSJsJnb";

    public SearchResultLoader(Context context,String searchText) {
        super(context);
        mStatues = new ArrayList<>();
        this.mContext =  context;
        this.searchText = searchText;
         /* Initialize application preferences */
        mSharedPreferences = mContext.getSharedPreferences(AppPrefrences.PREF_NAME, 0);
    }

    @Override
    public List<com.anibij.demoapp.model.Status> loadInBackground() {

        return new SearchUtility(mContext).fetchTwitterSearchTweets();
    }


    @Override
    public void onCanceled(List<com.anibij.demoapp.model.Status> statuses) {
        super.onCanceled(statuses);
        releaseResources(statuses);
    }

    @Override
    public void deliverResult(List<com.anibij.demoapp.model.Status> statuses) {

        Log.d(TAG, "Inside deliverResult");
        if (isReset()) {

            Log.d(TAG,"Is Reset...");
            releaseResources(statuses);
            return;

        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.

        List<com.anibij.demoapp.model.Status> oldStatusList = mStatues;
        mStatues = statuses;


        if (isStarted()) {
            Log.d(TAG,"Is Started...");
            super.deliverResult(statuses);
        }

        if (oldStatusList != null && oldStatusList != statuses) {
            releaseResources(oldStatusList);
        }
    }

    private void releaseResources(List<com.anibij.demoapp.model.Status> statuses) {
        statuses = null;
    }

    @Override
    protected void onStartLoading() {

        Log.d(TAG,"onStartLoading");

        if (mStatues != null) {

            // Deliver any previously loaded data immediately.
            Log.d(TAG,"calling deliverResult...");
            deliverResult(mStatues);
        }

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
        }
    }


}