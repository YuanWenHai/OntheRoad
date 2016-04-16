package com.will.ontheroad.bean;

import java.io.Serializable;

/**
 * Created by Will on 2016/4/16.
 */
public class PhotoDetailImage implements Serializable{
    private String note;
    private String imgtitle;
    private String imgurl;
    public void setNote(String note){
        this.note = note;
    }
    public String getNote(){
        return note;
    }
    public void setImgTitle(String imgtitle){
        this.imgtitle = imgtitle;
    }
    public String getImgTitle(){
        return imgtitle;
    }
    public void setImgUrl(String imgurl){
        this.imgurl = imgurl;
    }
    public String getImgUrl(){
        return imgurl;
    }
}
