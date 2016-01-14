package com.anibij.demoapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anibij.demoapp.Utils.AppPrefrences;
import com.anibij.demoapp.listener.RecyclerItemClickListener;
import com.anibij.demoapp.model.Status;
import com.anibij.demoapp.model.StatusAdapter;
import com.anibij.demoapp.model.StatusContract;
import com.anibij.demoapp.model.StatusListLoader;
import com.anibij.demoapp.service.RefreshService;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Status>> {

    private static final String TAG = TimelineActivity.class.getSimpleName();
    private static final int LOADER_ID = 44;
    private int newTweets =  0;

    private List<Status> mStatusList = new ArrayList<Status>();

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private StatusAdapter mStatusAdapter;
    private ContentResolver mContentResolver;
    private SharedPreferences mSharedPreferences;

    private TextView noTweets;
    private Loader<List<Status>> mLoader;
    private IntentFilter mIntentFilter;
    private TimelineReceiver mReceiver;

    public int getTweetCount(){
        return newTweets;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIntentFilter =  new IntentFilter(StatusContract.NEW_ITEMS);
        mReceiver = new TimelineReceiver();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                startActivity(new Intent(TimelineActivity.this, StatusActivity.class));
            }
        });
        mSharedPreferences = new AppPrefrences(this).getInstance();


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe_refresh);
        noTweets = (TextView) findViewById(R.id.no_tweets);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                mSwipeRefreshLayout.setRefreshing(true);
                Toast.makeText(TimelineActivity.this, "Refreshing...", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(TimelineActivity.this, RefreshService.class);

                RefreshResult resultRefresh = new RefreshResult(new Handler());
                intent.putExtra("RefreshResult", resultRefresh);
                TimelineActivity.this.startService(intent);

            }
        });

        // Configure the refreshing colors
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {


                    @Override
                    public void onItemClick(View view, int position) {

                          //mStatusAdapter.setSelected(position);
                          int viewType = mStatusAdapter.getItemViewType(position);
                          Log.d(TAG, "viewType : " + viewType);

                        if (mStatusAdapter.getItemViewType(position) == 1) {
                            Toast.makeText(TimelineActivity.this, "Load More Item Clicked", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "viewType2 : " + viewType);

                            Status minStatus = mStatusList.get(position);
                            String minId = minStatus.getId();
                            String maxId = null;
                            Log.d(TAG, "Size : " + mStatusList.size());
                            Uri deleteUri = ContentUris.withAppendedId(StatusContract.CONTENT_URI,Long.valueOf(minId));
                            getContentResolver().delete(deleteUri,null, null);

                            Intent refreshIntent = new Intent(TimelineActivity.this,RefreshService.class);
                            refreshIntent.putExtra("minId",minId);
                            startService(refreshIntent);

                            mStatusList.remove(position);
                            mStatusAdapter.notifyItemRemoved(position);
                        }

                    }


                }));

        mStatusAdapter = new StatusAdapter(this, mStatusList);

        mRecyclerView.setAdapter(mStatusAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();

        boolean isLoggedIn = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
        }




    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
        //mStatusAdapter.loadNewData(getStatus());
    }

    @Override
    public Loader<List<Status>> onCreateLoader(int id, Bundle args) {
        Log.e(TAG, "Creating loader");
        mContentResolver = getContentResolver();
        mLoader = new StatusListLoader(this, StatusContract.CONTENT_URI, mContentResolver, new Handler());
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Status>> loader, List<Status> data) {
        mStatusList = data;
//        if(newTweets > 0){
//            mStatusList.add(newTweets,null);
//        }
        Log.e(TAG, "onLoadFinished");
        mStatusAdapter.setData(data);

        if (mStatusList.isEmpty()) {
            noTweets.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            noTweets.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        // The list should now be shown.
//        if (isResumed()) {
//            Log.e(TAG, "setListShown");
//            setListShown(true);
//
//        } else {
//            Log.e(TAG, "setListNoAnimation");
//            setListShownNoAnimation(true);
//        }


    }

    @Override
    public void onLoaderReset(Loader<List<Status>> loader) {
        Log.e(TAG, "onLoaderReset");
        mStatusAdapter.setData(null);
    }

//    private List<Status> getStatus() {
//
//        List<Status> statuses = new ArrayList<>();
//
//        String[] projection = new String[]{
//                StatusContract.Column.ID,
//                StatusContract.Column.USER,
//                StatusContract.Column.MESSAGE,
//                StatusContract.Column.CREATED_AT,
//                StatusContract.Column.PROFILE_IMAGE,
//                StatusContract.Column.MEDIA_IMAGE
//        };
//
//        Cursor cursor = mContentResolver.query(StatusContract.CONTENT_URI, projection, null, null
//                , StatusContract.DEFAULT_SORT);
//
//        String id, user, message, profileImage, mediaImage;
//        long createdAt;
//
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                do {
//                    id = cursor.getString(cursor.getColumnIndex(StatusContract.Column.ID));
//                    user = cursor.getString(cursor.getColumnIndex(StatusContract.Column.USER));
//                    message = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));
//                    createdAt = cursor.getLong(cursor.getColumnIndex(StatusContract.Column.CREATED_AT));
//                    profileImage = cursor.getString(cursor.getColumnIndex(StatusContract.Column.PROFILE_IMAGE));
//                    mediaImage = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MEDIA_IMAGE));
//                    Status status = new Status(id, user, message, createdAt, profileImage, mediaImage);
//                    statuses.add(status);
//
//                } while (cursor.moveToNext());
//            }
//
//
//        }
//        return statuses;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.refresh:
                Intent refIntent = new Intent(this, RefreshService.class);
                startService(refIntent);
                break;
            case R.id.purge:
                getContentResolver().delete(StatusContract.CONTENT_URI,null,null);
                Toast.makeText(this,"Deleting all records",Toast.LENGTH_SHORT).show();
                mLoader.onContentChanged();
                break;
            case R.id.sign_out:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected  void onPause(){
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    class TimelineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Calling TimlineReciever on Receive");
            newTweets = intent.getIntExtra("count",0);
            Toast.makeText(TimelineActivity.this, "Timeline count : " + newTweets, Toast.LENGTH_LONG).show();
            if(newTweets>0) {
                Log.d(TAG,"adding item to list");
//                mStatusList.add(newTweetCount, null);
//                mStatusAdapter.notifyItemInserted(newTweetCount);
               // newTweets = newTweetCount;
                //mLoader.onContentChanged();
            }
            mLoader.onContentChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    class RefreshResult extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public RefreshResult(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            String helloText = (String) resultData.get("Hello");
            int newTweetCount = resultData.getInt("count");
            Toast.makeText(TimelineActivity.this, "count : " + newTweetCount, Toast.LENGTH_LONG).show();
            mStatusList.add(newTweetCount, null);
            mStatusAdapter.notifyItemInserted(newTweetCount);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
