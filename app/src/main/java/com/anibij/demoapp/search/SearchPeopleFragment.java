/**
 * Obsolete - Don't  use
 */

package com.anibij.demoapp.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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


public class SearchPeopleFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<User>>,AbsListView.OnScrollListener {

    private static final String TAG = SearchPeopleFragment.class.getSimpleName() ;
    private static final int LOADER_ID = 99;
    int loaderid = 100;
    private SharedPreferences mSharedPreferences;
    // Declare Variables
    private ListView mListView;
    ProgressBar mProgressBar;
    private SearchUserAdapter mSearchUserAdapter;
    private UserResultLoader mResultLoader;
    private List<User> userList;
    ConnectionDetector cd;
    Context mContext;
    TextView mNoRecordView;
    AlertDialogManager alert = new AlertDialogManager();
    private boolean loading = true;
    private int pageCount = 1;
    int previousTotal = 0;

    public SearchPeopleFragment() {
        // Required empty public constructor
    }

    public static SearchPeopleFragment newInstance() {
        SearchPeopleFragment fragment = new SearchPeopleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userList = new ArrayList<>();
        mSharedPreferences = getActivity().getSharedPreferences(AppPrefrences.PREF_NAME, 0);
        Log.d(TAG,"onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View v = inflater.inflate(R.layout.search_user_list, container, false);
        mListView = (ListView) v.findViewById(R.id.search_user_listview);

        View progressBarView = inflater.inflate(R.layout.search_progress_bar,null);
        mProgressBar = (ProgressBar)progressBarView.findViewById(R.id.search_progress_footer);

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

        Log.d(TAG,"onActivityCreated");

        mSearchUserAdapter = new SearchUserAdapter(getActivity(),userList);
        mListView.setAdapter(mSearchUserAdapter);
        mListView.setOnScrollListener(this);
        mListView.addFooterView(mProgressBar);

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID,null,this).forceLoad();

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
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        Log.d(TAG,"isLoading : Count : "+ loading + " : "+pageCount);
        mResultLoader =  new UserResultLoader(getActivity(),pageCount);
        return mResultLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        Log.d(TAG,"onLoadFinished "+data.size());
        mSearchUserAdapter.setData(data);
        //mListView.removeFooterView(mProgressBar);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {
        Log.d(TAG,"onLoaderReset");
        mSearchUserAdapter.setData(null);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        Log.d(TAG,"onscroll");
        if(loading) {
            if(totalItemCount > previousTotal) {
                // the loading has finished
                loading = false ;
                previousTotal = totalItemCount ;
            }
        }

        // check if the List needs more data
        if(!loading && ((firstVisibleItem + visibleItemCount ) >= (totalItemCount))){
            loading = true ;
            mListView.addFooterView(mProgressBar);
            pageCount++;
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
        }
    }



//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if(isVisibleToUser && !isLoading){
//            getActivity().getSupportLoaderManager().initLoader(27, null, this);
//        }
//    }
}
