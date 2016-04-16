package com.will.ontheroad.bean;

import java.io.Serializable;

/**
 * Created by Will on 2016/4/15.
 */
public class PhotoBean  implements Serializable{
    private String desc;
    private String cover;
    private String datatime;
    private String setid;
    private String imgsum;
    private String setname;
    private String tcover;
    public void setDesc(String desc){
        this.desc = desc;
    }
    public String getDesc(){
        return desc;
    }
    public void setCover(String cover){
        this.cover = cover;
    }
    public String getCover(){
        return cover;
    }
    public void setDataTime(String datatime){
        this.datatime = datatime;
    }
    public String getDataTime(){
        return datatime;
    }
    public void setSetId(String setid){
        this.setid = setid;
    }
    public String getSetid(){
        return setid;
    }
    public void setImgSum(String imgsum){
        this.imgsum = imgsum;
    }
    public String getImgSum(){
        return imgsum;
    }
    public void setSetName(String setname){
        this.setname = setname;
    }
    public String getSetName(){
        return setname;
    }
    public void setTCover(String tcover){
        this.tcover = tcover;
    }
    public String getTCover(){
        return tcover;
    }
}
