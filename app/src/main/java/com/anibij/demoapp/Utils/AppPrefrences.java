package com.anibij.demoapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bsoren on 02-Nov-15.
 */
public class AppPrefrences{

    public static final String PREF_NAME = "sample_twitter_pref";
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    public static final String PREF_USER_NAME = "twitter_user_name";
    public static final String PREF_USER_SCREEN_NAME = "twitter_user_screen_name";
    public static final String PREF_USER_PROFILE_IMAGE_URL = "twitter_user_profile_image_url";
    public static final String PREF_LOAD_MORE_ITEM_LOCATIONS = "load_more_items";

    public  static final String PREF_SINCE_ID = "tweet_since_id";

    public  static final String MENTION_PREF_SINCE_ID = "mention_since_id";

    private static SharedPreferences mSharedPreferences;
    private Context mContext;

    public SharedPreferences getInstance(){
        if(mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences(PREF_NAME, 0);
        }
        return mSharedPreferences;
    }
    public AppPrefrences(Context context) {
        mContext = context;
    }

    public static String getPreference(String key){

        return mSharedPreferences.getString(key,"");
    }

    public static void setPreference(String key, String value){

        mSharedPreferences.edit().putString(key,value).commit();
    }

}
