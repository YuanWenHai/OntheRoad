package com.will.ontheroad.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import cn.bmob.v3.Bmob;

/**
 * Created by Will on 2016/2/26.
 */
public class BaseActivity extends AppCompatActivity {
    protected int mScreenWidth;
    protected int mScreenHeight;
    private Toast mToast;
    private String BMOB_APPID = "02c0cb3d206b649c517d0cd2b663e9c4";
    protected boolean toggle = true;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, BMOB_APPID);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //initializePicasso();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    public void showToast(String message) {
        if (!TextUtils.isEmpty(message)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    public int getStateBarHeight() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }
    public int dpToPx(Context context,int dp){
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    public void delay(final int time){
        toggle = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(time);
                toggle = true;
            }
        }).start();
    }
}
