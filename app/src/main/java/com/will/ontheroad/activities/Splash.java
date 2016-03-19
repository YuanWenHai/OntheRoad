package com.will.ontheroad.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;

import com.squareup.picasso.Cache;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;

import java.util.Random;

import cn.bmob.v3.BmobUser;

/**
 * Created by Will on 2016/2/24.
 */
public class Splash extends Activity {
    private ImageView splashImage;
    private static final int[] SPLASHES = {
            R.drawable.splash0,
            R.drawable.splash1,
            R.drawable.splash2,
            R.drawable.splash3,
            R.drawable.splash4,
            R.drawable.splash5,
            R.drawable.splash6,
            R.drawable.splash7,
            R.drawable.splash8,
            R.drawable.splash9,
            R.drawable.splash10,
            R.drawable.splash11,
            R.drawable.splash12,
            R.drawable.splash13,
            R.drawable.splash14,
            R.drawable.splash15,
            R.drawable.splash16,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        splashImage = (ImageView) findViewById(R.id.splash_image_view);
        Random random = new Random(SystemClock.elapsedRealtime());
        splashImage.setImageResource(SPLASHES[random.nextInt(SPLASHES.length)]);
        initializeSplashImage();
        //Slide fade = new Slide();
        //fade.setDuration(1000);
        //getWindow().setExitTransition(fade);
        //initializePicasso();
    }
    private void initializeSplashImage(){
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY",1f,1.3f);
        PropertyValuesHolder p = PropertyValuesHolder.ofFloat("alpha",1.0f,0.1f);
        ObjectAnimator.ofPropertyValuesHolder(splashImage, pvhX, pvhY,p).setDuration(2000).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                if(BmobUser.getCurrentUser(Splash.this) == null) {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                }else{
                    Intent intent = new Intent(Splash.this,MainActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
                finish();
                //overridePendingTransition(0,R.anim.alpha_out);
            }
        }).start();
    }
    protected void initializePicasso(){
        Downloader downloader = new OkHttpDownloader(getApplicationContext(),1024*1024*20);
        Cache cache = new com.squareup.picasso.LruCache(this);
        Picasso mPicasso = new Picasso.Builder(getApplicationContext()).downloader(downloader).memoryCache(cache).build();
        Picasso.setSingletonInstance(mPicasso);
    }
}
