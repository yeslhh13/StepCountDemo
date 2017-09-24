package com.example.android.stepcountdemo.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.example.android.stepcountdemo.R;

/**
 * Created by soyeon on 2017-05-25.
 * 2017-09-10 last로 만짐
 */

public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    //개발자 정보를 띄우기 위한 코드
    //프래그먼트 생성해 addPreferencesFromResource 코드오류 해결
    public static class GreenPreferenceFragment extends PreferenceFragment {
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            Preference dialogPref = findPreference("dialog_preference");
            dialogPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle("개발자 정보")
                            .setView(R.layout.custom_dialog).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(false).setIcon(R.drawable.greentree_logo).create();
                    alertDialog.show();
                    return false;
                }
            });
        }
    }

}
