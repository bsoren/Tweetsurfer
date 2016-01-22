package com.anibij.demoapp.service;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by bsoren on 19-Jan-16.
 */
public class FavoriteToggleTask extends AsyncTask<String, Void, twitter4j.Status> {


    private static final String consumerKey = "o7kn8lHPoThttJhOejus6r1wJ";
    private static final String consumerSecret = "EfL1dRYw0xw6lWYogM4A7kuwCSwl2eeCINA746qTT28SSJsJnb";

    private static SharedPreferences mSharedPreferences;
    SQLiteDatabase db;

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";

    private Handler uiHandler;

    public FavoriteToggleTask(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    @Override
    protected twitter4j.Status doInBackground(String... params) {

        try {

            long statusId = Long.valueOf(params[0]);
            String toggle = params[1];
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);

            // Access Token
            String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
            // Access Token Secret
            String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

            AccessToken accessToken = new AccessToken(access_token, access_token_secret);
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

            twitter4j.Status status;

            if (toggle.equals("favorite")) {
                status = twitter.createFavorite(statusId);
            } else {
                status = twitter.destroyFavorite(statusId);
            }

            return status;

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPostExecute(twitter4j.Status status) {
        super.onPostExecute(status);
    }
}
