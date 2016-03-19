package com.will.ontheroad.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.will.ontheroad.R;

/**
 * Created by Will on 2016/3/16.
 */
public class TestActivity extends BaseActivity {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        //getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ListView test = (ListView) findViewById(R.id.test_list);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_expandable_list_item_1);
        int i = 0;
        while (i++<20) {
            adapter.add(i);
        }
        test.setAdapter(adapter);
        View view = View.inflate(this,R.layout.test2,null);
        test.addHeaderView(view);
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
    }
}
