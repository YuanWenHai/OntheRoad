package com.will.ontheroad.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by Will on 2016/3/1.
 */
public class BasePopupWindow extends PopupWindow {
    protected View mContentView;
    public BasePopupWindow (View contentView,int width,int height){
        super(contentView,width,height);
        mContentView = contentView;
        setBackgroundDrawable(new ColorDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() ==MotionEvent.ACTION_OUTSIDE){
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }
    public View findViewById(int id){
        return mContentView.findViewById(id);
    }
    public static int dpToPx(Context context,int dp){
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
