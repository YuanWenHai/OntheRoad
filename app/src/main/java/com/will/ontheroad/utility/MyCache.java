package com.will.ontheroad.utility;

import android.graphics.drawable.Drawable;
import android.util.LruCache;

/**
 * Created by Will on 2016/3/13.
 */
public class MyCache {
    private LruCache<String,Drawable> lru;
    private static MyCache cache;
    private MyCache(){
        lru = new LruCache<>(30*1024*1024);
    }
    public static MyCache getInstance(){
        if(cache==null){
            cache = new MyCache();
        }
        return cache;
    }
    public LruCache<String,Drawable> getLru(){
        return lru;
    }
}
