/***
  Copyright (c) 2008-2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.anibij.demoapp.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anibij.demoapp.R;
import com.anibij.demoapp.Utils.AppPrefrences;
import com.anibij.demoapp.model.SearchHelpAdapter;
import com.anibij.demoapp.model.SearchItem;

import java.util.ArrayList;

public class SearchFragment extends ListFragment implements
    TextView.OnEditorActionListener, SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {
  private static final String STATE_QUERY="q";
  private static final String STATE_MODEL="m";

  private static final String[] searchName= { "Twitter", "@skvijay42","Another Person" };
  private static final int[] searchImage = { R.drawable.twitter_search_image,R.drawable.twitter_placeholder_image,R.drawable.twitter_placeholder_image};
  private static final String[] searchItems = { "Tweets, People, Nearby2", "Tweets,Mentions,Favourite,Messages","Tweets,People" };
  public static final String SEARCH_TEXT = "search_text";
  private static String searchText = "";

  private ArrayAdapter<SearchItem> adapter=null;
  private CharSequence initialQuery=null;
  private SearchView sv=null;
  android.support.v7.widget.Toolbar toolbar;
  Context  context;
  private ArrayList<SearchItem> mSearchItems;
  private ListView mListView;
    private SharedPreferences mSharedPreferences;

    @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context =  context;
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
      mSharedPreferences = context.getSharedPreferences(AppPrefrences.PREF_NAME, 0);
    mListView = getListView();
    mListView.setVisibility(View.GONE);
    int[] colors = {0, 0xFFFF0000, 0}; // red for the example
    mListView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
    mListView.setDividerHeight(3);

    if (savedInstanceState == null) {
      initAdapter(null);
    }
    else {
      //initAdapter((ArrayList<SearchItem>) savedInstanceState.getSerializable(STATE_MODEL));
        initAdapter(null);
      initialQuery=savedInstanceState.getCharSequence(STATE_QUERY);
    }

    setHasOptionsMenu(true);
  }



  @Override
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);

    if (!sv.isIconified()) {
      state.putCharSequence(STATE_QUERY, sv.getQuery());
    }
    //state.putSerializable(STATE_MODEL, mSearchItems);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    configureSearchView(menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (event == null || event.getAction() == KeyEvent.ACTION_UP) {
      //adapter.add(v.getText().toString());
      v.setText("");

      InputMethodManager imm=
          (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

      imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    return(true);
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    if (TextUtils.isEmpty(newText)) {
      mListView.setVisibility(View.GONE);
      searchText = newText;
        mSharedPreferences.edit().putString(SearchFragment.SEARCH_TEXT,newText).apply();
    }
    else {
      mListView.setVisibility(View.VISIBLE);
      for(SearchItem searchItem : mSearchItems){
        searchItem.setSearchText("\""+newText+"\"");
        searchText = newText;
          mSharedPreferences.edit().putString(SearchFragment.SEARCH_TEXT,newText).apply();
      }
    }

    adapter.notifyDataSetChanged();

    return(true);
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return(false);
  }

  @Override
  public boolean onClose() {
    adapter.getFilter().filter("");

    return(true);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Toast.makeText(getActivity(),adapter.getItem(position).toString(),Toast.LENGTH_LONG).show();
    Intent searchResultActivity =  new Intent(getActivity(),SearchResultActivity.class);
    searchResultActivity.putExtra(SearchFragment.SEARCH_TEXT,searchText);
    startActivity(searchResultActivity);

  }

  private void configureSearchView(Menu menu) {
    MenuItem search=menu.findItem(R.id.search);

   // sv = (SearchView)MenuItemCompat.getActionView(search);
    sv = (SearchView)search.getActionView();
    sv.setQueryHint("Enter text for search...");
    sv.setOnQueryTextListener(this);
    sv.setOnCloseListener(this);
    sv.setSubmitButtonEnabled(false);
    sv.setIconifiedByDefault(true);

    if (initialQuery != null) {
      sv.setIconified(false);
      search.expandActionView();
      sv.setQuery(initialQuery, true);
    }
  }

  private void initAdapter(ArrayList<SearchItem> startingPoint) {
    if (startingPoint == null) {
      mSearchItems = new ArrayList<SearchItem>();

      for (int i=0; i<searchName.length;i++) {
        SearchItem searchItem =  new SearchItem();
        searchItem.setName(searchName[i]);
        searchItem.setImageName(new Integer(searchImage[i]).toString());
        searchItem.setSearchItems(searchItems[i]);
        searchItem.setSearchText("");

        mSearchItems.add(searchItem);
      }
    }
    else {
      mSearchItems = startingPoint;
    }

    adapter = new SearchHelpAdapter(getActivity(), mSearchItems);

    setListAdapter(adapter);
  }

}
