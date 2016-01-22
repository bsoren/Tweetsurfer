package com.anibij.demoapp;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anibij.demoapp.model.DbHelper;
import com.anibij.demoapp.model.StatusContract;
import com.squareup.picasso.Picasso;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


public class DetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = DetailsFragment.class.getSimpleName(); // 1
    private TextView screenNameView, textUser, textMessage, textCreatedAt, retweetedByView, retweetCountView, favCountView;
    private ImageView mImageView, mMediaImageView,replyViewButton,reTweetViewButton,likeButtonView;
    ProgressDialog pDialog;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_row, null, false); // 2

        mSharedPreferences = getActivity().getSharedPreferences(PREF_NAME,0);

        textUser = (TextView) view.findViewById(R.id.list_item_text_user);
        textMessage = (TextView) view.findViewById(R.id.list_item_text_message);
        textCreatedAt = (TextView) view.findViewById(R.id.list_item_text_created_at);
        retweetedByView = (TextView) view.findViewById(R.id.retweetBy);
        retweetCountView = (TextView) view.findViewById(R.id.reTweetCount);
        favCountView = (TextView) view.findViewById(R.id.favCount);
        screenNameView = (TextView) view.findViewById(R.id.list_item_text_screen_name);

        replyViewButton = (ImageView) view.findViewById(R.id.replyButton);
        reTweetViewButton = (ImageView) view.findViewById(R.id.re_tweetButton);
        likeButtonView = (ImageView) view.findViewById(R.id.likeButton);

        mImageView = (ImageView) view.findViewById(R.id.list_item_profile_image);
        mMediaImageView = (ImageView) view.findViewById(R.id.list_item_media_image);

        setClickListerner();

        DbHelper dbHelper = new DbHelper(getActivity());
        db = dbHelper.getWritableDatabase();

        return view;
    }

    public void setClickListerner(){

        replyViewButton.setOnClickListener(this);
        reTweetViewButton.setOnClickListener(this);
        likeButtonView.setOnClickListener(this);

    }



    @Override
    public void onResume() {
        super.onResume();
        long id = getActivity().getIntent().getLongExtra(
                StatusContract.Column.ID, -1); // 3

        Log.d(TAG, "Id is : " + id);
        updateView(id);
    }

    public void updateView(long id) { // 4
        if (id == -1) {
            textUser.setText("");
            textMessage.setText("");
            textCreatedAt.setText("");
            retweetedByView.setText("");
            return;
        }

        Uri uri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, id);

        Cursor cursor = getActivity().getContentResolver().query(uri, null,
                null, null, null);
        if (!cursor.moveToFirst())
            return;

        String user = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.USER));
        String message = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.MESSAGE));
        long createdAt = cursor.getLong(cursor
                .getColumnIndex(StatusContract.Column.CREATED_AT));
        String profileImage = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.PROFILE_IMAGE));

        String mediaImage = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.MEDIA_IMAGE));

        String retweetedBy = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.RETWEET_BY));

        int retweetCount = cursor.getInt(cursor
                .getColumnIndex(StatusContract.Column.RETWEET_COUNT));

        int favCount = cursor.getInt(cursor
                .getColumnIndex(StatusContract.Column.FAV_COUNT));

        String screenName = cursor.getString(cursor
                .getColumnIndex(StatusContract.Column.SCREEN_NAME));


        textUser.setText(user);
        textMessage.setText(message);
        textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt));
        retweetedByView.setText(retweetedBy);
        retweetCountView.setText(String.valueOf(retweetCount));
        favCountView.setText(String.valueOf(favCount));
        screenNameView.setText("@" + screenName);


        Picasso.with(getActivity())
                .load(profileImage)
                .placeholder(R.drawable.image_loading_animation)
                .error(R.drawable.no_image)
                .into(mImageView);

        if (!mediaImage.equals("NO_IMAGE")) {
            Picasso.with(getActivity())
                    .load(mediaImage)
                    .placeholder(R.drawable.image_loading_animation)
                    .error(R.drawable.no_image)
                    .into(mMediaImageView);
            mMediaImageView.setVisibility(View.VISIBLE);
        } else {
            mMediaImageView.setVisibility(View.GONE);
        }

        new RetrieveTweet().execute(String.valueOf(id));

    }

    @Override
    public void onClick(View v) {

        int buttonId =  v.getId();

        switch (buttonId){
            case R.id.replyButton :
                Log.d(TAG,"Reply button Clicked");
                Toast.makeText(getActivity(),"Reply Clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.re_tweetButton :
                Toast.makeText(getActivity(),"Retweet Clicked",Toast.LENGTH_SHORT).show();
                break;
            case R.id.likeButton:
                Toast.makeText(getActivity(),"like clicked",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }

    class RetrieveTweet extends AsyncTask<String, String,twitter4j.Status> {

        private long idToUpdate =  -1 ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Retreiving twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected twitter4j.Status doInBackground(String... args) {

            String id = args[0];
            idToUpdate = Long.valueOf(id);
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

                twitter4j.Status status = twitter.showStatus(Long.valueOf(id));

                return status;


            } catch (TwitterException e) {
                Log.d("Failed to retrieve",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(twitter4j.Status status) {


            if (status == null) {
                /* Dismiss the progress dialog after sharing */
                pDialog.dismiss();
                return;
            }


            // Toast.makeText(getActivity(), "Tweet Retrieved!", Toast.LENGTH_SHORT).show();

            User user = status.getUser();
            String retweetByUser = user.getName(); // user who did the retweet
            boolean isRetweet = status.isRetweet();
            long retweetCreatedAt = status.getCreatedAt().getTime();
            long retweetId = status.getId();

            if(isRetweet){
                status = status.getRetweetedStatus();
            }

            user = status.getUser();
            int favCount = status.getFavoriteCount();
            int reTweetCount = status.getRetweetCount();
            boolean isFavourite = status.isFavorited();
            retweetCountView.setText(String.valueOf(reTweetCount));
            favCountView.setText(String.valueOf(favCount));

            pDialog.dismiss();

            ContentValues values = new ContentValues();

            values.clear();
            values.put(StatusContract.Column.RETWEET_COUNT, reTweetCount);
            values.put(StatusContract.Column.FAV_COUNT,favCount);
            values.put(StatusContract.Column.IS_FAVOURITE, (isFavourite) ? 1 : 0);

            Uri updateUri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, idToUpdate);

            long updateStatus = getActivity().getContentResolver().update(updateUri, values, null, null);
            Log.d(TAG,"Updated retweet and favcount : "+updateStatus);


        }

    }
}
