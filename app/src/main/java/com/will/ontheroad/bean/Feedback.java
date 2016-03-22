package com.will.ontheroad.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Will on 2016/2/28.
 */
public class Feedback extends BmobObject{
    private String content;
    private String sdkVersion;
    private String model;
    private String androidVersion;
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return content;
    }
    public void setSdkVersion(String sdkVersion){
        this.sdkVersion = sdkVersion;
    }
    public String getSdkVersion(){
        return sdkVersion;
    }
    public void setModel(String model){
        this.model = model;
    }
    public String getModel(){
        return model;
    }
    public void setAndroidVersion(String androidVersion){
        this.androidVersion = androidVersion;
    }
    public String getAndroidVersion(){
        return androidVersion;
    }
}
