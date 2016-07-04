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

    private SharedPreferences mSharedPreferences;
    // Declare Variables
    private ListView mListView;
    ProgressBar mProgressBar;
    private SearchUserAdapter mSearchUserAdapter;
    View progressBarView;

    private List<User> userList;
    ConnectionDetector cd;
    TextView mNoRecordView;
    AlertDialogManager alert = new AlertDialogManager();
    private boolean loading = true;
    private int pageCount = 1;
    int previousTotal = 0;
    private boolean isMoreItems = true;
    private Context mContext;

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
        userList = new ArrayList<>();
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
        Log.d(TAG,"onSavedInstanceState");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG,"onViewStateRestored");
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSharedPreferences = getActivity().getSharedPreferences(AppPrefrences.PREF_NAME, 0);
        Log.d(TAG,"onActivityCreated");

        mSearchUserAdapter = new SearchUserAdapter(getActivity(),userList);
        mListView.setAdapter(mSearchUserAdapter);
        mListView.setOnScrollListener(this);
        new SearchUserTask(1).execute();

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
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        //Log.d(TAG,"onscroll, totalItem, pageCount : "+totalItemCount + " : "+pageCount);
        // check if the List needs more data
        if(isMoreItems && !loading && ((firstVisibleItem + visibleItemCount ) >= (totalItemCount))){
            loading = true ;
            loadMoreItems();

        }
    }


    private void loadMoreItems(){
        new SearchUserTask(pageCount).execute();
    }

    private void incrementPageCount(){
        pageCount++;
    }

    private class SearchUserTask extends AsyncTask<Void,Void,List<User>>{

        private int pageCount;

        @Override
        protected List<User> doInBackground(Void... params) {
            Log.d(TAG,"fetching users");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new SearchUtility(getActivity()).fetchTwitterSearchUsers(pageCount);
        }

        public SearchUserTask(int pageCount) {
            super();
            this.pageCount = pageCount;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            Log.d(TAG,"received pcount and users  "+pageCount+ " : " +users.size());
            if(users.size()== 0){
                isMoreItems = false;
            }
            if(users.size() > 0){
                incrementPageCount();
            }
            mListView.removeFooterView(progressBarView);
            mSearchUserAdapter.setData(users);
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
