package com.will.ontheroad.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.will.ontheroad.utility.DownloadImageListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import cn.bmob.v3.Bmob;

/**
 * Created by Will on 2016/2/26.
 */
public class BaseActivity extends Activity {
    protected int mScreenWidth;
    protected int mScreenHeight;
    private Toast mToast;
    private String BMOB_APPID = "02c0cb3d206b649c517d0cd2b663e9c4";
    protected SharedPreferences.Editor spEditor;
    protected SharedPreferences sp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, BMOB_APPID);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        sp = getSharedPreferences("cache",MODE_PRIVATE);
        spEditor = sp.edit();
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

    public String countDateHour(Date targetDate) {
        Date currentDate = new Date(System.currentTimeMillis());
        GregorianCalendar date1 = new GregorianCalendar();
        GregorianCalendar date2 = new GregorianCalendar();
        date1.setTime(targetDate);
        date2.setTime(currentDate);
        float hourNumber =  (date1.getTimeInMillis() - date2.getTimeInMillis()) / (1000 * 3600);
        return String.valueOf((int)(hourNumber));
    }

    public String getFormattedDateCorrectToDay(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        return format.format(date);
    }
    public String getFormattedDateCorrectToMinute(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm",Locale.CHINA);
        return format.format(date);
    }
    public int dpToPx(Context context,int dp){
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
    //下载图片并缓存，如有缓存则直接返回缓存path
    public  void downloadImage( Context context, final String url, final DownloadImageListener listener){
        final String dir = context.getFilesDir()+"/thumbnail";
        final SharedPreferences sp = context.getSharedPreferences("cache",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        if(sp.contains(url)){
            listener.onSuccess(sp.getString(url, ""));
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL imageUrl;
                    InputStream is = null;
                    FileOutputStream os  = null;
                    try{
                        imageUrl = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                        connection.setConnectTimeout(5000);
                        connection.setRequestMethod("GET");
                        is = connection.getInputStream();
                        File file = new File(dir);
                        if(!file.exists()){
                            file.mkdir();
                        }
                        final File image = new File(dir+"/"+url.replaceAll("/",""));
                        os = new FileOutputStream(image);
                        int len = 0;
                        byte[] bytes= new byte[1024];
                        while((len=is.read(bytes)) > 0){
                            os.write(bytes,0,len);
                        }
                        os.flush();
                        editor.putString(url, image.getPath());
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(image.getPath());
                            }
                        });

                    }catch (IOException e){
                        e.printStackTrace();
                        Log.e("downloadImage", "error");
                    }finally{
                        try{
                            if(is != null){
                                is.close();
                            }
                            if(os != null){
                                os.close();
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

}
