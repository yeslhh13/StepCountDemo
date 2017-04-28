package com.example.android.stepcountdemo;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

/**
 * Created by Kat on 2017-04-28
 * <p>
 * Main Tab activity
 */

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        /**
         * Create first tab
         */
        intent = new Intent(this, TreeActivity.class);
        spec = tabHost.newTabSpec(getString(R.string.tab_first)).setIndicator(getString(R.string.tab_first)).setContent(intent);
        tabHost.addTab(spec);

        /**
         * Set the main tab to first tab(TreeActivity.class)
         */
        tabHost.setCurrentTab(0);
    }
}
