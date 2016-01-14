package com.anibij.demoapp.service;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.anibij.demoapp.model.DbHelper;
import com.anibij.demoapp.model.StatusContract;

import org.json.JSONArray;
import org.json.JSONObject;

import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;


public class RefreshService extends IntentService {

    private static final int NO_OF_TWEETS = 40;
    private ResultReceiver mResultReceiver;
    int count = 0;

    String maxId;
    long sinceId;

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


    public RefreshService() {
        super(TAG);

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (intent != null) {
            if (intent.getExtras() != null) {
                maxId = intent.getStringExtra("MAX_ID");
                sinceId = intent.getLongExtra("SINCE_ID", 1000L);

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize application preferences */
        mSharedPreferences = getSharedPreferences(PREF_NAME, 0);
        Log.d(TAG, "onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "onStarted");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        SQLiteDatabase db = null;
        count = 0;


        try {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);
            builder.setJSONStoreEnabled(true);
            //builder.setUserStreamWithFollowingsEnabled(false);
            //builder.setIncludeEntitiesEnabled(true);
//            builder.setIncludeMyRetweetEnabled(false);
            //builder.setUserStreamRepliesAllEnabled(false);


            // Access Token
            String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
            // Access Token Secret
            String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

            AccessToken accessToken = new AccessToken(access_token, access_token_secret);
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);


            DbHelper dbHelper = new DbHelper(this);
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            ResponseList<Status> statuses = null;


            if (maxId != null) {
                Log.d(TAG, "Max Id is not null : " + String.valueOf(maxId));
                long maxIdd = Long.valueOf(maxId);
                Paging page1 = new Paging();

                //page1.setPage(1);
                page1.setCount(NO_OF_TWEETS);
                page1.setMaxId(maxIdd);
                statuses = twitter.getHomeTimeline(page1);

            } else {

                Paging page2 = new Paging();
                //page1.setPage(1);
                page2.setCount(NO_OF_TWEETS);
                page2.setSinceId(sinceId);
                Log.d(TAG, "MinId is null : and sinceId is " + sinceId);
                statuses = twitter.getHomeTimeline(page2);
            }

            RateLimitStatus rateLimitStatus = statuses.getRateLimitStatus();
            int remaining = rateLimitStatus.getRemaining();
            int restTimeInSeconds = rateLimitStatus.getResetTimeInSeconds();

            Log.d(TAG, "Remaining: restTimeSec : " + remaining + "  :: " + restTimeInSeconds);

            Log.d(TAG, "No. of new Statues : " + statuses.size());

            try {
                String strInitialDataSet = DataObjectFactory.getRawJSON(statuses);
                Log.d(TAG, " TWEEEETS *******************************");
                Log.d(TAG, strInitialDataSet);
                Log.d(TAG, " TWEEEETS *******************************");
                JSONArray JATweets = new JSONArray(strInitialDataSet);
                Log.d(TAG, "Array SIZE : " + JATweets.length());

                for (int i = 0; i < JATweets.length(); i++) {
                    JSONObject JOTweets = JATweets.getJSONObject(i);
                    Log.e("TWEETS", JOTweets.toString());
                }

            } catch (Exception e) {
                // TODO: handle exception
            }

            Uri uri;
            long lastId = 0;
            int replyToCount = 0;
            int retweetCount = 0;

            for (twitter4j.Status status : statuses) {

                Status reTweetStatus = null;

                User user = status.getUser();
                String retweetByUser = user.getName(); // user who did the retweet
                boolean isRetweet = status.isRetweet();
                long createdAt = status.getCreatedAt().getTime();
                long id = status.getId();
                Log.d(TAG, String.format("Normal Id and Created At : %s , %s", id, createdAt));
                long replyToId = status.getInReplyToStatusId();
                long replyToUserId = status.getInReplyToUserId();
                String inReplyToScreenName = status.getInReplyToScreenName();

                if (replyToId != -1 || replyToUserId != -1 || inReplyToScreenName != null) {
                    replyToCount++;
                }
                if (isRetweet) {
                    retweetCount++;
                    status = status.getRetweetedStatus();
                    Log.d(TAG, String.format("Retweet Id and RetweetCreated At : %s , %s", status.getId(), status.getCreatedAt().getTime()));
                    Log.d(TAG, "Is Retweet : and retweet count :  " + true + " :: " + retweetCount);

                }
                user = status.getUser();
                int favCount = status.getFavoriteCount();
                int reTweetCount = status.getRetweetCount();


                final MediaEntity[] mediaEntities = status.getMediaEntities();
                StringBuilder sMediaUrls = new StringBuilder();
                //Log.d(TAG," Media Urls : ");

//                Log.d(TAG,
//                        String.format("retweet by%s \n" +
//                                      "tweet user screen name %s : \n" +
//                                        "tweet user name %s : \n" +
//                                        "Tweet : %s \n"+
//                                        "id : %s",
//                                retUser,status.getUser().getScreenName(),user.getName(),
//                                status.getText(), id));


                for (MediaEntity me : mediaEntities) {

                    // Log.d(TAG, " url : " + me.getMediaURL() + " type : " + me.getType());

                }

                // Log.d(TAG, "***********************");


                values.clear();
                if(isRetweet){
                    values.put(StatusContract.Column.RETWEET_BY,retweetByUser);
                }
                values.put(StatusContract.Column.RETWEET_COUNT,reTweetCount);
                values.put(StatusContract.Column.FAV_COUNT,favCount);
                values.put(StatusContract.Column.ID, id);
                values.put(StatusContract.Column.USER, user.getName());
                values.put(StatusContract.Column.MESSAGE, status.getText());
                values.put(StatusContract.Column.CREATED_AT, createdAt);
                values.put(StatusContract.Column.PROFILE_IMAGE, user.getProfileImageURL());
                values.put(StatusContract.Column.MORE_ITEMS, 0);

                if (mediaEntities != null && mediaEntities.length > 0 && mediaEntities[0].getType().equals("photo")) {
                    values.put(StatusContract.Column.MEDIA_IMAGE, mediaEntities[0].getMediaURL());
                } else {
                    values.put(StatusContract.Column.MEDIA_IMAGE, "NO_IMAGE");
                }


                uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);

                if (uri != null) {

                    count++;
                    Log.d(TAG,
                            String.format("Inserted New Records : %s : %s :%s ", count, status.getUser().getScreenName(),
                                    status.getText()));
                    lastId = Long.valueOf(uri.getLastPathSegment());
                }

                if (count >= NO_OF_TWEETS/2) {
                    values.put(StatusContract.Column.MORE_ITEMS, 1);
                    long idd = values.getAsLong(StatusContract.Column.ID);
                    Uri itemUri = ContentUris.withAppendedId(StatusContract.CONTENT_URI,idd);

                    if (itemUri != null) {
                        int updatedId = getContentResolver().update(itemUri, values, null, null);
                        Log.d(TAG, "Load more Item Record inserted : " + updatedId);
                    } else {
                        Log.d(TAG, "Load more Item Not inserted");
                    }

                    break;
                }


            }

//            if (statuses.size() >= NO_OF_TWEETS / 2) {
//
//                values.put(StatusContract.Column.MORE_ITEMS, 1);
//                long idd = values.getAsLong(StatusContract.Column.ID);
//                Uri itemUri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, idd);
//
//                int updatedId = getContentResolver().update(itemUri, values, null, null);
//
//                Log.d(TAG, "Load more Item Record inserted : " + updatedId);
//                //uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
//
//            }


//            if(count > 1 ) {
//                values.put(StatusContract.Column.MORE_ITEMS, 1);
//                Uri itemUri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, lastId);
//
//                if (itemUri != null) {
//                    int updatedId = getContentResolver().update(itemUri, values, null, null);
//                    Log.d(TAG, "Load more Item Record inserted : " + updatedId);
//                } else {
//                    Log.d(TAG, "Load more Item Not inserted");
//                }
//            }

            //if(count > 0){
            Intent newIntent = new Intent(StatusContract.NEW_ITEMS);
            newIntent.putExtra("count", count);
            newIntent.putExtra("datasize", statuses.size());
            sendBroadcast(newIntent);
            Log.d(TAG, "BroadCast sent");


            //}

            Log.d(TAG, "Tweet Count :" + count);
            Log.d(TAG, "Retweets :" + retweetCount);
            Log.d(TAG, "Reply To Counte :" + replyToCount);


        } catch (TwitterException e) {
            Log.d("Failed to post!", e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }

//        if(mResultReceiver != null) {
//            Bundle bundleData = new Bundle();
//            bundleData.putString("Hello", "Service Hello");
//			bundleData.putInt("count",count);
//            mResultReceiver.send(44, bundleData);
//        }

        return;
    }

}
