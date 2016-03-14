package com.will.ontheroad.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Will on 2016/2/26.
 */
public class Diary extends BmobObject {
    private String content;
    private String image;
    private String imageThumbnail;
    private String goalId;
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return content;
    }
    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return image;
    }
    public void setGoalId(String goalId){
        this.goalId = goalId;
    }
    public String getGoalId(){
        return goalId;
    }
    public void setImageThumbnail(String imageThumbnail){
        this.imageThumbnail = imageThumbnail;
    }
    public String getImageThumbnal(){
        return imageThumbnail;
    }
}
