package com.anibij.demoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anibij.demoapp.model.StatusListLoader;


public class TabFragment extends Fragment {

    private StatusListLoader mStatusListLoader;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static Toolbar toolbar;
    public static int int_items = 4 ;
    TabFragment.OnDrawerIconClick activity;



    public interface OnDrawerIconClick{
        void onDrawerClick();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Timeline");

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
                        case 3 :
                            iconDrawable = R.drawable.star;
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
                case 0 : return new TweetFragment();
                case 1 : return new PrimaryFragment();
                case 2 : return new PrimaryFragment();
                case 3 : return new PrimaryFragment();
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