package com.will.ontheroad.bean;

import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by Will on 2016/2/26.
 */
public class Goal extends BmobObject {
    private String name;
    private Date createDate;
    private Date updateDate;
    private String imageFileName;
    private String presentation;
    private MyUser user;
    private Date achievementDate;
    private String become;
    private String imageThumbnail;
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setCreateDate(Date createDate){
        this.createDate = createDate;
    }
    public Date getCreateDate(){
        return createDate;
    }
    public void setUpdateDate(Date updateDate){
        this.updateDate = updateDate;
    }
    public Date getUpdateDate(){
        return updateDate;
    }
    public void setImageFileName(String imageFileName){
        this.imageFileName = imageFileName;
    }
    public String getImageFileName(){
        return imageFileName;
    }
    public void setPresentation(String presentation){
        this.presentation = presentation;
    }
    public String getPresentation(){
        return presentation;
    }
    public void setUser(MyUser user){
        this.user = user;
    }
    public MyUser getUser(){
        return user;
    }
    public void setAchievementDate(Date achievementDate){
        this.achievementDate = achievementDate;
    }
    public Date getAchievementDate(){
        return achievementDate;
    }
    public void setBecome(String become){
        this.become = become;
    }
    public String getBecome(){
        return become;
    }
    public  void setImageThumbnail(String imageThumbnail){
        this.imageThumbnail = imageThumbnail;
    }
    public String getImageThumbnail(){
        return imageThumbnail;
    }
}
