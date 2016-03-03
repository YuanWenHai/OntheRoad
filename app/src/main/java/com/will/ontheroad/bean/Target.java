package com.will.ontheroad.bean;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by Will on 2016/2/28.
 */
public class Target extends BmobObject{
    private String become;
    private Date achievementDate;
    private BmobUser user;
    public void setBecome(String become){
        this.become = become;
    }
    public String getBecome(){
        return become;
    }
    public void setAchievementDate(Date achievementDate){
        this.achievementDate = achievementDate;
    }
    public Date getAchievementDate(){
        return achievementDate;
    }
    public void setUser(BmobUser user){
        this.user = user;
    }
    public BmobUser getUser(){
        return user;
    }
}
