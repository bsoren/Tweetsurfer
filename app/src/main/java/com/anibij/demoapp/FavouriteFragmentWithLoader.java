package com.anibij.demoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.anibij.demoapp.Utils.ConnectionDetector;
import com.anibij.demoapp.model.DbHelper;
import com.anibij.demoapp.model.FavoriteCursorLoader;
import com.anibij.demoapp.model.MentionListViewAdapter;
import com.anibij.demoapp.model.Status;
import com.anibij.demoapp.model.StatusContract;
import com.anibij.demoapp.view.AlertDialogManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


public class FavouriteFragmentWithLoader extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String consumerKey = "o7kn8lHPoThttJhOejus6r1wJ";
    private static final String consumerSecret = "EfL1dRYw0xw6lWYogM4A7kuwCSwl2eeCINA746qTT28SSJsJnb";
    private static final int LOADER_ID = 55;
    private static final String TAG = FavouriteFragmentWithLoader.class.getSimpleName() ;

    private static SharedPreferences mSharedPreferences;
    SQLiteDatabase db;

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";


    // Declare Variables
    ListView listview;
    View view;
    ProgressDialog mProgressDialog;
    MentionListViewAdapter adapter;
    private List<Status> statusList = null;
    SwipeRefreshLayout refreshLayout;
    ConnectionDetector cd;
    Context mContext;
    LinearLayout mBottomLinearLayout;
    TextView mNoRecordView;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    SimpleCursorAdapter mSimpleCursorAdapter;
    FavoriteCursorLoader mFavoriteCursorLoader;
    DbHelper dbHelper;

    String[] columns = {
            StatusContract.Column.USER,
            StatusContract.Column.MESSAGE,
            StatusContract.Column.CREATED_AT,
            StatusContract.Column.PROFILE_IMAGE,
            StatusContract.Column.SCREEN_NAME,
            StatusContract.Column.MEDIA_IMAGE
    };

    int[] to = {
            R.id.list_item_text_user,
            R.id.list_item_text_message,
            R.id.list_item_text_created_at,
            R.id.profile_image,
            R.id.user_screen_name,
            R.id.list_item_media_image
    };


    public FavouriteFragmentWithLoader() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSharedPreferences = getActivity().getSharedPreferences(PREF_NAME, 0);
        mContext = getActivity();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mentions, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe_refresh);
        mNoRecordView = (TextView) view.findViewById(R.id.empty_view);
        listview = (ListView) view.findViewById(R.id.mentionListView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshLayout.setRefreshing(false);

            }

        });

        mSimpleCursorAdapter = new SimpleCursorAdapter(mContext, R.layout.list_row_mentions, null, columns, to);
        mSimpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                switch(view.getId()){
                    case R.id.list_item_text_created_at:
                        String createdDateString =
                                DateUtils.getRelativeTimeSpanString(
                                        cursor.getLong(cursor.getColumnIndex
                                                (StatusContract.Column.CREATED_AT))).toString();
                        ((TextView)view).setText(createdDateString);
                        return true;

                    case R.id.list_item_media_image:
                        String mediaImageUrl =  cursor.getString(cursor.getColumnIndex(StatusContract.Column.MEDIA_IMAGE));
                        Log.d(TAG,"MediaURL : "+mediaImageUrl);

                        if (!mediaImageUrl.equals("NO_IMAGE")) {

                            Log.d(TAG,"Loading Image : ");
                            ((ImageView)view).setVisibility(View.VISIBLE);
                            Picasso.with(mContext).load(mediaImageUrl)
                                    .error(R.drawable.no_image)
                                    .placeholder(R.drawable.image_loading_animation).into(((ImageView)view));

                        } else {
                            ((ImageView)view).setVisibility(View.GONE);
                            Log.d(TAG, " Image Gone : ");
                            ((ImageView)view).setImageResource(R.drawable.no_image);
                        }

                        return true;
                }
                return false;
            }
        });
        listview.setAdapter(mSimpleCursorAdapter);

        //Initializing loader
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void doRefresh() {

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        dbHelper = new DbHelper(mContext);
        mFavoriteCursorLoader = new FavoriteCursorLoader(mContext, dbHelper);
        return mFavoriteCursorLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mSimpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mSimpleCursorAdapter.swapCursor(null);
    }


    private class RemoteDataTask extends AsyncTask<Void, Void, List<Status>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Retrieving Favorites...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected List<com.anibij.demoapp.model.Status> doInBackground(Void... params) {
            // Create the array
            statusList = new ArrayList<com.anibij.demoapp.model.Status>();
            try {

                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(consumerKey);
                builder.setOAuthConsumerSecret(consumerSecret);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                List<twitter4j.Status> statuses = twitter.getFavorites();

                for (twitter4j.Status returnStatus : statuses) {

                    String id = String.valueOf(returnStatus.getId());
                    String userName = returnStatus.getUser().getName();
                    String screenName = returnStatus.getUser().getScreenName();
                    String profileImage = returnStatus.getUser().getProfileImageURL();
                    long createdAt = returnStatus.getCreatedAt().getTime();
                    String textMessage = returnStatus.getText();
                    int retweetCount = returnStatus.getRetweetCount();
                    int favCount = returnStatus.getFavoriteCount();

                    com.anibij.demoapp.model.Status newStatus =
                            new com.anibij.demoapp.model.Status(id, userName, textMessage, createdAt, profileImage, null, null, retweetCount, favCount, screenName);

                    statusList.add(newStatus);
                }
                return statusList;

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<com.anibij.demoapp.model.Status> result) {

            if (result == null || result.size() <= 0) {

                return;
            }
            // Locate the listview in listview_main.xml
            listview = (ListView) view.findViewById(R.id.mentionListView);
            mNoRecordView.setVisibility(View.GONE);
            // Pass the results into ListViewAdapter.java
            adapter = new MentionListViewAdapter(getActivity(), result);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
}
