package com.anibij.demoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.anibij.demoapp.model.StatusContract;


/**
 * Created by bsoren on 12-Oct-15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onStart() {
        super.onStart();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
        Preference preference = findPreference(key);
        updateSharedPref(preference);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String prefTitle = preference.getTitle().toString();
        Log.d(TAG," Clicked "+prefTitle);
        return true;

    }

    private void updateSharedPref(Preference preference) {

        if(preference instanceof EditTextPreference){
            EditTextPreference ePref = (EditTextPreference) preference;
            if(ePref.getTitle().toString().contains("Username")){
                ePref.setSummary(ePref.getText());
            }

            if(ePref.getTitle().toString().contains("sync interval")){
                String syncInterval = ePref.getText();
                ePref.setSummary(ePref.getText());
                Toast.makeText(getActivity(),"Sending Sync Interval : "+syncInterval,Toast.LENGTH_SHORT).show();
                getActivity().sendBroadcast(new Intent("com.example.twittershare.SYNC_INTERVAL_UPDATED")
                        .putExtra(StatusContract.UPDATE_INTERVAL,syncInterval));

            }
        }
    }
}
