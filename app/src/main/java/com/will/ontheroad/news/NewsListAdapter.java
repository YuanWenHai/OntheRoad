package com.will.ontheroad.news;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.bean.NewsBean;
import com.will.ontheroad.constant.Constant;
import com.will.ontheroad.utility.OkHttpUtil;

import java.util.List;

/**
 * Created by Will on 2016/4/14.
 */
public class NewsListAdapter extends BaseAdapter {
    private List<NewsBean> data;
    private boolean showFooter = true;
    private Context context;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private int type;
    private int newsCount = 0;

    /**
     *
     * @param context context
     * @param data List容器
     * @param type 新闻类型
     */
    public NewsListAdapter(Context context,List<NewsBean> data,int type){
        this.context = context;
        this.data = data;
        this.type = type;
    }
    @Override
    public NewsBean getItem(int position){
        return data.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public int getCount(){
        return data.size()+ (showFooter?1:0);
    }
    public int getItemViewType(int position){
        if(!showFooter){
            return TYPE_ITEM;
        }
        if(position+1 == getCount()){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        if(getItemViewType(position) == TYPE_ITEM){
           ViewHolder holder;
            //listView的view复用机制对于type似乎并没有太好的支持，只能在入口通过tag判定view类型
            if(convertView == null || convertView.getTag().getClass() != NewsListAdapter.ViewHolder.class){
                convertView = View.inflate(context, R.layout.news_list_item,null);
                holder = new ViewHolder();
                holder.description = (TextView)convertView.findViewById(R.id.news_list_item_description);
                holder.image = (ImageView) convertView.findViewById(R.id.news_list_item_image);
                holder.title = (TextView) convertView.findViewById(R.id.news_list_item_title);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            Picasso.with(context).load(data.get(position).getImgsrc()).into(holder.image);
            holder.title.setText(data.get(position).getTitle());
            holder.description.setText(data.get(position).getDigest());
            return convertView;
        }else{
            View view = View.inflate(context,R.layout.news_list_footer,null);
            view.setTag("abc");
            getData();
            return view;
        }
    }
    class ViewHolder{
        public ImageView image;
        public TextView title;
        public TextView description;
    }
    public void setData(List<NewsBean> data){
        this.data = data;
    }
    public void addData(List<NewsBean> data){
        this.data.addAll(data);
    }
    public void clearData(){
        data.clear();
    }
    public void setShowFooter(boolean which){
        showFooter = which;
    }
    private String getUrl(int type,int index){
        if(type == Constant.TYPE_HEADLINE){
            return Constant.HEAD_LINE+index+"-"+Constant.NEWS_COUNT+Constant.END;
        }else{
            return Constant.LOCAL+index+"-"+Constant.NEWS_COUNT+Constant.END;
        }
    }
    /**
    *获取新闻list信息
    */
    public void getData(){
        OkHttpUtil.get(getUrl(type,newsCount), new OkHttpUtil.Callback() {
            @Override
            public void onResult(String result) {
                List<NewsBean> data  = NewsJsonUtils.readJsonNewsBeans(result,type);
                addData(data);
                notifyDataSetChanged();
                newsCount += 20;
            }
        });
    }
    private void getData(final ProgressCallback callback){
        OkHttpUtil.get(getUrl(type,newsCount), new OkHttpUtil.Callback() {
            @Override
            public void onResult(String result) {
                List<NewsBean> data  = NewsJsonUtils.readJsonNewsBeans(result,type);
                addData(data);
                notifyDataSetChanged();
                newsCount += 20;
                callback.callback();
            }
        });
    }
    public void refreshData(ProgressCallback callback){
        newsCount = 0;
        clearData();
        getData(callback);
    }
    public interface ProgressCallback{
        void callback();
    }
}
