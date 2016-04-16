package com.will.ontheroad.photo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.will.ontheroad.R;
import com.will.ontheroad.bean.PhotoBean;

/**
 * Created by Will on 2016/4/15.
 */
public class PhotoFragment extends Fragment implements PhotoRecyclerAdapter.PhotoClickListener,
        PhotoRecyclerAdapter.LoadingCallback,SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    private PhotoRecyclerAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private boolean isRefresh = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.frament_photo_page,null);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_photo_swipe_refresh);
        refreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_recycler_list);
        adapter = new PhotoRecyclerAdapter(getContext());
        adapter.setOnItemClickListener(this);
        manager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        //LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        onRefresh();
        //refreshLayout.setRefreshing(true);
        return view;
    }

    public void onRefresh(){
        if(isConnected()){
            adapter.refreshData(this);
            adapter.setIsShowFooter(true);
        }else{
            Toast.makeText(getContext(),"无可用网络连接",Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
            adapter.setIsShowFooter(false);
        }
    }
    public void onLoadingFinish(){
        refreshLayout.setRefreshing(false);
    }
    public void onPhotoClick(PhotoBean bean){
        Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
        intent.putExtra("id",bean.getSetid());
        startActivity(intent);
    }
    private boolean isConnected(){
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null &&info.isConnected());
    }
}
