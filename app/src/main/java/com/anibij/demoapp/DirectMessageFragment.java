package com.anibij.demoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.anibij.demoapp.model.DirectMessageListViewAdapter;
import com.anibij.demoapp.view.AlertDialogManager;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class DirectMessageFragment extends Fragment {

    private static final String consumerKey = "o7kn8lHPoThttJhOejus6r1wJ";
    private static final String consumerSecret = "EfL1dRYw0xw6lWYogM4A7kuwCSwl2eeCINA746qTT28SSJsJnb";
    private static final String TAG = DirectMessageFragment.class.getSimpleName();

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
    SwipeRefreshLayout refreshLayout;
    ConnectionDetector cd;
    Context mContext;
    TextView noRecordView;
    LinearLayout mBottomLinearLayout;
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

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, List<DirectMessage>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Retrieving Mentions...");
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
                    long senderId = dm.getSenderId();
                    String senderScreenName = dm.getSenderScreenName();
                    String textMessage = dm.getText();

                    DirectMessage newDirectMessage = new DirectMessage(createdAt, id, receipientName, recipientId,
                            receipientScreenName, senderName, senderId, senderScreenName, textMessage, recipientImageUrl);

                    Log.d(TAG, "Direct message \n " + newDirectMessage.toString());

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
            // Locate the listview in listview_main.xml

            if (result == null || result.size() <= 0) {
                return;
            }
            listview = (ListView) view.findViewById(R.id.messageListView);
            noRecordView.setVisibility(View.GONE);
            // Pass the results into ListViewAdapter.java
            adapter = new DirectMessageListViewAdapter(result, mContext);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
}
