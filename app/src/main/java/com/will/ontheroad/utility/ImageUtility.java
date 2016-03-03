package com.will.ontheroad.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Will on 2016/3/2.
 */
public class ImageUtility {

    public void downloadImage( Context context, final String url, final DownloadImageListener listener){
        final String dir = context.getFilesDir()+"/thumbnail";
        final SharedPreferences sp = context.getSharedPreferences("cache",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        if(sp.contains(url)){
            listener.onSuccess(sp.getString(url,""));
            Log.e("getCache","execute");
        }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        URL imageUrl;
                        InputStream is = null;
                        FileOutputStream os  = null;
                        try{
                            imageUrl = new URL(url);
                            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                            connection.setConnectTimeout(5000);
                            connection.setRequestMethod("GET");
                            is = connection.getInputStream();
                            File file = new File(dir);
                            if(!file.exists()){
                                file.mkdir();
                            }
                            File image = new File(dir+"/"+url.replaceAll("/",""));
                            os = new FileOutputStream(image);
                            int len = 0;
                            byte[] bytes= new byte[1024];
                            while((len=is.read(bytes)) > 0){
                                os.write(bytes,0,len);
                            }
                            os.flush();
                            editor.putString(url, image.getPath());
                            editor.apply();
                            listener.onSuccess(image.getPath());
                        }catch (IOException e){
                            e.printStackTrace();
                            Log.e("downloadImage", "error");
                        }finally{
                            try{
                                if(is != null){
                                    is.close();
                                }
                                if(os != null){
                                    os.close();
                                }
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
        }
    }
    /*public void deleteImage(String imageUrl){
        if(sp.contains(imageUrl)) {
            File file = new File(sp.getString(imageUrl, ""));
            file.delete();
        }
            BmobFile bmobFile = new BmobFile();
            bmobFile.setUrl(imageUrl);
            bmobFile.delete(context);
        }*/
    }

