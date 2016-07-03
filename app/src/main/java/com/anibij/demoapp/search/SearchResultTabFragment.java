package com.anibij.demoapp.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anibij.demoapp.R;
import com.anibij.demoapp.model.StatusContract;
import com.anibij.demoapp.model.StatusListLoader;


public class SearchResultTabFragment extends Fragment {

    private static final String TAG = SearchResultTabFragment.class.getSimpleName();
    private StatusListLoader mStatusListLoader;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static Toolbar toolbar;
    public static int int_items = 3 ;
    private String searchText;

    ActionBar supportActionBar;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        int arg = getArguments().getInt(StatusContract.TAB_FRAGMENT);
        searchText =  getArguments().getString(SearchFragment.SEARCH_TEXT);
        Log.d(TAG," Search Text "+searchText);


        switch (arg) {
            case 0:
                viewPager.setCurrentItem(0, true);
                break;

            case 1:
                viewPager.setCurrentItem(1, true);
                break;
            case 2:
                viewPager.setCurrentItem(2, true);
                break;
            default:

        }




    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.tab_layout,null);

        toolbar = (Toolbar) x. findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(android.R.drawable.btn_star);


        tabLayout = (TabLayout) x.findViewById(R.id.tabs);

        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                String title = "Tweets";

                switch (position) {
                    case 0:
                        title = "Tweets";
                        break;
                    case 1:
                        title = "People";
                        break;
                    case 2:
                        title = "Nearby";
                        break;
                    default:

                }

                supportActionBar.setTitle(title);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);

                int iconDrawable = R.drawable.timeline;

                for (int i = 0; i < tabLayout.getTabCount(); i++) {

                    switch(i){
                        case 0 :
                            iconDrawable = R.drawable.timeline;
                            break;
                        case 1 :
                            iconDrawable = R.drawable.email;
                            break;
                        case 2 :
                            iconDrawable = R.drawable.message;
                            break;
                        default:
                            iconDrawable = R.drawable.timeline;
                            break;

                    }

                    tabLayout.getTabAt(i).setIcon(iconDrawable);
                }

            }
        });


        return x;

    }

    class MyAdapter extends FragmentPagerAdapter {



        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */


        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0:
                    //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Timeline");
                    Bundle bundle = new Bundle();
                    bundle.putString(SearchFragment.SEARCH_TEXT,searchText);

                    SearchTweetFragment searchTweetFragment = new SearchTweetFragment();
                    searchTweetFragment.setArguments(bundle);

                    return searchTweetFragment;
                case 1:
                    //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mentions");
                    Log.d(TAG,"Searhing people");
                    return new SearchPeopleFragment();
                case 2:
                    //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Messages");
                   // return new DirectMessageFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(SearchFragment.SEARCH_TEXT,searchText);

                    SearchTweetFragment searchTweetFragment2 = new SearchTweetFragment();
                    searchTweetFragment2.setArguments(bundle1);

                    return searchTweetFragment2;
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

//            switch (position){
//                case 0 :
//                    return "Tweets";
//                case 1 :
//                    return "Social";
//                case 2 :
//                    return "updates";
//            }
            return null;
        }
    }

}