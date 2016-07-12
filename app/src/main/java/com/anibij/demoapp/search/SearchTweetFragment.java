package com.anibij.demoapp.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
    private static final String LAST_ITEM_POSITION = "last_item_position";
    private static final String STATUS_LIST_ITEMS = "status_list_items";
    private static final String IS_LOAD_RESTARTED =  "restart_load";

    private Toolbar toolbar;

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private SearchResultAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SearchResultLoader mSearchResultLoader;
    private Button scrollTop;

    private View progressBarView;

    private ProgressDialog mProgressDialog;

    private String searchText;

    private SharedPreferences mSharedPreferences;


    // Internet Connection detector
    private ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    private List<Status> mStatusList;


    private Context mContext;

    private boolean loading;
    private boolean moreItems = true;
    private long initialMaxId = Long.MAX_VALUE;
    private long maxId = Long.MAX_VALUE;
    private long sinceId = Long.MIN_VALUE;

    private Handler uiHandler;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");
        mContext = getActivity();
        uiHandler = new Handler();
        mSharedPreferences = mContext.getSharedPreferences(AppPrefrences.PREF_NAME, 0);


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

        mStatusList = new ArrayList<Status>();
        // create an Object for Adapter
        mAdapter = new SearchResultAdapter(mContext, mStatusList);

        if (mStatusList.isEmpty()) {

            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }


        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int pastVisiblesItems, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // Log.d(TAG,"firstItem : "+mLayoutManager.findFirstVisibleItemPosition());
                // Log.d(TAG,"firstItem Id "+mAdapter.getItem(pastVisiblesItems+1).getId());

                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    Log.d(TAG,"loading : "+loading);
                        Log.d(TAG,"(visibleItemCount + pastVisiblesItems) >= totalItemCount :"
                               +((visibleItemCount + pastVisiblesItems) >= totalItemCount));
                        if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading= true;
                            Log.d(TAG, "before running loadMoreItems-> loading value is : "+loading);
                            //Do pagination.. i.e. fetch new data
                            loadMoreItems(totalItemCount);
                        }

            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView method - start");
        View view = inflater.inflate(R.layout.search_tweet_fragment, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tvEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        scrollTop = (Button) view.findViewById(R.id.scrollTop);
        progressBarView = inflater.inflate(R.layout.search_progress_bar,null);



//        mProgressDialog = new ProgressDialog(getActivity());
//        // Set progressdialog title
//        mProgressDialog.setTitle("Retrieving Results...");
//        // Set progressdialog message
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setIndeterminate(false);

       // boolean isLoggedIn = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
//        if (!isLoggedIn) {
//            startActivity(new Intent(mContext, LoginActivity.class));
//        }

        Log.d(TAG, "onCreateView method - end");

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState "+mStatusList.size());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestoryView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }

    private void loadMoreItems(int lastItem){
        Log.d(TAG, "running loadMoreItems");
        Bundle args = new Bundle();
        args.putInt(LAST_ITEM_POSITION,lastItem);
        args.putBoolean(IS_LOAD_RESTARTED,true);
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID,args,this);
    }


    @Override
    public android.support.v4.content.Loader<List<Status>> onCreateLoader(int id, Bundle args) {

        int lastItemPosition = args.getInt(LAST_ITEM_POSITION);
        Log.d(TAG, "onCreateLoader first item postion : "+lastItemPosition);

        if(mAdapter.getItem(lastItemPosition) != null) {
            maxId = new Long(mAdapter.getItem(lastItemPosition).getId());
        }

        boolean isRestartedLoad2 =  args.getBoolean(IS_LOAD_RESTARTED,false);

        Log.d(TAG,"MaxId :"+maxId);
        if(!isRestartedLoad2) {
            Log.d(TAG,"onCreate Load : isRestarted "+mStatusList.size()+" : "+isRestartedLoad2);
            mSearchResultLoader = new SearchResultLoader(mContext, searchText, maxId, null,isRestartedLoad2);
        }
        else {
            Log.d(TAG,"onScroll Load Restarting Loader : isRestarted "+mStatusList.size()+" : "+isRestartedLoad2);
            mSearchResultLoader = new SearchResultLoader(mContext, searchText, maxId, mStatusList,isRestartedLoad2);
        }

        mSearchResultLoader.setOnSearchResultLoaderListener(new SearchResultLoader.SearchResultLoaderListener() {
            @Override
            public void onLoadStart() {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loading = true;
                        mStatusList.add(null);
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }

            @Override
            public void onLoadFinished() {

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loading = false;
                        mStatusList.remove(mStatusList.size()-1);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        return mSearchResultLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Status>> loader,
                               List<Status> data) {


        Log.e(TAG, "onLoadFinished : "+data.size());

        Log.d(TAG,"Before mStatusList "+mStatusList.size());

        mStatusList = (ArrayList<Status>) data;
        mAdapter.setData(mStatusList);
        mAdapter.notifyDataSetChanged();

        Log.d(TAG,"After mStatusList : "+mStatusList.size());

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
        mStatusList = null;
        mAdapter.notifyDataSetChanged();
        //mAdapter.setData(null);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        Bundle args = new Bundle();
        args.putInt(LAST_ITEM_POSITION,mAdapter.getItemCount());
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, args, this);

        loading = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }
}
