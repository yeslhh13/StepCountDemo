package com.example.android.stepcountdemo.setting;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.widget.Toast;

import com.example.android.stepcountdemo.R;

/**
 * Created by soyeon on 2017-09-10.
 * 환경설정 추가 코드부
 */

public class SettingSubActivity extends PreferenceActivity {
    public static String Name = null; //나무 이름 설정변수
    public static String font = null; //폰트 설정 변수

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting); //preference setting파일

        final EditTextPreference Tree_Name = (EditTextPreference) findPreference("treeName"); //나무 이름 설정받기
        Name = Tree_Name.getText(); //입력한 이름 값 받아오기
        Tree_Name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                StringBuffer result = new StringBuffer();
                result.append("<FONT COLOR = #0000ff>");
                result.append("</FONT>");
                result.append("으로 설정이 완료되었습니다.");

                //Name = newValue.toString();
                Toast.makeText(SettingSubActivity.this, Html.fromHtml(result.toString()), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

}
