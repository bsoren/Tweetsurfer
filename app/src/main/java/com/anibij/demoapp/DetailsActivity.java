package com.anibij.demoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.tweet_detail_layout);
                android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                //getSupportActionBar().setTitle("Tweet");

                // Check if this activity was created before
                if (savedInstanceState == null) {   // 1
                        // Create a fragment
                        DetailsFragment fragment = new DetailsFragment(); // 2
                        getSupportFragmentManager()
                           .beginTransaction()
                           .add(R.id.tweetDetailcontainerView, fragment,
                                   fragment.getClass().getSimpleName()).commit(); // 3
                }

        }
}
