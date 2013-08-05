package com.at.consumo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

/**
 * Created by at on 8/4/13.
 */
public class UserSettingActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {


    String USERNAME_PREFERENCE = "prefUsername";
    String PASSWORD_PREFERENCE = "prefPassword";
    private EditTextPreference usernamePreference;
    private EditTextPreference passwordPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the XML preferences file
        addPreferencesFromResource(R.xml.settings);
        // Get a reference to the preferences
        usernamePreference = (EditTextPreference) getPreferenceScreen().findPreference(USERNAME_PREFERENCE);
        passwordPreference = (EditTextPreference) getPreferenceScreen().findPreference(PASSWORD_PREFERENCE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        usernamePreference.setSummary(sharedPreferences.getString(USERNAME_PREFERENCE, ""));
        String password = sharedPreferences.getString(PASSWORD_PREFERENCE, null);
        if (password != null){
            password = "********";
        }else{
            password  = passwordPreference.getSummary().toString();
        }
        passwordPreference.setSummary(password);
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Let's do something a preference value changes
        if (key.equals(USERNAME_PREFERENCE)) {
            usernamePreference.setSummary("Current value is " + sharedPreferences.getString(key, ""));
        }else
        if (key.equals(PASSWORD_PREFERENCE)) {
            passwordPreference.setSummary("Current value is " + sharedPreferences.getString(key, ""));
        }

    }
}
