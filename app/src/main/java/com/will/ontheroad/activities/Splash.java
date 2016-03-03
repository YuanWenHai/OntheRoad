package com.will.ontheroad.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;

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
    }
    private void initializeSplashImage(){
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY",1f,1.3f);
        ObjectAnimator.ofPropertyValuesHolder(splashImage, pvhX, pvhY).setDuration(2000).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                if(BmobUser.getCurrentUser(Splash.this) == null) {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                }else{
                    startActivity(new Intent(Splash.this,MainActivity.class));
                }
                finish();
            }
        }).start();
    }
}
