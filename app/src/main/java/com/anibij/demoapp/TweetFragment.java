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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anibij.demoapp.Utils.AppPrefrences;
import com.anibij.demoapp.Utils.ConnectionDetector;
import com.anibij.demoapp.listener.RecyclerItemClickListener;
import com.anibij.demoapp.model.Status;
import com.anibij.demoapp.model.StatusAdapter;
import com.anibij.demoapp.model.StatusContract;
import com.anibij.demoapp.model.StatusListLoader;
import com.anibij.demoapp.service.RefreshService;
import com.anibij.demoapp.view.AlertDialogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsoren on 29-Dec-15.
 */
public class TweetFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Status>> {


    // for broadcast receiver
    private TimelineReceiver mTimelineReceiver;
    private IntentFilter mIntentFilter;

    private static final int LOADER_ID = 44;
    private static final String TAG = TweetFragment.class.getSimpleName();

    private Toolbar toolbar;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private StatusAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private StatusListLoader mStatusListLoader;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Button scrollTop;

    // Internet Connection detector
    private ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    private List<Status> mStatusList;


    private Context mContext;
    private ContentResolver mContentResolver;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTimelineReceiver = new TimelineReceiver();
        mIntentFilter = new IntentFilter(StatusContract.NEW_ITEMS);
        Log.d(TAG, "onActivityCreated method");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView method - start");
        mContext = getActivity();
        mContentResolver = getActivity().getContentResolver();
        mSharedPreferences = mContext.getSharedPreferences(AppPrefrences.PREF_NAME, 0);

        View view = inflater.inflate(R.layout.tweet_fragment, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tvEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe_refresh);
        scrollTop = (Button) view.findViewById(R.id.scrollTop);


        mStatusList = new ArrayList<Status>();


        scrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "scrolling to top", Toast.LENGTH_SHORT).show();
                mRecyclerView.scrollToPosition(0);

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                mSwipeRefreshLayout.setRefreshing(true);
                Toast.makeText(mContext, "Refreshing...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, RefreshService.class);

                long sinceIdLong = mSharedPreferences.getLong(AppPrefrences.PREF_SINCE_ID, 1000L);

                intent.putExtra("SINCE_ID", sinceIdLong);

                Log.d(TAG, "Sending since_id : " + sinceIdLong);

                cd = new ConnectionDetector(mContext);
                boolean isInternetAvailable = cd.isConnectingToInternet();

               // Toast.makeText(mContext,"Internet Available? "+isInternetAvailable,Toast.LENGTH_SHORT).show();

                if (!isInternetAvailable) {

                    mSwipeRefreshLayout.setRefreshing(false);

                    alert.showAlertDialog(mContext, "Internet Connection Error",
                            "Please connect to working Internet Connection", false);

                    // stop executing code by return

                    return;
                }

                mContext.startService(intent);

            }
        });

        // Configure the refreshing colors
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(mContext);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

        // RecyclerView Item Click

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext,
                new RecyclerItemClickListener.OnItemClickListener() {


                    @Override
                    public void onItemClick(View view, int position) {

                        //mStatusAdapter.setSelected(position);
                        int viewType = mAdapter.getItemViewType(position);
                        Log.d(TAG, "viewType : " + viewType);

                        if (mAdapter.getItemViewType(position) == 1) {
                            Toast.makeText(mContext, "Load More Item Clicked", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "viewType2 : " + viewType);

                            Status maxStatus = mStatusList.get(position - 1);
                            String maxId = maxStatus.getId();

                            Status deleteStatus = mStatusList.get(position);
                            String deleteId = deleteStatus.getId();

                            Log.d(TAG, "Size : " + mStatusList.size());
                            Uri deleteUri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, Long.valueOf(deleteId));
                            int rowsDeleted = mContentResolver.delete(deleteUri, null, null);

                            Log.d(TAG, "Row Deleted : " + rowsDeleted);

                            Intent refreshIntent = new Intent(mContext, RefreshService.class);
                            refreshIntent.putExtra("MAX_ID", deleteId);
                            mContext.startService(refreshIntent);

                            mStatusList.remove(position);
                            mAdapter.notifyItemRemoved(position);
                            //mAdapter.notifyItemChanged(position);
                        }
                        if (mAdapter.getItemViewType(position) == 0) {
                            Toast.makeText(mContext, "Tweet Clicked", Toast.LENGTH_SHORT).show();
                            Status status = mStatusList.get(position);
                            long statusId = Long.valueOf(status.getId());

                            Intent detailIntent = new Intent(mContext, DetailsActivity.class);
                            detailIntent.putExtra(StatusContract.Column.ID, statusId);
                            startActivity(detailIntent);
                        }

                    }


                }));


        // create an Object for Adapter
        mAdapter = new StatusAdapter(mContext, mStatusList);
        //mAdapter.setCustomDataAdapter(mAdapter);

        if (mStatusList.isEmpty()) {

            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);

        //	 mAdapter.notifyDataSetChanged();

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();

        boolean isLoggedIn = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
        if (!isLoggedIn) {
            startActivity(new Intent(mContext, LoginActivity.class));
        }

//        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//
//                // startActivity(new Intent(MainActivity.this,RefreshService.class));
//            }
//        });

        Log.d(TAG, "onCreateView method - end");

        return view;
    }


    @Override
    public android.support.v4.content.Loader<List<Status>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        mStatusListLoader = new StatusListLoader(mContext, StatusContract.CONTENT_URI, mContentResolver, new Handler());
        return mStatusListLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Status>> loader,
                               List<Status> data) {

        Log.e(TAG, "onLoadFinished :");

        if (mStatusList == null) {
            Log.d(TAG, "mStudent is null");
        }

        if (mStatusList != null) {
            Log.d(TAG, "mStudent is NOT null size : " + mStatusList.size());
        }

        mStatusList = data;

        Log.d(TAG, "data assigned : " + data.size());

        mAdapter.setData(data);

        if (mStatusList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Status>> loader) {
        Log.d(TAG, "onLoader Reset");
        mAdapter.setData(null);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        getActivity().registerReceiver(mTimelineReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        getActivity().unregisterReceiver(mTimelineReceiver);
    }

    // Broadcast Receiver
    class TimelineReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int newTweets = intent.getIntExtra("count", 0);
            int statusSize = intent.getIntExtra("datasize", 0);
            String twitterError =  intent.getStringExtra("TWITTER_ERROR");

            if(twitterError != null){
                mSwipeRefreshLayout.setRefreshing(false);
                alert.showAlertDialog(mContext, "Internet Connection Error",
                        "Error : "+twitterError, false);
               // Toast.makeText(mContext,"Error : "+twitterError,Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(mContext, "New Tweets : " + newTweets, Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "Total Size : " + statusSize, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Broadcast received By TweetFragment");
            getActivity().getSupportLoaderManager().getLoader(44).onContentChanged();
            // mStudentLoader.onContentChanged();
        }
    }


}
