package com.will.ontheroad.news;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.activities.BaseActivity;
import com.will.ontheroad.bean.NewsDetailBean;

/**
 * Created by Will on 2016/4/15.
 */
public class NewsDetailActivity extends BaseActivity implements NewsDetailHelper.NewsDetailCallback{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.news_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("正文");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        NewsDetailHelper.getDetail(getIntent().getStringExtra("id"),this);
    }
    public void detailCallback( NewsDetailBean bean){
        TextView title = (TextView) findViewById(R.id.news_detail_title);
        TextView source = (TextView) findViewById(R.id.news_detail_source);
        TextView time = (TextView) findViewById(R.id.news_detail_time);
        TextView content = (TextView) findViewById(R.id.news_detail_content);
        ImageView image =(ImageView) findViewById(R.id.news_detail_image);
        title.setText(bean.getTitle());
        source.setText(bean.getSource());
        time.setText(bean.getPtime());
        content.setText(Html.fromHtml(bean.getBody()));
        Picasso.with(this).load(getIntent().getStringExtra("image")).into(image);
    }
}
