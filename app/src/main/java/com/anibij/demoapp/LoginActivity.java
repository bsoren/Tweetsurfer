package com.anibij.demoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anibij.demoapp.Utils.AppPrefrences;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class LoginActivity extends Activity implements OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
	
	/* Shared preference keys */
//	private static final String PREF_NAME = "sample_twitter_pref";
//	private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
//	private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
//	private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
//	private static final String PREF_USER_NAME = "twitter_user_name";

	/* Any number for uniquely distinguish your request */
	public static final int WEBVIEW_REQUEST_CODE = 100;

	private ProgressDialog pDialog;

	private static Twitter twitter;
	private static RequestToken requestToken;
	
	private static SharedPreferences mSharedPreferences;

	private EditText mShareEditText;
	private TextView userName;
	private View loginLayout;
	private View shareLayout;

	private String consumerKey = null;
	private String consumerSecret = null;
	private String callbackUrl = null;
	private String oAuthVerifier = null;

	private Button getTimeline;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* initializing twitter parameters from string.xml */
		initTwitterConfigs();

		/* Enabling strict mode */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		/* Setting activity layout file */
		setContentView(R.layout.login_layout);

		/* register button click listeners */
		findViewById(R.id.btn_login).setOnClickListener(this);


		/* Check if required twitter keys are set */
		if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
			Toast.makeText(this, "Twitter key and secret not configured",
					Toast.LENGTH_SHORT).show();
			return;
		}
		/* Initialize application preferences */
		mSharedPreferences =  getSharedPreferences(AppPrefrences.PREF_NAME,0);

		/*  if already logged in, then hide login layout and show share layout */
		boolean isLoggedIn = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
		if (isLoggedIn) {

			String username = mSharedPreferences.getString(AppPrefrences.PREF_USER_NAME, "");

		} else {

			Uri uri = getIntent().getData();
			
			if (uri != null && uri.toString().startsWith(callbackUrl)) {
			
				String verifier = uri.getQueryParameter(oAuthVerifier);

				try {
					
					/* Getting oAuth authentication token */
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					/* Getting user id form access token */
					long userID = accessToken.getUserId();
					final User user = twitter.showUser(userID);
					final String username = user.getName();

					/* save updated token */
					saveTwitterInfo(accessToken);

				} catch (Exception e) {
					Log.e("Failed login Twitter!!", e.getMessage());
				}
			}

		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu_login,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Log.d(TAG, "Settings Clicked on Twitter-Sharing");
                //startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.refresh:
                //startActivity(new Intent(this,SettingsActivity.class));
                return true;
            case R.id.sign_out:
                // remove twitter login preferences.
                boolean pktL = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN,false);
                Log.d(TAG,"Boolean "+pktL);
                if(mSharedPreferences.contains(AppPrefrences.PREF_KEY_TWITTER_LOGIN)){
                    Editor edit = mSharedPreferences.edit();
                    edit.putBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
					edit.putLong(AppPrefrences.PREF_SINCE_ID,1000L);
                    edit.commit();
                    Log.d(TAG, "Removed pref_key_twitter_login");
                    pktL = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN,false);
                    Log.d(TAG, "Boolean " + pktL);

                    loginToTwitter();
                }

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
	 * Saving user information, after user is authenticated for the first time.
	 * You don't need to show user to login, until user has a valid access toen
	 */
	private void saveTwitterInfo(AccessToken accessToken) {
		
		long userID = accessToken.getUserId();
		
		User user;
		try {
			user = twitter.showUser(userID);
		
			String username = user.getName();
			String userScreenName = user.getScreenName();
			String userProfileImage =  user.getProfileImageURL();

			/* Storing oAuth tokens to shared preferences */
			Editor e = mSharedPreferences.edit();
			e.putString(AppPrefrences.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
			e.putString(AppPrefrences.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
			e.putBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, true);
			e.putString(AppPrefrences.PREF_USER_NAME, username);
			e.putString(AppPrefrences.PREF_USER_SCREEN_NAME,userScreenName);
			e.putString(AppPrefrences.PREF_USER_PROFILE_IMAGE_URL,userProfileImage);
			e.putLong(AppPrefrences.PREF_SINCE_ID,1000L);
			e.commit();

		} catch (TwitterException e1) {
			e1.printStackTrace();
		}
	}

	/* Reading twitter essential configuration parameters from strings.xml */
	private void initTwitterConfigs() {
		consumerKey = getString(R.string.twitter_consumer_key);
		consumerSecret = getString(R.string.twitter_consumer_secret);
		callbackUrl = getString(R.string.twitter_callback);
		oAuthVerifier = getString(R.string.twitter_oauth_verifier);
	}

	public void loginToTwitter() {
		boolean isLoggedIn = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
        Log.d(TAG,"Running loginToTwitter Again : "+isLoggedIn);
		
		if (!isLoggedIn) {
            Log.d(TAG,"Running loginToTwitter Again, Inside !isLoggedIn");
			final ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(consumerKey);
			builder.setOAuthConsumerSecret(consumerSecret);

			final Configuration configuration = builder.build();
			final TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();


			try {
				requestToken = twitter.getOAuthRequestToken(callbackUrl);

                Log.d(TAG,"Auth URL : "+requestToken.getAuthenticationURL());

				/**
				 *  Loading twitter login page on webview for authorization 
				 *  Once authorized, results are received at onActivityResult
				 *  */
				final Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL()+"&force_login=true");
				startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
				
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
            Log.d(TAG, "Already Logged in Inside Else");
			startActivity(new Intent(this, NewMainActivity.class));
			//loginLayout.setVisibility(View.GONE);
			//shareLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			String verifier = data.getExtras().getString(oAuthVerifier);
			try {
				AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

				long userID = accessToken.getUserId();
				final User user = twitter.showUser(userID);
				String username = user.getName();
				
				saveTwitterInfo(accessToken);

				startActivity(new Intent(this,NewMainActivity.class));

			} catch (Exception e) {
				Log.e("Twitter Login Failed", e.getMessage());
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			loginToTwitter();
			break;

		}
	}

}