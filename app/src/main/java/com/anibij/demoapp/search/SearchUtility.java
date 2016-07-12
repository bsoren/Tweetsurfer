package com.anibij.demoapp.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.anibij.demoapp.Utils.AppPrefrences;
import com.anibij.demoapp.model.Status;

import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by bsoren on 03-Jul-16.
 */
public class SearchUtility {

    private static final String TAG = SearchUtility.class.getSimpleName();
    private static final int MAX_SEARCH_RESULT = 20;
    private Context mContext;
    private String searchText;
    private List<Status> mStatues = null;
    private long maxId;

    /* Shared preference keys */
    private static final String PREF_NAME = "sample_twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_USER_NAME = "twitter_user_name";


    private static final String consumerKey = "o7kn8lHPoThttJhOejus6r1wJ";
    private static final String consumerSecret = "EfL1dRYw0xw6lWYogM4A7kuwCSwl2eeCINA746qTT28SSJsJnb";

    private static SharedPreferences mSharedPreferences;

    private String prefSearchText = "";

    private Twitter mTwitter;

    public SearchUtility(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(AppPrefrences.PREF_NAME, 0);
        mTwitter = getTwitterInstance();
        prefSearchText = mSharedPreferences.getString(SearchFragment.SEARCH_TEXT, "");
    }

    private Twitter getTwitterInstance() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);
        builder.setJSONStoreEnabled(true);
        builder.setIncludeMyRetweetEnabled(true);
        // Access Token
        String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
        // Access Token Secret
        String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

        AccessToken accessToken = new AccessToken(access_token, access_token_secret);
        Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

        return twitter;
    }

    /**
     * Method to search tweets
     */
    public List<Status> fetchTwitterSearchTweets(long maxId) {
        this.maxId = maxId;
        try {

            Log.d(TAG, "searchText is and maxId:" + prefSearchText + " : " + maxId);

            Thread.sleep(1000);

            Query query = new Query(prefSearchText);
            query.setCount(MAX_SEARCH_RESULT);
            query.setResultType(Query.ResultType.recent);

            if (maxId != Long.MAX_VALUE) {
                query.setMaxId(maxId);
            }

            query.setLang("en");

            QueryResult result = null;

            result = mTwitter.search(query);
            Log.d(TAG, "Search Result Size : " + result.getTweets().size());
            mStatues = processStatus(result.getTweets());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mStatues;
    }


    /**
     * Method to convert twitter4j tweet status to application status
     *
     * @param tweets
     * @return
     */
    private List<com.anibij.demoapp.model.Status> processStatus(List<twitter4j.Status> tweets) {
        List<com.anibij.demoapp.model.Status> statusList = new ArrayList<>();

        for (int i = 0; i < tweets.size(); i++) {

            if (maxId != Long.MAX_VALUE && i == 0) {
                continue;
            }

            twitter4j.Status status = tweets.get(i);
            com.anibij.demoapp.model.Status newStatus = new com.anibij.demoapp.model.Status("default");
            newStatus.setId(new Long(status.getId()).toString());
            newStatus.setCreatedAt(status.getCreatedAt().getTime());
           // newStatus.setRetweetBy(status.getUser().getName());

            boolean isRetweet = status.isRetweet();

            if (isRetweet) {
                newStatus.setRetweetBy(status.getUser().getName());
                status = status.getRetweetedStatus();

            }
            newStatus.setProfileImageUrl(status.getUser().getProfileImageURL());
            newStatus.setUser(status.getUser().getName());
            newStatus.setMessage(status.getText());
            newStatus.setRetweetCount(status.getRetweetCount());
            newStatus.setFavCount(status.getFavoriteCount());
            newStatus.setScreenName(status.getUser().getScreenName());

            MediaEntity[] mediaEntities = status.getMediaEntities();
            if (mediaEntities != null && mediaEntities.length > 0 && mediaEntities[0].getType().equals("photo")) {
                newStatus.setMediaImageUrl(mediaEntities[0].getMediaURL());
            } else {
                newStatus.setMediaImageUrl("NO_IMAGE");
            }

            statusList.add(newStatus);

        }

        return statusList;
    }


    public List<com.anibij.demoapp.model.User> fetchTwitterSearchUsers(int pageCount) {
        List<com.anibij.demoapp.model.User> retUserList = new ArrayList<>();
        try {
            Log.d(TAG, "Searching User " + prefSearchText);
            ResponseList<User> users = mTwitter.searchUsers(prefSearchText, pageCount);
            retUserList = processRetrievedUsers(users);

        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return retUserList;
    }

    private List<com.anibij.demoapp.model.User> processRetrievedUsers(ResponseList<User> users) {
        List<com.anibij.demoapp.model.User> retUserList = new ArrayList<>();
        Log.d(TAG, "User Size : " + users.size());

        for (User user : users) {
            com.anibij.demoapp.model.User myUser = new com.anibij.demoapp.model.User();
            myUser.setId(user.getId());
            myUser.setName(user.getName());
            myUser.setScreenName(user.getScreenName());
            myUser.setLatestStatus(user.getStatus() != null ? user.getStatus().getText() : "");
            myUser.setProfileImageUrl(user.getProfileImageURL());

            retUserList.add(myUser);
        }

        return retUserList;
    }


}
