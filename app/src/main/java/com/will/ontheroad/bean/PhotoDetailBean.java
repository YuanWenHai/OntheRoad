package com.will.ontheroad.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will on 2016/4/16.
 */
public class PhotoDetailBean {
    private List<PhotoDetailImage> photos = new ArrayList<>();
    private String setname;
    private String imgsum;
    public void setPhotos(List<PhotoDetailImage> photos){
        this.photos = photos;
    }
    public List<PhotoDetailImage> getPhotos(){
        return photos;
    }
    public void setSetName(String setname){
        this.setname = setname;
    }
    public String getSetName(){
        return setname;
    }
    public void setImgSum(String imgsum){
        this.imgsum = imgsum;
    }
    public String getImgSum(){
        return imgsum;
    }
}
