package com.example.android.stepcountdemo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stepcountdemo.db.TreeContract;
import com.example.android.stepcountdemo.db.TreeDBHelper;

import java.io.BufferedReader;
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

        final TreeDBHelper dbHelper = new TreeDBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Check if growing tree exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TreeContract.MainEntry.TABLE_NAME
                + " WHERE " + TreeContract.MainEntry.COLUMN_TREE_LEVEL + " < 5;", null);
        int count = cursor.getCount();

        if (count > 0) {
            // If the growing tree exists in the database, load the tree info from the database
            try {
                FileReader fileReader = new FileReader("StepCountTreeInfo.txt");
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

        // Register service on the broadcast and start {@link StepCountService}
        Intent intent = new Intent(getApplicationContext(), StepCountService.class);
        IntentFilter intentFilter = new IntentFilter("com.example.android.stepcountdemo.StepCountService");
        registerReceiver(mStepCountReceiver, intentFilter);
        startService(intent);
    }

    /**
     * Insert new tree to the database
     */
    private void insertTree() {
        final ContentValues values = new ContentValues();

        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edittext, null);
        final EditText input = (EditText) view.findViewById(R.id.edit_tree_name);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage("나무의 이름을 지어주세요.")
                .setView(view).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(TreeActivity.this, "앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Override
                    }
                })
                .setIcon(R.mipmap.ic_launcher).setTitle("새로운 시작").create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().equals("")) {
                    Toast.makeText(TreeActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    values.put(TreeContract.MainEntry.COLUMN_TREE_NAME, input.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });

        values.put(TreeContract.MainEntry.COLUMN_TREE_TYPE, "cherryblossom");
        values.put(TreeContract.MainEntry.COLUMN_TREE_LEVEL, 1);
        Uri uri = this.getContentResolver().insert(TreeContract.MainEntry.CONTENT_URI, values);
        if (uri == null)
            Log.e("Tree Insertion", "failed");

        try {
            FileOutputStream fileOutputStream = openFileOutput("TreeInfo", MODE_PRIVATE);
            fileOutputStream.write(tree_id);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public int getDrawableIDByStepCount(int stepCount) {
        int drawableID;

        if (stepCount < 300)
            drawableID = R.drawable.tree_base;
        else if (stepCount < 1000)
            drawableID = R.drawable.cherryblossom_2;
        else if (stepCount < 2500)
            drawableID = R.drawable.cherryblossom_3;
        else if (stepCount < 5000)
            drawableID = R.drawable.cherryblossom_4;
        else
            drawableID = R.drawable.cherryblossom_5;

        return drawableID;
    }

    /**
     * method to change the TextView and the ImageView
     */
    public void setViews() {
        stepCountView.setText(String.valueOf(mGlobalVariable.getTreeStep()));
        Object currentTag = treeImage.getTag();
        int drawableID = getDrawableIDByStepCount(mGlobalVariable.getTreeStep());

        if (drawableID == R.drawable.cherryblossom_5) {
            new AlertDialog.Builder(this).setMessage("나무가 모두 자랐습니다! 이제 새로운 나무가 자라납니다.")
                    .setNeutralButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setIcon(R.mipmap.ic_launcher).setTitle("성장 완료")
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
    protected void changeLevel(int drawableID) {
        int newLevel = 2;
        if (drawableID == R.drawable.cherryblossom_3)
            newLevel = 3;
        else if (drawableID == R.drawable.cherryblossom_4)
            newLevel = 4;
        else if (drawableID == R.drawable.cherryblossom_5)
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
    public static class MyHandler extends Handler {
        private final WeakReference<TreeActivity> mActivity;

        public MyHandler(TreeActivity activity) {
            mActivity = new WeakReference<TreeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TreeActivity activity = mActivity.get();
            if (activity != null)
                activity.setViews();
        }
    }

    public class BackgroundThread extends Thread {
        boolean running = false;

        public void setRunning(boolean b) {
            running = b;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        }
    }
}