package com.will.ontheroad.activities;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
import com.will.ontheroad.R;
import com.will.ontheroad.adapter.MyRecyclerAdapter;
import com.will.ontheroad.adapter.QuickAdapter;
import com.will.ontheroad.bean.Diary;
import com.will.ontheroad.bean.Goal;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.fragment.ImageFragment;
import com.will.ontheroad.popup.QuickPopup;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Will on 2016/3/4.
 */
public class GoalActivity extends BaseActivity implements View.OnClickListener{
    private MyUser user;
    private String goalId;
    private String goalName;
    private String goalImage;
    private String goalPresentation;
    private String become;
    private String achievementDate;
    private String createdAt;
    private String fullImage;
    private Intent receivedIntent;
    private ScaleInAnimationAdapter scaleAdapter;
    private RelativeLayout loadingPage;
    private List<Diary> list = new ArrayList<>();
    private int position;
    private QuickPopup editPopup;
    private boolean refreshMain;
    private boolean order = true;
    private RecyclerView recyclerView;
    private MyRecyclerAdapter recyclerAdapter;
    private boolean ended;
    private ImageFragment fragment;
    private AVLoadingIndicatorView loading;
    private QuickAdapter<Diary> quickAdapter;
    private ImageView goalImageView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //initializeTransitions();
        setContentView(R.layout.goal_page);
        //ViewCompat.setTransitionName(findViewById(R.id.appbar_layout),"image");
        //supportPostponeEnterTransition();
        user = BmobUser.getCurrentUser(this, MyUser.class);
        receivedIntent = getIntent();
        queryGoal();
        initializeViews();
        queryDiary();
    }
    private void queryGoal(){
        goalId = receivedIntent.getStringExtra("goal");
        goalName = receivedIntent.getStringExtra("name");
        goalImage = receivedIntent.getStringExtra("image");
        goalPresentation = receivedIntent.getStringExtra("presentation");
        become = receivedIntent.getStringExtra("become");
        achievementDate = receivedIntent.getStringExtra("date");
        createdAt = receivedIntent.getStringExtra("created_at");
        fullImage =receivedIntent.getStringExtra("full_image");
    }
     private void loadMore(){
            BmobQuery<Diary> query = new BmobQuery<>();
            query.addWhereEqualTo("goalId", goalId);
            if (order) {
                query.order("-createdAt");
            } else {
                query.order("+createdAt");
            }
            query.setSkip(list.size());
            query.setLimit(20);
            query.findObjects(this, new FindListener<Diary>() {
                @Override
                public void onSuccess(List<Diary> list) {
                    if (list.size() < 20) {
                        ended = true;
                    }
                    GoalActivity.this.list.addAll(list);
                    recyclerAdapter.notifyDataSetChanged();
                    loading.setVisibility(View.GONE);
                }

                @Override
                public void onError(int i, String s) {
                    showToast(s);
                }
            });
        }

    private void queryDiary(){
        ended = false;
        BmobQuery<Diary> query = new BmobQuery<>();
        query.addWhereEqualTo("goalId", goalId);
        if(order) {
            query.order("-createdAt");
        }else{
            query.order("+createdAt");
        }
        query.setLimit(20);
        query.findObjects(this, new FindListener<Diary>() {
            @Override
            public void onSuccess(List<Diary> diaries) {
                list.clear();
                list.addAll(diaries);
                recyclerAdapter.notifyItemRangeChanged(0,list.size());
                //listView.setAdapter(scaleAdapter);
                //loadingPage.setVisibility(View.GONE);
                //listView.setVisibility(View.VISIBLE);
                if (order && diaries.size() > 0) {
                    //获取到diary列表时，将最近的diary更新时间交给所属goal
                    Goal goal = new Goal();
                    goal.setUpdateDate(diaries.get(0).getCreatedAt());
                    goal.update(GoalActivity.this, goalId, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onFailure(int i, String s) {
                            showToast(s);
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast(s);
            }
        });
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.goal_page_popup_edit://编辑日志
                editPopup.dismiss();
                Intent toAddDiary = new Intent(this,AddDiaryActivity.class);
                String imagePath = list.get(position).getImageThumbnal();
                String content = list.get(position).getContent();
                toAddDiary.putExtra("edit",true);
                toAddDiary.putExtra("goal",goalId);
                toAddDiary.putExtra("objectId",list.get(position).getObjectId());
                if(imagePath != null){
                    toAddDiary.putExtra("imagePath",imagePath);
                }
                if(content != null){
                    toAddDiary.putExtra("content",content);
                }
                startActivity(toAddDiary);
                break;
            case R.id.goal_page_popup_delete://删除日志
                editPopup.dismiss();
                Diary diary = new Diary();
                diary.setObjectId(list.get(position).getObjectId());
                diary.delete(this, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        list.remove(position);
                        //quickAdapter.clear();
                        //quickAdapter.addAll(list);
                        recyclerAdapter.notifyItemRemoved(position);
                        recyclerAdapter.notifyDataSetChanged();
                        showToast("已删除");
                        //queryDiary();
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        showToast(s);
                    }
                });
                break;
            case R.id.shared_image:
                if(fullImage != null){
                fragment = new ImageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("path",fullImage);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container,fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
        }else{
                    showToast("你起码得先设置个图啊摔!");
                }
                break;
            case R.id.goal_page_content_image:
                int index = (int)v.getTag();
                if(list.get(index).getImage() != null){
                    Bundle bundle = new Bundle();
                    bundle.putString("path",list.get(index).getImage());
                    if(fragment == null){
                        fragment = new ImageFragment();
                    }
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().add(R.id.fragment_container,fragment).commit();
                }
                break;
        }
    }
    @Override
    public void onNewIntent(Intent intent){
        if(intent.getBooleanExtra("refresh",false)){
            BmobQuery<Goal> query = new BmobQuery<>();
            query.getObject(this, goalId, new GetListener<Goal>() {
                @Override
                public void onSuccess(Goal goal) {
                    goalName = goal.getName();
                    goalImage = goal.getImageThumbnail();
                    goalPresentation = goal.getPresentation();
                    become = goal.getBecome();
                    queryDiary();
                    initializeHeader();
                }

                @Override
                public void onFailure(int i, String s) {
                    showToast(s);
                }
            });
        }else {
            receivedIntent = intent;
        }
        refreshMain = true;
    }
    private void initializeViews(){
        initializeRecyclerView();
        appBarLayout = (AppBarLayout)findViewById(R.id.appbar_layout);
        loading = (AVLoadingIndicatorView) findViewById(R.id.loading);
        loadingPage = (RelativeLayout) findViewById(R.id.loading_page);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        goalImageView = (ImageView) findViewById(R.id.shared_image);
        goalImageView.setOnClickListener(this);
        initializeHeader();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    private void initializeHeader(){
        collapsingToolbarLayout.setTitle(goalName);
        if(goalImage != null){
        Picasso.with(this).load(goalImage).into(goalImageView, new Callback() {
            @Override
            public void onSuccess() {
                Palette.from(((BitmapDrawable) goalImageView.getDrawable()).getBitmap())
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                collapsingToolbarLayout.setContentScrimColor(palette.getDarkMutedColor(Color.BLACK));
                                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(Color.BLACK));
                            }
                        });

            }
            @Override
            public void onError() {}
        });
        }else{
            goalImageView.setImageResource(R.drawable.noimgavailable);
            collapsingToolbarLayout.setContentScrimColor(Color.BLACK);
            collapsingToolbarLayout.setStatusBarScrimColor(Color.BLACK);
        }
    }
    @Override
    public void onBackPressed(){
        if(fragment!= null && fragment.isVisible()){
            getFragmentManager().beginTransaction().remove(fragment).commit();
            appBarLayout.setVisibility(View.VISIBLE);
        }else{
        if(refreshMain){
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("refresh_goal", true);
            startActivity(intent);
            finish();
        }else {
            super.onBackPressed();
        }
    }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.goal_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.goal_page_toolbar_add://增加日志
                Intent intent = new Intent(this,AddDiaryActivity.class);
                intent.putExtra("goal", goalId);
                startActivity(intent);
                return true;
            case R.id.goal_page_toolbar_delete://删除目标及其日志
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("确定删除?执行后将无法恢复");
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Goal goal = new Goal();
                        goal.setObjectId(goalId);
                        goal.delete(GoalActivity.this, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                showToast("已删除");
                                Intent toMainActivity = new Intent(GoalActivity.this,MainActivity.class);
                                toMainActivity.putExtra("refresh_goal",true);
                                startActivity(toMainActivity);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int i = 0;
                                        List<BmobObject> diaries = new ArrayList<>();
                                        while(i<list.size()){
                                            int a = 0;
                                            diaries.clear();
                                            while (a++ < 50){
                                                diaries.add(list.get(i++));
                                                if( i == list.size()){
                                                    break;
                                                }
                                            }
                                            new BmobObject().deleteBatch(GoalActivity.this, diaries, new DeleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    finish();
                                                }
                                                @Override
                                                public void onFailure(int i, String s) {
                                                    showToast(s);
                                                }
                                            });

                                        }
                                    }
                                }).start();
                            }
                            @Override
                            public void onFailure(int i, String s) {
                                showToast(s);
                            }
                        });
                    }
                }).create().show();
                return true;
            case R.id.goal_page_toolbar_edit://编辑目标
                Intent toAddGoal = new Intent(this,AddGoalActivity.class);
                toAddGoal.putExtra("objectId",goalId);
                toAddGoal.putExtra("edit",true);
                toAddGoal.putExtra("title",goalName);
                toAddGoal.putExtra("content",goalPresentation);
                toAddGoal.putExtra("image",goalImage);
                toAddGoal.putExtra("become", become);
                toAddGoal.putExtra("date", achievementDate);
                startActivity(toAddGoal);
                return true;
            case R.id.goal_page_toolbar_order:
                if(order){
                    order = false;
                }else{
                    order = true;
                }
                queryDiary();
                return true;
            case R.id.goal_page_toolbar_statistic:
                Intent toStatistic = new Intent(this,StatisticActivity.class);
                toStatistic.putExtra("created_at",createdAt);
                toStatistic.putExtra("become",become);
                toStatistic.putExtra("achievement_date",achievementDate);
                startActivity(toStatistic);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initializeRecyclerView(){
        recyclerView =(RecyclerView) findViewById(R.id.goal_page_recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerAdapter = new MyRecyclerAdapter(this,list,BmobUser.getCurrentUser(this,MyUser.class));
        recyclerAdapter.setOnMoreClickListener(new MyRecyclerAdapter.MoreClickedListener() {
            @Override
            //item上more按钮的点击事件
            public void onMoreClicked(View v, int position) {
                View view = View.inflate(GoalActivity.this,R.layout.popup_edit_diary,null);
                TextView edit = (TextView) view.findViewById(R.id.goal_page_popup_edit);
                TextView delete = (TextView) view.findViewById(R.id.goal_page_popup_delete);
                edit.setOnClickListener(GoalActivity.this);
                delete.setOnClickListener(GoalActivity.this);
                editPopup = new QuickPopup(view,dpToPx(GoalActivity.this,200),dpToPx(GoalActivity.this,50));
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                editPopup.setAnimationStyle(R.style.scaleAnimation);
                editPopup.showAtLocation(recyclerView, Gravity.NO_GRAVITY, location[0] - dpToPx(GoalActivity.this, 200), location[1]);
                GoalActivity.this.position = position;
            }
            @Override
        public void onImageClicked(View v ,int position){
                if(list.get(position).getImage() != null){
                    Bundle bundle = new Bundle();
                    bundle.putString("path", list.get(position).getImage());
                    if(fragment == null){
                        fragment = new ImageFragment();
                    }
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().add(R.id.fragment_container,fragment).commit();
                    appBarLayout.setVisibility(View.GONE);
                }
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == totalItemCount - 1) {
                        if (!ended) {
                            loading.setVisibility(View.VISIBLE);
                            loadMore();
                        } else {
                            //showToast("已经到最后了");
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(recyclerAdapter);
    }
    private void initializeTransitions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Slide transition = new Slide();
            getWindow().setEnterTransition(transition);
            //getWindow().setReturnTransition(transition);
        }
    }

}

