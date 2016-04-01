package com.will.ontheroad.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Will on 2016/3/31.
 */
public class Note extends BmobObject {
    private String image;
    private String content;
    private MyUser user;
    private String imageThumbnail;
    public String getImage(){
        return image;
    }
    public void setImage(String image){
        this.image = image;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }
    public void setUser(MyUser user){
        this.user = user;
    }
    public MyUser getUser(){
        return user;
    }
    public void setImageThumbnail(String imageThumbnail){
        this.imageThumbnail = imageThumbnail;
    }
    public String getImageThumbnail(){
        return imageThumbnail;
    }

}
