package com.will.ontheroad.bean;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Will on 2016/2/26.
 */
public class Diary extends BmobObject {
    private String content;
    private Date postTime;
    private BmobFile picture;
    private Goal goal;
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return content;
    }
    public void setPostTime(Date postTime){
        this.postTime = postTime;
    }
    public Date getPostTime(){
        return postTime;
    }
    public void setPicture(BmobFile picture){
        this.picture = picture;
    }
    public BmobFile getPicture(){
        return picture;
    }
    public void setGoal(Goal goal){
        this.goal = goal;
    }
    public Goal getGoal(){
        return goal;
    }
}
