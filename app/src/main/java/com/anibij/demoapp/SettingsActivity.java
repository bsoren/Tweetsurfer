package com.anibij.demoapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by bsoren on 12-Oct-15.
 */
public class SettingsActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            SettingsFragment sFragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, sFragment, sFragment.getClass().getSimpleName())
                    .commit();
        }

    }
}
