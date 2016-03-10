package com.will.ontheroad.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.will.ontheroad.R;
import com.will.ontheroad.bean.Diary;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.utility.DownloadImageListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by Will on 2016/3/4.
 */
public abstract class MySimpleAdapter extends BaseAdapter {
    List<Diary> data = new ArrayList<>();
    List<String> firstItemData = new ArrayList<>();
    View firstView;
    Context context;
    ImageView goalImageView;
    TextView goalNameTextView;
    TextView goalPresentationTextView;
    String userName;
    String userImage;
    public MySimpleAdapter(Context context){
        this.context = context;
        MyUser user = BmobUser.getCurrentUser(context, MyUser.class);
        userName = user.getUserName();
        userImage = user.getUserImageThumbnail();
    }
    public void setListData(List<Diary> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }
    public void setFirstItemData(String name,String image,String presentation){
        firstItemData.clear();
        firstItemData.add(name);
        firstItemData.add(image);
        firstItemData.add(presentation);
        notifyDataSetChanged();
    }
    @Override
    public int getCount(){
        return data == null? 1:data.size()+1;
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        if(position == 0 ){
            if(firstView == null){
                firstView = View.inflate(context, R.layout.goal_page_list_item_first,null);
                goalImageView = (ImageView) firstView.findViewById(R.id.goal_page_image_view);
                goalNameTextView = (TextView) firstView.findViewById(R.id.goal_page_goal_name);
                goalPresentationTextView = (TextView) firstView.findViewById(R.id.goal_page_goal_presentation);
                downloadImage(context, firstItemData.get(1), new DownloadImageListener() {
                    @Override
                    public void onSuccess(String localImagePath) {
                        goalImageView.setImageDrawable(Drawable.createFromPath(localImagePath));
                    }
                });
                goalNameTextView.setText(firstItemData.get(0));
                goalPresentationTextView.setText(firstItemData.get(2));
            }else{
                downloadImage(context, firstItemData.get(1), new DownloadImageListener() {
                    @Override
                    public void onSuccess(String localImagePath) {
                        goalImageView.setImageDrawable(Drawable.createFromPath(localImagePath));
                    }
                });
                goalNameTextView.setText(firstItemData.get(0));
                goalPresentationTextView.setText(firstItemData.get(2));
            }
            return firstView;
        }else{
            ViewHolder holder;
            if(convertView == null){
                convertView = View.inflate(context,R.layout.goal_page_list_item,null);
                holder = new ViewHolder();
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.goal_page_list_item_image);
                holder.content = (TextView) convertView.findViewById(R.id.goal_page_list_item_content);
                holder.more = (Button) convertView.findViewById(R.id.goal_page_list_item_more);
                holder.updateTime = (TextView) convertView.findViewById(R.id.goal_page_list_item_time);
                holder.userName = (TextView) convertView.findViewById(R.id.goal_page_list_item_name);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.userName.setText(userName);
            holder.thumbnail.setImageDrawable(Drawable.createFromPath(userImage));
            holder.updateTime.setText(data.get(position - 1).getUpdatedAt());
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoreClick(v);
                }
            });
            holder.content.setText(data.get(position - 1).getContent());
            return  convertView;
        }
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public Diary getItem(int position){
        return data.get(position-1);
    }
    @Override
    public int getItemViewType(int position){
        return position == 0 ? 1:0;
    }
    class ViewHolder{
        ImageView thumbnail;
        TextView userName;
        TextView updateTime;
        Button more;
        TextView content;
    }
    private String convertTime(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(date);
    }
    public abstract void onMoreClick(View v);
    public  void downloadImage( Context context, final String url, final DownloadImageListener listener){
        final String dir = context.getFilesDir()+"/thumbnail";
        final SharedPreferences sp = context.getSharedPreferences("cache",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        if(sp.contains(url)){
            listener.onSuccess(sp.getString(url, ""));
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
                        final File image = new File(dir+"/"+url.replaceAll("/",""));
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
}
