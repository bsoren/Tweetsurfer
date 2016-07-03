package com.anibij.demoapp.search;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.anibij.demoapp.R;
import com.anibij.demoapp.model.StatusContract;
import com.anibij.demoapp.view.SearchResultsActivity;

public class SearchResultActivity extends AppCompatActivity {

    private static final String TAG = SearchResultsActivity.class.getSimpleName();
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mToolbar = (Toolbar) findViewById(R.id.search_result_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String searchText = getIntent().getStringExtra(SearchFragment.SEARCH_TEXT);
        Bundle bundle = new Bundle();
        bundle.putInt(StatusContract.TAB_FRAGMENT, 0);
        bundle.putString(SearchFragment.SEARCH_TEXT,searchText);
        Log.d(TAG,"Search text "+searchText);

        SearchResultTabFragment fragment = new SearchResultTabFragment();
        fragment.setArguments(bundle);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.search_result_framelayout, fragment).commit();
    }
}
