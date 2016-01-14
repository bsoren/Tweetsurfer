package com.anibij.demoapp.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.anibij.demoapp.model.StatusListLoader;


/**
 * Created by bsoren on 18-Oct-15.
 */
public class StatusObserver extends BroadcastReceiver {

    public final static String NEW_STATUSES ="NEW_STATUSES";
    private static final String TAG = StatusObserver.class.getSimpleName();

    private StatusListLoader mLoader;

    public StatusObserver(){

    }

    public StatusObserver(StatusListLoader loader) {
        this.mLoader = loader;
        IntentFilter filter = new IntentFilter("com.anibij.demoapp.NEW_STATUSES");
        mLoader.getContext().registerReceiver(this, filter);
        Log.d(TAG,"registered content observer with loader");

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"calling adapter onContent change..");
        //int newTweetCount = intent.getIntExtra("count",0);
        mLoader.onContentChanged();
    }
}
