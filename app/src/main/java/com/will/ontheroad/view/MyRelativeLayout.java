package com.will.ontheroad.view;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Will on 2016/3/6.
 */
public class MyRelativeLayout extends RelativeLayout{
    public MyRelativeLayout(Context context){
        super(context);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        Log.e("onInterceptTouchEvent", "execute");
        return super.onInterceptTouchEvent(event);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.e("onTouchEvent","execute");
        return true;
    }
}
