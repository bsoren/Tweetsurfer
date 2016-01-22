package com.anibij.demoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anibij.demoapp.Utils.AppPrefrences;
import com.anibij.demoapp.model.StatusContract;
import com.anibij.demoapp.model.StatusListLoader;
import com.anibij.demoapp.service.RefreshService;
import com.squareup.picasso.Picasso;

public class NewMainActivity extends AppCompatActivity implements TabFragment.OnDrawerIconClick {

    private static final String TAG = NewMainActivity.class.getSimpleName();

    private StatusListLoader mStatusListLoader;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ActionBarDrawerToggle mDrawerToggle;
    SharedPreferences mSharedPreferences;
    ImageView mProfileImageView;
    TextView mUserNameView;
    TextView mUserScreenNameView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout_activity);

        // SharedPreferences
        mSharedPreferences = getSharedPreferences(AppPrefrences.PREF_NAME,0);

        String userName = mSharedPreferences.getString(AppPrefrences.PREF_USER_NAME,"");
        String userScreenName =  mSharedPreferences.getString(AppPrefrences.PREF_USER_SCREEN_NAME,"");
        String userProfileImageUrl =  mSharedPreferences.getString(AppPrefrences.PREF_USER_PROFILE_IMAGE_URL, "");

        /**
         *Setup the DrawerLayout and NavigationView
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        /* Inflate header and add it to NavigationView */
        View headerView = mNavigationView.inflateHeaderView(R.layout.header);

        /* Bind header items to variables */
        mProfileImageView =(ImageView) headerView.findViewById(R.id.profile_image);
        mUserNameView = (TextView) headerView.findViewById(R.id.user_name);
        mUserScreenNameView = (TextView) headerView.findViewById(R.id.user_screen_name);

        /* Set username */
        mUserNameView.setText(userName);

        /* Set user screen name */
        mUserScreenNameView.setText(userScreenName);

        if(!userProfileImageUrl.isEmpty()) {
            Picasso.with(this).load(userProfileImageUrl)
                    .error(R.drawable.no_image)
                    .placeholder(R.drawable.twitter_placeholder_image)
                    .into(mProfileImageView);
        }

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        Bundle bundle = new Bundle();
        bundle.putInt(StatusContract.TAB_FRAGMENT, 0);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(bundle);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, fragment).commit();

        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_item_timeline) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(StatusContract.TAB_FRAGMENT, 0);
                    TabFragment fragment = new TabFragment();
                    fragment.setArguments(bundle);

                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, fragment).commit();
                }

                if (menuItem.getItemId() == R.id.nav_item_mentions) {

                    Bundle bundle = new Bundle();
                    bundle.putInt(StatusContract.TAB_FRAGMENT, 1);
                    TabFragment fragment = new TabFragment();
                    fragment.setArguments(bundle);

                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, fragment).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_messages) {

                    Bundle bundle = new Bundle();
                    bundle.putInt(StatusContract.TAB_FRAGMENT, 2);
                    TabFragment fragment = new TabFragment();
                    fragment.setArguments(bundle);

                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, fragment).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_likes) {

                    Bundle bundle = new Bundle();
                    bundle.putInt(StatusContract.TAB_FRAGMENT, 3);
                    TabFragment fragment = new TabFragment();
                    fragment.setArguments(bundle);

                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, fragment).commit();

                }

                return true;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name){
            @Override
            public void syncState() {
                super.syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                syncState();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.refresh:
                /* Getting since Id and passing it to RefreshService */
                long sinceId = mSharedPreferences.getLong(AppPrefrences.PREF_SINCE_ID,1000L);
                Intent intent = new Intent(this, RefreshService.class);
                intent.putExtra("SINCE_ID",sinceId);
                startService(intent);
                Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.purge:
                int deleteRows = getContentResolver().delete(StatusContract.CONTENT_URI, null, null);
                Toast.makeText(this, "Deleted Rows : " + deleteRows, Toast.LENGTH_SHORT).show();
                mSharedPreferences.edit().putLong(AppPrefrences.PREF_SINCE_ID,1000L).commit();
                sendBroadcast(new Intent(StatusContract.NEW_ITEMS));
                Log.d(TAG, "BroadCast sent from Mainactivity");
                break;
            case R.id.postTweet:
                 startActivity(new Intent(this,StatusActivity.class));
                break;
            case R.id.sign_out:
                boolean pktL = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN,false);
                Log.d(TAG,"Boolean "+pktL);
                if(mSharedPreferences.contains(AppPrefrences.PREF_KEY_TWITTER_LOGIN)) {

                    SharedPreferences.Editor edit = mSharedPreferences.edit();
                    edit.putBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
                    edit.remove(AppPrefrences.PREF_SINCE_ID);
                    edit.commit();
                    Log.d(TAG, "Removed pref_key_twitter_login");
                    pktL = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
                    Log.d(TAG, "Boolean " + pktL);
                    Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                }

                boolean isLoggedIn = mSharedPreferences.getBoolean(AppPrefrences.PREF_KEY_TWITTER_LOGIN, false);
                if (!isLoggedIn) {
                    startActivity(new Intent(this, LoginActivity.class));
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onDrawerClick() {

    }
}
