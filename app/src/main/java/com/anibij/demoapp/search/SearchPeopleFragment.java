package com.anibij.demoapp.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.anibij.demoapp.R;
import com.anibij.demoapp.Utils.ConnectionDetector;
import com.anibij.demoapp.model.User;
import com.anibij.demoapp.view.AlertDialogManager;

import java.util.ArrayList;
import java.util.List;


public class SearchPeopleFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<User>> {

    private static final String TAG = SearchPeopleFragment.class.getSimpleName() ;
    // Declare Variables
    private ListView mListView;
    ProgressDialog mProgressDialog;
    private ArrayAdapter<User> mSearchUserAdapter;
    private UserResultLoader mResultLoader;
    private List<User> userList = new ArrayList<>();
    ConnectionDetector cd;
    Context mContext;
    TextView mNoRecordView;
    AlertDialogManager alert = new AlertDialogManager();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        cd = new ConnectionDetector(mContext);
        boolean isInternetAvailable = cd.isConnectingToInternet();

        if (!isInternetAvailable) {

            alert.showAlertDialog(mContext, "Internet Connection Error",
                    "Please connect to working Internet Connection", false);
        }

        View v = inflater.inflate(R.layout.search_user_list, container, false);
        mListView = (ListView) v.findViewById(R.id.search_user_listview);

        getActivity().getSupportLoaderManager().initLoader(22, null, this).forceLoad();

        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach(){
        super.onDetach();
    }


    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader");
        mResultLoader =  new UserResultLoader(mContext);
        return mResultLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        userList = data;
        Log.d(TAG,"onLoadFinished "+data.size());
        if(userList != null){
            mSearchUserAdapter = new SearchUserAdapter(mContext,userList);
            mListView.setAdapter(mSearchUserAdapter);
            mSearchUserAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {
        userList = null;
        mSearchUserAdapter.notifyDataSetChanged();
    }
}
