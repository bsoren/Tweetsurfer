/**
 * Obsolete - Don't  use
 */

package com.anibij.demoapp.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anibij.demoapp.R;
import com.anibij.demoapp.Utils.AppPrefrences;
import com.anibij.demoapp.Utils.ConnectionDetector;
import com.anibij.demoapp.model.User;
import com.anibij.demoapp.view.AlertDialogManager;

import java.util.ArrayList;
import java.util.List;


public class SearchUserFragment extends Fragment
        implements AbsListView.OnScrollListener {

    private static final String TAG = SearchUserFragment.class.getSimpleName() ;
    private static final String PAGINATION_COUNT = "pagination_count" ;
    private static final String USER_ITEM_LISTS = "user_lists" ;
    private static final String TASK_RUNNING_STATUS = "task_running_status";
    private static final String IS_MORE_ITEMS_AVAILABLE = "is_more_items_available";

    private SharedPreferences mSharedPreferences;
    // Declare Variables
    private ListView mListView;
    ProgressBar mProgressBar;
    private SearchUserAdapter mSearchUserAdapter;
    View progressBarView;

    private ArrayList<User> userList;
    ConnectionDetector cd;
    TextView mNoRecordView;
    AlertDialogManager alert = new AlertDialogManager();
    private boolean loading = false;
    private int pageCount = 1;
    int previousTotal = 0;
    private boolean initializeScroll = false;
    private boolean noMoreItems =  false;
    private Context mContext;
    private SearchUserTask mSearchUserTask;

    public SearchUserFragment() {
        // Required empty public constructor
    }

    public static SearchUserFragment newInstance() {
        SearchUserFragment fragment = new SearchUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View v = inflater.inflate(R.layout.search_user_list, container, false);
        mListView = (ListView) v.findViewById(R.id.search_user_listview);
        progressBarView = inflater.inflate(R.layout.search_progress_bar,null);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"In onSavedInstanceState()");

        //saving isMoreItems available
        outState.putBoolean(IS_MORE_ITEMS_AVAILABLE,noMoreItems);
        // save pagecount
        outState.putInt(PAGINATION_COUNT,pageCount);
        //save arraylist data
        outState.putSerializable(USER_ITEM_LISTS,userList);
        // save task running status
        if(isTaskRunning()) {
            outState.putBoolean(TASK_RUNNING_STATUS, true);
        }

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG,"onViewStateRestored");
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSharedPreferences = getActivity().getSharedPreferences(AppPrefrences.PREF_NAME, 0);
        Log.d(TAG,"In onActivityCreated()");

        userList = new ArrayList<>();
        pageCount = 1;
        initializeScroll = false;
        noMoreItems = false;

        mSearchUserAdapter = new SearchUserAdapter(getActivity(),userList);
        mListView.setAdapter(mSearchUserAdapter);
        mListView.setOnScrollListener(this);


        if(savedInstanceState != null){
            Log.d(TAG,"restoring saveInstance");

            noMoreItems = savedInstanceState.getBoolean(IS_MORE_ITEMS_AVAILABLE,false);
            pageCount =  savedInstanceState.getInt(PAGINATION_COUNT,1);
            userList = (ArrayList<User>) savedInstanceState.getSerializable(USER_ITEM_LISTS);
            Log.d(TAG,"In onActivityCreated() and savedInstance is NOT NULL, userList size : "+userList.size());

            mSearchUserAdapter.setData(userList);
            //mSearchUserAdapter.notifyDataSetChanged();

            if(savedInstanceState.getBoolean(TASK_RUNNING_STATUS,false)){
                loading = true;
                mSearchUserTask = new SearchUserTask();
                mSearchUserTask.setPageCount(pageCount);
                mSearchUserTask.execute();
            }else{
                loading = false;
            }

        }else {

            Log.d(TAG,"savedInstance is null");

            mSearchUserTask = new SearchUserTask();
            mSearchUserTask.setPageCount(pageCount);
            mSearchUserTask.execute();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        initializeScroll = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach");
        mContext = context;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.d(TAG,"onDetach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if(isTaskRunning()) {
            Log.d(TAG, "cancelling task");
            mSearchUserTask.cancel(true);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    private boolean isTaskRunning(){
        return (mSearchUserTask != null) &&
                (mSearchUserTask.getStatus() == AsyncTask.Status.RUNNING);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        // Log.d(TAG,"inside onScroll");

        //Log.d(TAG,"onscroll, totalItem, pageCount : "+totalItemCount + " : "+pageCount);
        // check if the List needs more data

        if (firstVisibleItem > 0) {

            if (initializeScroll && !loading && ((firstVisibleItem + visibleItemCount) >= (totalItemCount))) {
                Log.d(TAG, "In onScroll(): About to load data : firstvisible item : totalvisibleItem :"
                        + firstVisibleItem + " : " + totalItemCount + " Loading Status : " + loading);

                loading = true;
                Log.d(TAG, "just before loading :" + loading);
                if (!noMoreItems) {
                    loadMoreItems();
                }
            }
        }

    }


    private void loadMoreItems(){
        Log.d(TAG,"In loadMoreItems() :loading more users");
            mSearchUserTask = new SearchUserTask();
            mSearchUserTask.setPageCount(pageCount);
            mSearchUserTask.execute();

    }

    private void incrementPageCount(){
        pageCount++;
    }

    private class SearchUserTask extends AsyncTask<Void,Void,List<User>>{

        private int asyncTaskPageCount;

        private void setPageCount(int count){
            this.asyncTaskPageCount = count;
        }

        @Override
        protected List<User> doInBackground(Void... params) {
            Log.d(TAG,"doInBackground fetching users");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new SearchUtility(getActivity()).fetchTwitterSearchUsers(asyncTaskPageCount);
        }


        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            Log.d(TAG,"onPostExecute currentPageCount and user size  "+asyncTaskPageCount+ " : " +users.size());

            if(users.size() < 20){
                noMoreItems = true;
            }
            if(users.size() >= 20){
                incrementPageCount();
            }

            mListView.removeFooterView(progressBarView);
            userList.addAll(users);
            mSearchUserAdapter.notifyDataSetChanged();
            //mSearchUserAdapter.setData(users);
            loading = false;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mListView.addFooterView(progressBarView);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
