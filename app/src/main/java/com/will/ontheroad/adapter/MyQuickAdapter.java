package com.will.ontheroad.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.will.ontheroad.R;

import static com.will.ontheroad.adapter.BaseAdapterHelper.get;

/**
 * Created by Will on 2016/3/4.
 */
public abstract  class MyQuickAdapter<T> extends BaseQuickAdapter<T,BaseAdapterHelper>{
    private View view;
    private ImageView image;
    private TextView name;
    private TextView presentation;
    private LinearLayout bg;
    public MyQuickAdapter(Context context,int resourceId){
        super(context,resourceId);
        view = View.inflate(context, R.layout.goal_page_list_item_first,null);
        image = (ImageView) view.findViewById(R.id.goal_page_image_view);
        name = (TextView) view.findViewById(R.id.goal_page_goal_name);
        presentation = (TextView) view.findViewById(R.id.goal_page_goal_presentation);
        bg = (LinearLayout)view.findViewById(R.id.goal_page_first_item_bg);
    }
    protected BaseAdapterHelper getAdapterHelper(int position, View convertView, ViewGroup parent) {
        return get(context, convertView, parent, layoutResId, position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(position ==0){
            onFirstItem(image,name,presentation,bg);
            return view;
        }else{
            final BaseAdapterHelper helper = getAdapterHelper(position-1,convertView,parent);
            helper.setPosition(position-1);
            convert(helper,getItem(position-1));
            return helper.getView();
        }
    }
    @Override
    public int getCount(){
        return data == null ? 1:data.size()+1;
    }
    protected abstract void onFirstItem(ImageView image,TextView name,TextView presentation,LinearLayout bg);

}
