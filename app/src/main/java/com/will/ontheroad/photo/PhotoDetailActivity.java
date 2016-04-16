package com.will.ontheroad.photo;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.will.ontheroad.R;
import com.will.ontheroad.activities.BaseActivity;
import com.will.ontheroad.bean.PhotoDetailImage;
import com.will.ontheroad.constant.Constant;
import com.will.ontheroad.utility.OkHttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will on 2016/4/16.
 */
public class PhotoDetailActivity extends BaseActivity  {
    private ViewPager pager;
    private PhotoViewPagerAdapter adapter;
    private List<PhotoDetailImage> data = new ArrayList<>();
    private TextView title;
    private TextView content;
    private TextView count;
    private RelativeLayout info;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.photo_detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        title = (TextView) findViewById(R.id.photo_detail_title);
        content = (TextView) findViewById(R.id.photo_detail_text);
        count = (TextView) findViewById(R.id.photo_detail_count);
        info = (RelativeLayout) findViewById(R.id.photo_detail_info);
        getData();
        setupViewPager();
    }
    private void getData(){
        String id = getIntent().getStringExtra("id");
        String url = Constant.IMAGE_DETAIL+id+Constant.IMAGE_END;
        OkHttpUtil.get(url, new OkHttpUtil.Callback() {
            @Override
            public void onResult(String result) {
                data.addAll(PhotoJsonUtil.readJsonPhotoDetailImage(result));
                adapter.notifyDataSetChanged();
                title.setText(data.get(0).getImgTitle());
                content.setText(data.get(0).getNote());
                String str = "1/"+data.size();
                count.setText(str);
            }
        });
    }
    private void setupViewPager(){
        pager = (ViewPager) findViewById(R.id.photo_detail_view_pager);
        adapter = new PhotoViewPagerAdapter(getSupportFragmentManager(),data);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                title.setText(data.get(position).getImgTitle());
                content.setText(data.get(position).getNote());
                String str = (position+1)+"/"+data.size();
                count.setText(str);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
