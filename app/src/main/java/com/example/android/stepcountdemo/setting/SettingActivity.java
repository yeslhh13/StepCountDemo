package com.example.android.stepcountdemo.setting;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.example.android.stepcountdemo.R;

//import com.example.android.stepcountdemo.ContentViewActivity;

/**
 * Created by soyeon on 2017-05-25.
 * 2017-08-03 last로 만짐
 */

public class SettingActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting); //preference setting파일 새로 만들었음
    }

    public static class GreenPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
        }

        public boolean onPreferenceChange(Preference pref, Object newVal) {
            String val = newVal.toString();
            if (pref instanceof ListPreference) {
                ListPreference listPref = (ListPreference) pref;
                //int pref
            }
            return true;
        }
    }
}
