package com.anibij.demoapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.anibij.demoapp.Utils.ConnectionDetector;
import com.anibij.demoapp.model.DirectMessage;
import com.anibij.demoapp.model.DirectMessageCursorAdapter;
import com.anibij.demoapp.model.DirectMessageListViewAdapter;
import com.anibij.demoapp.model.StatusContract;
import com.anibij.demoapp.view.AlertDialogManager;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class DirectMessageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String consumerKey = "o7kn8lHPoThttJhOejus6r1wJ";
    private static final String consumerSecret = "EfL1dRYw0xw6lWYogM4A7kuwCSwl2eeCINA746qTT28SSJsJnb";
    private static final String TAG = DirectMessageFragment.class.getSimpleName();
    private static final int LOADER_ID = 77 ;

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
    DirectMessageListViewAdapter adapter;
    private List<DirectMessage> directMessageList = null;
    private List<DirectMessage> sendMessageList = null;
    SwipeRefreshLayout refreshLayout;
    ConnectionDetector cd;
    Context mContext;
    TextView noRecordView;
    LinearLayout mBottomLinearLayout;

    CursorAdapter mCursorAdapter;
    CursorLoader mCursorLoader;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    public DirectMessageFragment() {
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
        view = inflater.inflate(R.layout.fragment_directmessages, container, false);
        listview = (ListView) view.findViewById(R.id.messageListView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe_refresh);
        noRecordView = (TextView) view.findViewById(R.id.empty_view);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshLayout.setRefreshing(false);
                cd = new ConnectionDetector(mContext);
                boolean isInternetAvailable = cd.isConnectingToInternet();

                // Toast.makeText(mContext,"Internet Available? "+isInternetAvailable,Toast.LENGTH_SHORT).show();

                if (!isInternetAvailable) {
                    alert.showAlertDialog(mContext, "Internet Connection Error",
                            "Please connect to working Internet Connection", false);

                    // stop executing code by return
                    return;
                }

               DirectMessageFragment.this.doRefresh();
            }

        });

        mCursorAdapter = new DirectMessageCursorAdapter(getActivity(),null,0);
        listview.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(LOADER_ID,null, this).forceLoad();

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void doRefresh() {
        new RemoteDataTask().execute();
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{
                StatusContract.DirectMessage.Column.ID,
                StatusContract.DirectMessage.Column.CREATED_AT,
                StatusContract.DirectMessage.Column.TEXT_MESSAGE,
                StatusContract.DirectMessage.Column.RECEPIENT_ID,
                StatusContract.DirectMessage.Column.RECEPIENT_NAME,
                StatusContract.DirectMessage.Column.RECEPIENT_SCREEN_NAME,
                StatusContract.DirectMessage.Column.RECEPIENT_IMAGE_URL,
                StatusContract.DirectMessage.Column.SENDER_ID,
                StatusContract.DirectMessage.Column.SENDER_NAME,
                StatusContract.DirectMessage.Column.SENDER_SCREEN_NAME,
                StatusContract.DirectMessage.Column.SENDER_IMAGE_URL
        };


        String selctionArgs = "";

        String orderBy =  StatusContract.DirectMessage.Column.CREATED_AT + " DESC";
        Uri groupBySenderUri = Uri.withAppendedPath(StatusContract.DM_CONTENT_URI,"GROUP_BY_SENDER");
        mCursorLoader = new CursorLoader(getContext(),
                groupBySenderUri, projection, null, null,orderBy);

        return mCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(TAG,"cursor data "+data.getCount());

        if(data != null){
            if(data.moveToFirst()){
                do{
                    long createdAt = data.getLong(data.getColumnIndex(StatusContract.DirectMessage.Column.CREATED_AT));
                    String textMessage = data.getString(data.getColumnIndex(StatusContract.DirectMessage.Column.TEXT_MESSAGE));

                    Log.d(TAG,"created at and txt message "+createdAt + " : " + textMessage );

                }while(data.moveToNext());
            }
        }

        if(data.getCount() > 0){
            noRecordView.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
        }else {
            noRecordView.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
        }
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }




    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, List<DirectMessage>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Retrieving Direct Messages...");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected List<com.anibij.demoapp.model.DirectMessage> doInBackground(Void... params) {
            // Create the array
            directMessageList = new ArrayList<DirectMessage>();
            sendMessageList =  new ArrayList<DirectMessage>();
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

                List<twitter4j.DirectMessage> directMessages = twitter.getDirectMessages();
                List<twitter4j.DirectMessage> sendMessages = twitter.getSentDirectMessages();

                directMessages.addAll(sendMessages);

                if (directMessages != null) {
                    Log.d(TAG, "Direct Message Size :" + directMessages.size());
                } else {
                    Log.d(TAG, "DirectMessage is null");
                }

                for (twitter4j.DirectMessage dm : directMessages) {

                    long createdAt = dm.getCreatedAt().getTime();
                    long id = dm.getId();
                    String receipientName = dm.getRecipient().getName();
                    long recipientId = dm.getRecipientId();
                    String receipientScreenName = dm.getRecipientScreenName();
                    String recipientImageUrl = dm.getRecipient().getProfileImageURL();
                    String senderName = dm.getSender().getName();
                    String senderImageUrl = dm.getSender().getProfileImageURL();
                    long senderId = dm.getSenderId();
                    String senderScreenName = dm.getSenderScreenName();
                    String textMessage = dm.getText();

                    DirectMessage newDirectMessage = new DirectMessage(createdAt, id, receipientName, recipientId,
                            receipientScreenName, senderName, senderId, senderScreenName, textMessage, recipientImageUrl,senderImageUrl);

                    Log.d(TAG, "Direct message \n " + newDirectMessage.toString());

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(StatusContract.DirectMessage.Column.ID,id);
                    contentValues.put(StatusContract.DirectMessage.Column.CREATED_AT,createdAt);
                    contentValues.put(StatusContract.DirectMessage.Column.TEXT_MESSAGE,textMessage);

                    contentValues.put(StatusContract.DirectMessage.Column.RECEPIENT_ID,recipientId);
                    contentValues.put(StatusContract.DirectMessage.Column.RECEPIENT_NAME,receipientName);
                    contentValues.put(StatusContract.DirectMessage.Column.RECEPIENT_SCREEN_NAME,receipientScreenName);
                    contentValues.put(StatusContract.DirectMessage.Column.RECEPIENT_IMAGE_URL,recipientImageUrl);

                    contentValues.put(StatusContract.DirectMessage.Column.SENDER_ID,senderId);
                    contentValues.put(StatusContract.DirectMessage.Column.SENDER_NAME,senderName);
                    contentValues.put(StatusContract.DirectMessage.Column.SENDER_SCREEN_NAME,senderScreenName);
                    contentValues.put(StatusContract.DirectMessage.Column.SENDER_IMAGE_URL,senderImageUrl);

                    getContext().getContentResolver().insert(StatusContract.DM_CONTENT_URI, contentValues);

                    directMessageList.add(newDirectMessage);
                }

                return directMessageList;

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(List<com.anibij.demoapp.model.DirectMessage> result) {
            mProgressDialog.dismiss();
            getLoaderManager().initLoader(LOADER_ID,null, DirectMessageFragment.this).forceLoad();
            mCursorAdapter.notifyDataSetChanged();
        }
    }
}
