package com.anibij.demoapp.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
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

import com.anibij.demoapp.R;
import com.anibij.demoapp.Utils.AppPrefrences;
import com.anibij.demoapp.Utils.ConnectionDetector;
import com.anibij.demoapp.model.Status;
import com.anibij.demoapp.view.AlertDialogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsoren on 29-Dec-15.
 */
public class SearchTweetFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Status>> {

    private static final int LOADER_ID = 11;
    private static final String TAG = SearchTweetFragment.class.getSimpleName();

    private Toolbar toolbar;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private SearchResultAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SearchResultLoader mSearchResultLoader;
    private Button scrollTop;

    private String searchText;

    private SharedPreferences mSharedPreferences;


    // Internet Connection detector
    private ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    private List<Status> mStatusList;


    private Context mContext;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        searchText =  getArguments().getString(SearchFragment.SEARCH_TEXT);
        mSharedPreferences = mContext.getSharedPreferences(AppPrefrences.PREF_NAME, 0);
        Log.d(TAG, "SearchText "+searchText);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView method - start");
        mContext = getActivity();


        View view = inflater.inflate(R.layout.tweet_fragment, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tvEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        scrollTop = (Button) view.findViewById(R.id.scrollTop);


        mStatusList = new ArrayList<Status>();


        scrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "scrolling to top", Toast.LENGTH_SHORT).show();
                mRecyclerView.scrollToPosition(0);

            }
        });

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(mContext);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

        // create an Object for Adapter
        mAdapter = new SearchResultAdapter(mContext, mStatusList);
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

       // boolean isLoggedIn = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
//        if (!isLoggedIn) {
//            startActivity(new Intent(mContext, LoginActivity.class));
//        }

        Log.d(TAG, "onCreateView method - end");

        return view;
    }


    @Override
    public android.support.v4.content.Loader<List<Status>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        mSearchResultLoader = new SearchResultLoader(mContext,searchText);

        return mSearchResultLoader;
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

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Status>> loader) {
        Log.d(TAG, "onLoader Reset");
        mAdapter.setData(null);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }
}
