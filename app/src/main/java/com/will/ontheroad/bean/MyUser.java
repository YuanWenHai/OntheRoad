package com.will.ontheroad.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by Will on 2016/2/26.
 */
public class MyUser extends BmobUser {
    private String userImageName;
    private String myGoalId;
    private String userName;
    private String userImageThumbnail;
    private String backgroundImage;
    public void setUserImageName(String userImageName){
        this.userImageName = userImageName;
    }
    public String getUserImageName(){
        return userImageName;
    }
    public void setMyGoalId(String myGoalId){
        this.myGoalId = myGoalId;
    }
    public String getMyGoalId(){
        return myGoalId;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserImageThumbnail(String userImageThumbnail){
        this.userImageThumbnail = userImageThumbnail;
    }
    public String getUserImageThumbnail(){
        return userImageThumbnail;
    } public void setBackgroundImage(String backgroundImage){
        this.backgroundImage = backgroundImage;
    }
    public String getBackgroundImage(){
        return backgroundImage;
    }

}
