package com.will.ontheroad.photo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.will.ontheroad.bean.PhotoDetailImage;

import java.util.List;

/**
 * Created by Will on 2016/4/16.
 */
public class PhotoViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<PhotoDetailImage> data;

    public PhotoViewPagerAdapter(FragmentManager fm,List<PhotoDetailImage> data){
        super(fm);
        this.data = data;
    }
    @Override
    public int getCount(){
        return data.size();
    }
    public Fragment getItem(int position){
        return PhotoDetailFragment.getInstance(data.get(position).getImgUrl());

    }
    public void setData(List<PhotoDetailImage> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }
}
