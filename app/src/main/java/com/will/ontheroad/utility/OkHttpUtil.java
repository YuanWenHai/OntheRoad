package com.will.ontheroad.utility;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Will on 2016/4/14.
 * 网络加载内容并回调结果
 */
public class OkHttpUtil {
    private OkHttpClient client;
    private static OkHttpUtil instance;
    private Handler handler;
    private  OkHttpUtil(){
        client = new OkHttpClient();
        client.setConnectTimeout(8, TimeUnit.SECONDS);
        client.setReadTimeout(8,TimeUnit.SECONDS);
        client.setWriteTimeout(8,TimeUnit.SECONDS);
        handler = new Handler(Looper.getMainLooper());
    }
    public static OkHttpUtil getInstance(){
        if(instance == null){
            instance = new OkHttpUtil();
        }
        return instance;
    }
    private void getRequest(String url, final Callback callback){
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {}
            @Override
            public void onResponse(Response response) throws IOException {
                final String str = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(str);
                    }
                });
            }
        });
    }
    public static void get(String url,Callback callback){
        getInstance().getRequest(url,callback);
    }



    public interface Callback{
        void onResult(String result);
    }

}
