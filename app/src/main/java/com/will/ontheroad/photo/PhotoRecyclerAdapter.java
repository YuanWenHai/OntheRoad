package com.will.ontheroad.photo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.bean.PhotoBean;
import com.will.ontheroad.constant.Constant;
import com.will.ontheroad.utility.OkHttpUtil;

import java.util.ArrayList;

/**
 * Created by Will on 2016/4/15.
 */
public class PhotoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_REFRESH = 0;
    private static final int TYPE_LOADMORE = 1;
    private static final int TYPE_ITEM = 2;
    private static final int TYPE_FOOTER = 3;
    private String lastItemId;
    private ArrayList<PhotoBean> data = new ArrayList<>();
    private Context context;
    private PhotoClickListener listener;
    private boolean isShowFooter = true;
    public PhotoRecyclerAdapter(Context context){
        this.context = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(viewType == TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item,null);
            return new PhotoViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_footer,null);
            return new FooterViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if(holder instanceof PhotoViewHolder) {
            ((PhotoViewHolder)holder).name.setText(data.get(position).getSetName());
            ((PhotoViewHolder)holder).count.setText("Pic:"+data.get(position).getImgSum());
            Picasso.with(context).load(data.get(position).getCover()).placeholder(R.drawable.loading_image).
                    error(R.drawable.noimgavailable).into(((PhotoViewHolder)holder).image);
        }else{
            loadMore();
        }
    }
    @Override
    public int getItemCount(){
        return data.size()+(isShowFooter?1:0);
    }
    @Override
    public int getItemViewType(int position){
        if(!isShowFooter){
            return TYPE_ITEM;
        }
        if(position+1 ==getItemCount()){
            return TYPE_FOOTER;
        }else{
            return TYPE_ITEM;
        }
    }
    public void refreshData(final LoadingCallback callback){
        OkHttpUtil.get(getUrl(TYPE_REFRESH), new OkHttpUtil.Callback() {
            @Override
            public void onResult(String result) {
                data.clear();
                data.addAll(PhotoJsonUtil.readJsonPhotoBeans(result));
                lastItemId = data.get(data.size()-1).getSetid();
                callback.onLoadingFinish();
                notifyDataSetChanged();
            }
        });
    }

    public void loadMore(){
        OkHttpUtil.get(getUrl(TYPE_LOADMORE), new OkHttpUtil.Callback() {
            @Override
            public void onResult(String result) {
                data.addAll(PhotoJsonUtil.readJsonPhotoBeans(result));
                lastItemId = data.get(data.size()-1).getSetid();
                notifyDataSetChanged();
            }
        });
    }
    private String getUrl(int type){
        if(type == TYPE_REFRESH){
            return Constant.NEW_IMAGE;
        }else{
            return Constant.MORE_IMAGE+lastItemId+Constant.IMAGE_END;
        }
    }
    public void setIsShowFooter(boolean which){
        isShowFooter = which;
    }
    public void setOnItemClickListener(PhotoClickListener listener){
        this.listener = listener;
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }
    }
    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name;
        public ImageView image;
        public TextView count;
        public PhotoViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.photo_list_item_name);
            image = (ImageView) itemView.findViewById(R.id.photo_list_item_image);
            count = (TextView) itemView.findViewById(R.id.photo_list_item_image_count);
        }
        @Override
        public void onClick(View v){
            if(listener != null){
                listener.onPhotoClick(data.get(getAdapterPosition()));
            }
        }
    }
    interface PhotoClickListener{
        void onPhotoClick(PhotoBean bean);
    }
    interface LoadingCallback{
        void onLoadingFinish();
    }
}
