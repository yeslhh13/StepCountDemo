package com.example.android.stepcountdemo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stepcountdemo.db.TreeContract;
import com.example.android.stepcountdemo.db.TreeDBHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.ref.WeakReference;

/**
 * Created by Kat on 2017-03-24
 * <p>
 * Tree activity
 * Detect user's step count and show it to the user with the state of the tree
 */

public class TreeActivity extends AppCompatActivity {
    /**
     * Handler and Thread to show the user's step count in real time
     */
    private final MyHandler mHandler = new MyHandler(this);
    private BackgroundThread thread;
    /**
     * TextView to show step count to the user
     */
    private TextView stepCountView;
    /**
     * TextView to show tree's name to the user
     */
    private TextView treeNameView;
    /**
     * {@link GlobalVariable} to get the step count value
     */
    private GlobalVariable mGlobalVariable;
    /**
     * {@link StepCountReceiver} to restart the service when the task is killed
     */
    private StepCountReceiver mStepCountReceiver;
    /**
     * ImageView to show the tree state to the user
     */
    private ImageView treeImage;
    /**
     * Tree ID value
     * -1 when there's no tree
     */
    private int tree_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);

        // Get the {@link GlobalVariable} instance
        mGlobalVariable = (GlobalVariable) getApplication();
        mStepCountReceiver = new StepCountReceiver();

        stepCountView = (TextView) findViewById(R.id.step_count);
        treeImage = (ImageView) findViewById(R.id.treeImage);
        treeNameView = (TextView) findViewById(R.id.walk_counter);

        checkTree();

        // Register service on the broadcast and start {@link StepCountService}
        Intent intent = new Intent(getApplicationContext(), StepCountService.class);
        IntentFilter intentFilter = new IntentFilter("com.example.android.stepcountdemo.StepCountService");
        registerReceiver(mStepCountReceiver, intentFilter);
        startService(intent);

        thread = new BackgroundThread();
        thread.setRunning(true);
        thread.start();
    }

    /**
     * Check if growing tree exists
     */
    private void checkTree() {
        final TreeDBHelper dbHelper = new TreeDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Check if growing tree exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TreeContract.MainEntry.TABLE_NAME
                + " WHERE " + TreeContract.MainEntry.COLUMN_TREE_LEVEL + " < 5;", null);
        int count = cursor.getCount();

        if (count > 0) {
            // If the growing tree exists in the database, load the tree info from the database
            try {
                FileReader fileReader = new FileReader(new File(Environment.getExternalStorageDirectory(), getString(R.string.txt_file_name)));
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                tree_id = Integer.parseInt(bufferedReader.readLine());
                mGlobalVariable.setTreeStep(Integer.parseInt(bufferedReader.readLine()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            treeImage.setImageResource(getDrawableIDByStepCount(mGlobalVariable.getTreeStep()));
            treeImage.setTag(getDrawableIDByStepCount(mGlobalVariable.getTreeStep()));
        } else {
            // If there are no trees growing, insert new tree to the database
            insertTree();
        }

        cursor.close();
    }

    /**
     * Insert new tree to the database
     */
    private void insertTree() {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edittext, null);
        final EditText input = (EditText) view.findViewById(R.id.edit_tree_name);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(getString(R.string.tree_name))
                .setView(view).setPositiveButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Override
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            finishAffinity();
                            return true;
                        }
                        return false;
                    }
                }).setCancelable(false).setIcon(R.drawable.greentree_logo).setTitle(getString(R.string.tree_dialog_title)).create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().equals("")) {
                    Toast.makeText(TreeActivity.this, getString(R.string.tree_dialog_message), Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();

                    values.put(TreeContract.MainEntry.COLUMN_TREE_NAME, input.getText().toString());
                    values.put(TreeContract.MainEntry.COLUMN_TREE_TYPE, "cherryblossom");
                    values.put(TreeContract.MainEntry.COLUMN_TREE_LEVEL, 1);

                    Uri uri = TreeActivity.this.getContentResolver().insert(TreeContract.MainEntry.CONTENT_URI, values);

                    if (uri == null)
                        Log.e("Tree Insertion", "failed");

                    try {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getString(R.string.txt_file_name));
                        FileOutputStream fos = new FileOutputStream(file);

                        String text = String.valueOf(tree_id) + "\n" + String.valueOf(0);
                        fos.write(text.getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    alertDialog.dismiss();
                }
            }
        });

        treeImage.setImageResource(getDrawableIDByStepCount(R.drawable.tree_base));
        treeImage.setTag(R.drawable.tree_base);
    }

    /**
     * Start the Handler loop
     */
    @Override
    protected void onStart() {
        super.onStart();

        thread = new BackgroundThread();
        thread.setRunning(true);
        thread.start();
    }

    /**
     * stop the Handler loop when paused
     */
    @Override
    protected void onPause() {
        super.onPause();

        boolean b = true;
        thread.setRunning(false);

        // stop the thread
        while (b) {
            try {
                thread.join();
                b = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * restart the Handler loop when restarted
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        thread = new BackgroundThread();
        thread.setRunning(true);
        thread.start();
    }

    /**
     * restart the Handler loop when resumed
     */
    @Override
    protected void onResume() {
        super.onResume();

        thread = new BackgroundThread();
        thread.setRunning(true);
        thread.start();

        //checkbox가 true값인지 받아옴
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String checkText = Boolean.toString(prefs.getBoolean("useTreeName", true));

        //체크박스 값에따라 나무 이름 표시할지말지 결정
        if (prefs.getBoolean("useTreeName", true))
            treeNameView.setText(String.valueOf(tree_id)); //String.valueOf 형변환
        else
            treeNameView.setText(" ");
    }

    /**
     * stop the Handler loop when destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        boolean b = true;
        thread.setRunning(false);

        // stop the thread
        while (b) {
            try {
                thread.join();
                b = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.i("TreeActivity", "onDestroy");
        unregisterReceiver(mStepCountReceiver);
    }

    /**
     * stop the Handler loop when stopped
     */
    @Override
    protected void onStop() {
        super.onStop();
        boolean b = true;
        thread.setRunning(false);

        // stop the thread
        while (b) {
            try {
                thread.join();
                b = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Decide which image to use when the step count changes
     *
     * @param stepCount to decide which drawable ID has to be returned
     * @return drawable ID needed to change the ImageView
     */
    private int getDrawableIDByStepCount(int stepCount) {
        int drawableID;

        //TODO:change step count
        if (stepCount < 300)
            drawableID = R.drawable.tree_base;
        else if (stepCount < 1000)
            drawableID = R.drawable.tree_step2;
        else if (stepCount < 2500)
            drawableID = R.drawable.tree_step3;
        else if (stepCount < 5000)
            drawableID = R.drawable.tree_step4;
        else
            drawableID = R.drawable.tree_step5;

        return drawableID;
    }

    /**
     * method to change the TextView and the ImageView
     */
    private void setViews() {
        stepCountView.setText(String.valueOf(mGlobalVariable.getTreeStep()));
        Object currentTag = treeImage.getTag();
        int drawableID = getDrawableIDByStepCount(mGlobalVariable.getTreeStep());

        if (drawableID == R.drawable.tree_step5) {

            new AlertDialog.Builder(this).setMessage(getString(R.string.tree_end_message))
                    .setNeutralButton(getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setIcon(R.drawable.greentree_logo).setTitle(getString(R.string.tree_end_title))
                    .create().show();

            mGlobalVariable.resetTreeStep();
            insertTree();
        } else {
            treeImage.setImageResource(drawableID);
            treeImage.setTag(drawableID);

            if (!currentTag.equals(treeImage.getTag())) {
                changeLevel(drawableID);
            }
        }
    }

    /**
     * Change Level if the drawable has changed
     *
     * @param drawableID to change level
     */
    private void changeLevel(int drawableID) {
        int newLevel = 2;
        if (drawableID == R.drawable.tree_step3)
            newLevel = 3;
        else if (drawableID == R.drawable.tree_step4)
            newLevel = 4;
        else if (drawableID == R.drawable.tree_step5)
            newLevel = 5;

        ContentValues values = new ContentValues();
        values.put(TreeContract.MainEntry.COLUMN_TREE_LEVEL, newLevel);
        int result = getContentResolver().update(TreeContract.MainEntry.CONTENT_URI, values, TreeContract.MainEntry._ID + " = " + tree_id, null);
        if (result == 0)
            Log.e("Update Tree", "failed");
    }

    /**
     * Custom Handler class
     */
    private static class MyHandler extends Handler {
        private final WeakReference<TreeActivity> mActivity;

        private MyHandler(TreeActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TreeActivity activity = mActivity.get();
            if (activity != null)
                activity.setViews();
        }
    }

    private class BackgroundThread extends Thread {
        boolean running = false;

        private void setRunning(boolean b) {
            running = b;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        }
    }
}