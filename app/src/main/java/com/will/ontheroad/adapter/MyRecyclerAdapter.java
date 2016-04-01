package com.will.ontheroad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.bean.Diary;
import com.will.ontheroad.bean.MyUser;

import java.util.List;

/**
 * Created by Will on 2016/3/29.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private List<Diary> data;
    private MyUser user;
    private Context context;
    private MoreClickedListener listener;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView userImage;
        public TextView userName;
        public TextView createdAt;
        public Button more;
        public TextView textContent;
        public ImageView imageContent;
        public ViewHolder(View v){
            super(v);
            userImage = (ImageView) v.findViewById(R.id.goal_page_list_item_image);
            userName = (TextView) v.findViewById(R.id.goal_page_list_item_name);
            createdAt = (TextView) v.findViewById(R.id.goal_page_list_item_time);
            more = (Button) v.findViewById(R.id.goal_page_list_item_more);
            textContent = (TextView)  v.findViewById(R.id.goal_page_list_item_content);
            imageContent = (ImageView) v.findViewById(R.id.goal_page_content_image);
        }
    }
    public MyRecyclerAdapter(Context context, List<Diary> data, MyUser user){
        this.data = data;
        this.user = user;
        this.context = context;
    }
    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v = LayoutInflater.from(context).inflate(R.layout.goal_page_list_item,parent,false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String userImageStr = user.getUserImageThumbnail();
        String userNameStr = user.getUserName();
        String imageContentStr = data.get(position).getImageThumbnal();
        String textContentStr = data.get(position).getContent();
        if(userImageStr != null ){
            Picasso.with(context).load(userImageStr).into(holder.userImage);
        }else{
            holder.userImage.setImageResource(R.drawable.anonymous);
        }
        if(userNameStr != null ){
            holder.userName.setText(userNameStr);
        }else{
            holder.userName.setText("请修改名字");
        }
        if(imageContentStr != null && !imageContentStr.isEmpty()){
            Picasso.with(context).load(imageContentStr).into(holder.imageContent);
            holder.imageContent.setVisibility(View.VISIBLE);
        }else{
            holder.imageContent.setVisibility(View.GONE);
        }
       if(textContentStr != null){
           holder.textContent.setText(textContentStr);
           holder.textContent.setVisibility(View.VISIBLE);
       }else{
           holder.textContent.setVisibility(View.GONE);
       }
        holder.createdAt.setText(data.get(position).getCreatedAt());
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMoreClicked(v, position);
                }
            }
        });
        holder.imageContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageClicked(v,position);
            }
        });
    }
    public interface MoreClickedListener{
        void onMoreClicked(View v,int position);
        void onImageClicked(View v,int position);
    }
    @Override
    public int getItemCount(){
        return  data.size();
    }
    public void setOnMoreClickListener(MoreClickedListener listener){
        this.listener = listener;
    }

}
