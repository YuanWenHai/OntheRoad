package com.will.ontheroad.activities;

import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.will.ontheroad.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will on 2016/3/16.
 */
public class TestActivity extends BaseActivity {
    List<Integer> list;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        //getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ListView test = (ListView) findViewById(R.id.test_list);
        list = new ArrayList<>();
        int i = 0;
        while (i++<20) {
            list.add(i);
        }
        MyAdapter adapter = new MyAdapter();
        test.setAdapter(adapter);
        View view = View.inflate(this,R.layout.test2,null);
        test.addHeaderView(view);
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
    }
    void loadMore(){
        int i = 0;
        while (i++<20) {
            list.add(i);
        }
    }
    class MyAdapter extends BaseAdapter{
        @Override
        public int getCount(){
           return list.size();
        }
        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            if(position>list.size()-5){
                loadMore();
                notifyDataSetChanged();
            }
            ViewHolder holder;
            if(convertView == null){
                convertView = View.inflate(TestActivity.this,R.layout.test_item,null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.test_text);
                convertView.setTag(holder);
            }else{
                holder =(ViewHolder) convertView.getTag();
            }
            holder.textView.setText(list.get(position).toString());
            return convertView;
        }
        @Override
        public long getItemId(int id){
            return id;
        }
        @Override
        public Integer getItem(int position){
            return list.get(position);
        }
    }


    class ViewHolder{
        TextView textView;
    }
}
