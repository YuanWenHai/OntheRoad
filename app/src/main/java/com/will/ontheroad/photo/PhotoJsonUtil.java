package com.will.ontheroad.photo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.will.ontheroad.bean.PhotoBean;
import com.will.ontheroad.bean.PhotoDetailBean;
import com.will.ontheroad.bean.PhotoDetailImage;
import com.will.ontheroad.utility.JsonUtils;

import java.util.ArrayList;


/**
 * Created by Will on 2016/4/15.
 */
public class PhotoJsonUtil {
    public static ArrayList<PhotoBean> readJsonPhotoBeans(String res){
        ArrayList<PhotoBean> data = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(res).getAsJsonArray();
        for(JsonElement element :array){
            JsonObject object = element.getAsJsonObject();
            PhotoBean bean = JsonUtils.deserialize(object,PhotoBean.class);
            data.add(bean);
        }
        return data;
    }
    public static PhotoDetailBean readJsonPhotoDetailbean(String res){
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(res).getAsJsonObject();
        JsonElement element= object.get("photos");
        JsonArray imageArray = element.getAsJsonArray();
        ArrayList<PhotoDetailImage> list = new ArrayList<>();
        for(JsonElement e :imageArray){
            JsonObject o = e.getAsJsonObject();
            PhotoDetailImage imgurl = JsonUtils.deserialize(o,PhotoDetailImage.class);
            list.add(imgurl);
        }
        PhotoDetailBean bean = JsonUtils.deserialize(object,PhotoDetailBean.class);
        bean.getPhotos().addAll(list);
        return bean;
    }public static ArrayList<PhotoDetailImage> readJsonPhotoDetailImage(String res){
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(res).getAsJsonObject();
        JsonElement element= object.get("photos");
        JsonArray imageArray = element.getAsJsonArray();
        ArrayList<PhotoDetailImage> list = new ArrayList<>();
        for(JsonElement e :imageArray){
            JsonObject o = e.getAsJsonObject();
            PhotoDetailImage imgurl = JsonUtils.deserialize(o,PhotoDetailImage.class);
            list.add(imgurl);
        }
        return list;
    }

}
