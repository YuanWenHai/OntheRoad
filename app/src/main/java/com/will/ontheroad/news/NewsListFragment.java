package com.will.ontheroad.news;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.will.ontheroad.R;
import com.will.ontheroad.bean.NewsBean;

import java.util.ArrayList;

/**
 * Created by Will on 2016/4/14.
 */
public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener{
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private int type;
    private NewsListAdapter adapter;
    private ArrayList<NewsBean> data = new ArrayList<>();
    public static NewsListFragment getInstance(int type){
        Bundle bundle = new Bundle();
        NewsListFragment fragment = new NewsListFragment();
        bundle.putInt("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view  = inflater.inflate(R.layout.fragment_news_list,container,false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_news_list_swipe_refresh);
        refreshLayout.setOnRefreshListener(this);
        listView = (ListView) view.findViewById(R.id.fragment_news_list_list_view);
        listView.setOnItemClickListener(this);
        adapter = new NewsListAdapter(getActivity(),data,type);
        if (!isConnected()){
            adapter.setShowFooter(false);
            Toast.makeText(getActivity(),"无网络连接",Toast.LENGTH_SHORT).show();
        }
        listView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView,View v,int position,long itemId){
        Intent intent = new Intent(getActivity(),NewsDetailActivity.class);
        intent.putExtra("id",data.get(position).getDocid());
        intent.putExtra("image",data.get(position).getImgsrc());
        startActivity(intent);
    }
    @Override
    public void onRefresh(){
       if(isConnected())
      {
            adapter.refreshData(new NewsListAdapter.ProgressCallback() {
                @Override
                public void callback() {
                    adapter.setShowFooter(true);
                    refreshLayout.setRefreshing(false);
                }
            });
        }else{
            refreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(),"无网络连接",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isConnected(){
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null &&info.isConnected());
    }
}
