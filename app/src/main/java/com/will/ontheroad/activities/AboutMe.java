package com.will.ontheroad.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.will.ontheroad.R;

/**
 * Created by Will on 2016/3/23.
 */
public class AboutMe extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_me_page);
        TextView title = (TextView) findViewById(R.id.universal_toolbar_text);
        title.setVisibility(View.GONE);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.universal_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
