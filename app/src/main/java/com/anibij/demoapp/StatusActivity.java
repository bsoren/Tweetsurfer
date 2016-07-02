package com.anibij.demoapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


public class StatusActivity extends Activity {

    ProgressDialog pDialog;

    private final static String TAG = "RefreshService";
    private static SharedPreferences mSharedPreferences;

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    private static final String consumerKey = "o7kn8lHPoThttJhOejus6r1wJ";
    private static final String consumerSecret = "EfL1dRYw0xw6lWYogM4A7kuwCSwl2eeCINA746qTT28SSJsJnb";



    private EditText mShareEditText;
    private Button mShareButton;
    private TextView mUserName;
    String tweetIdString="";
    String userScreenName="";
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mSharedPreferences = getSharedPreferences(PREF_NAME,0);
        String username = mSharedPreferences.getString(PREF_USER_NAME,"None");

        mShareEditText = (EditText) findViewById(R.id.share_text);

        mUserName = (TextView) findViewById(R.id.user_name);
        mUserName.setText(username);

        mShareButton = (Button) findViewById(R.id.btn_share);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = mShareEditText.getText().toString();
                if(userScreenName.length() > 0){
                    String statusIdString = tweetIdString;
                    new UpdateTwitterStatus().execute(status,statusIdString);
                }else {
                    String statusIdString = tweetIdString;
                    new UpdateTwitterStatus().execute(status,statusIdString);
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // call helper method
        setupTweet();
    }

    private void setupTweet() {

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            tweetIdString =  extras.getString("TWEET_ID","");
            userScreenName = extras.getString("TWEET_USER", "");
            message = extras.getString("TWEET_MESSAGE","");
            if(!message.isEmpty()){
                mShareEditText.setText("http://twitter.com/"+userScreenName+"/status/"+tweetIdString);
                mShareEditText.setSelection(0);
            }else {
                mShareEditText.setText("@" + userScreenName + " ");
                mShareEditText.setSelection(mShareEditText.getText().length());
            }

        }else {
            mShareEditText.setText("");
        }
    }

    class UpdateTwitterStatus extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(StatusActivity.this);
            pDialog.setMessage("Posting to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(String... args) {

            String status = args[0];
            String statusIdString =  args[1];
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

                // Update status
                StatusUpdate statusUpdate = new StatusUpdate(status);
                //InputStream is = getResources().openRawResource(R.drawable.lakeside_view);
                //statusUpdate.setMedia("test.jpg", is);
                twitter4j.Status response;
                if(statusIdString != null && statusIdString.length() > 0) {
                    long statusId = Long.valueOf(statusIdString);
                    response =  twitter.updateStatus(statusUpdate.inReplyToStatusId(statusId));
                }else{
                    response = twitter.updateStatus(statusUpdate);
                }

                Log.d("Status", response.getText());

            } catch (TwitterException e) {
                Log.d("Failed to post!", e.getMessage());
                String errorMessage = e.getErrorMessage();
                Toast.makeText(StatusActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

			/* Dismiss the progress dialog after sharing */
            pDialog.dismiss();

            Toast.makeText(StatusActivity.this, "Posted to Twitter!", Toast.LENGTH_SHORT).show();

            // Clearing EditText field
            mShareEditText.setText("");
        }

    }
}
