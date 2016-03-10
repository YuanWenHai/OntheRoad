package com.will.ontheroad.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.will.ontheroad.R;
import com.will.ontheroad.adapter.BaseAdapterHelper;
import com.will.ontheroad.adapter.MyQuickAdapter;
import com.will.ontheroad.bean.Diary;
import com.will.ontheroad.bean.Goal;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.popup.QuickPopup;
import com.will.ontheroad.utility.DownloadImageListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Will on 2016/3/4.
 */
public class GoalActivity extends BaseActivity implements View.OnClickListener{
    private Goal myGoal;
    private MyQuickAdapter<Diary> adapter;
    private ListView listView;
    private MyUser user;
    private String goalId;
    private final int NETWORK_FIRST = 0;
    private final int CACHE_FIRST = 1;
    private RelativeLayout loading;
    private List<Diary> list;
    private int position;
    private QuickPopup editPopup;
    private QuickPopup goalPopup;
    private boolean refreshMain;
    private boolean order = true;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_page);
        user = BmobUser.getCurrentUser(this, MyUser.class);
        goalId = getIntent().getStringExtra("goal");
        initializeAdapter();
        initializeViews();
        queryGoal(goalId);
        showToast("onCreate");
        //queryDiary();
    }
    private void queryGoal(String goalId){
        BmobQuery<Goal> query = new BmobQuery<>();
        query.getObject(this, goalId, new GetListener<Goal>() {
            @Override
            public void onSuccess(Goal goal) {
                myGoal = goal;
                queryDiary();
            }

            @Override
            public void onFailure(int i, String s) {
                showToast(s);
            }
        });
    }
    private void queryDiary(){
        BmobQuery<Diary> query = new BmobQuery<>();
        query.addWhereEqualTo("goalId", goalId);
        if(order) {
            query.order("-createdAt");
        }else{
            query.order("+createdAt");
        }
        query.findObjects(this, new FindListener<Diary>() {
            @Override
            public void onSuccess(List<Diary> list) {
                GoalActivity.this.list = list;
                adapter.clear();
                adapter.addAll(list);
                listView.setAdapter(adapter);
                showToast("queryDiary success");
                loading.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(int i, String s) {
                showToast(s);
            }
        });
    }
    private void initializeAdapter(){
        adapter = new MyQuickAdapter<Diary>(this,R.layout.goal_page_list_item) {
            @Override
            protected void onFirstItem(final ImageView image, TextView name, TextView presentation) {
                name.setText(myGoal.getName());
                presentation.setText(myGoal.getPresentation());
                downloadImage(GoalActivity.this, myGoal.getImageThumbnail(), new DownloadImageListener() {
                    @Override
                    public void onSuccess(String localImagePath) {
                       image.setImageDrawable(Drawable.createFromPath(localImagePath));
                    }
                });
            }
            @Override
            protected void convert(final BaseAdapterHelper helper, Diary item) {
                helper.setText(R.id.goal_page_list_item_content,item.getContent())
                        .setText(R.id.goal_page_list_item_name, user.getUserName())
                        .setText(R.id.goal_page_list_item_time, item.getCreatedAt());
                helper.getView(R.id.goal_page_list_item_more).setOnClickListener(GoalActivity.this);
                helper.getView(R.id.goal_page_list_item_more).setTag(helper.getPosition());
                downloadImage(GoalActivity.this, user.getUserImageThumbnail(), new DownloadImageListener() {
                    @Override
                    public void onSuccess(String localImagePath) {
                        helper.setImageDrawable(R.id.goal_page_list_item_image, Drawable.createFromPath(localImagePath));
                    }
                });if(item.getImage() != null){
                downloadImage(GoalActivity.this, item.getImage(), new DownloadImageListener() {
                    @Override
                    public void onSuccess(String localImagePath) {
                        helper.setImageDrawable(R.id.goal_page_content_image,Drawable.createFromPath(localImagePath));
                        helper.getView(R.id.goal_page_content_image).setVisibility(View.VISIBLE);
                    }
                });
                }else{
                    helper.getView(R.id.goal_page_content_image).setVisibility(View.GONE);
                }
            }
        };
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.goal_page_bar_add://工具栏-添加日志
                Intent intent = new Intent(this,AddDiaryActivity.class);
                intent.putExtra("goal", myGoal.getObjectId());
                startActivity(intent);
                break;
            case R.id.goal_page_list_item_more://日志栏-更多
                View view = View.inflate(this,R.layout.popup_edit_diary,null);
                TextView edit = (TextView) view.findViewById(R.id.goal_page_popup_edit);
                TextView delete = (TextView) view.findViewById(R.id.goal_page_popup_delete);
                edit.setOnClickListener(this);
                delete.setOnClickListener(this);
                editPopup = new QuickPopup(view,dpToPx(this,200),dpToPx(this,50));
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                editPopup.showAtLocation(listView, Gravity.NO_GRAVITY, location[0] - dpToPx(this, 200), location[1]);
                position = (int) v.getTag();
                break;
            case R.id.goal_page_popup_edit://编辑日志
                editPopup.dismiss();
                Intent toAddDiary = new Intent(this,AddDiaryActivity.class);
                String imagePath = list.get(position).getImage();
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
                Diary diary = new Diary();
                diary.setObjectId(list.get(position).getObjectId());
                diary.delete(this, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        editPopup.dismiss();
                        showToast("已删除");
                        queryDiary();
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        showToast(s);
                    }
                });
                break;
            case R.id.goal_page_bar_back://工具栏-返回
                onBackPressed();
                break;
            case R.id.goal_page_bar_more://工具栏-更多
                View editGoalView = View.inflate(this,R.layout.popup_edit_goal,null);
                TextView editGoal = (TextView) editGoalView.findViewById(R.id.goal_page_popup_edit_goal);
                TextView deleteGoal = (TextView) editGoalView.findViewById(R.id.goal_page_popup_delete_goal);
                TextView orderGoal = (TextView) editGoalView.findViewById(R.id.goal_page_popup_order_goal);
                editGoal.setOnClickListener(this);
                deleteGoal.setOnClickListener(this);
                orderGoal.setOnClickListener(this);
                goalPopup = new QuickPopup(editGoalView,dpToPx(this,100),dpToPx(this,120));
                goalPopup.showAtLocation(listView,Gravity.END|Gravity.TOP,0,dpToPx(this,50)+getStateBarHeight());
                break;
            case R.id.goal_page_popup_edit_goal://编辑目标
                goalPopup.dismiss();
                Intent toAddGoal = new Intent(this,AddGoalActivity.class);
                toAddGoal.putExtra("objectId",myGoal.getObjectId());
                toAddGoal.putExtra("edit",true);
                toAddGoal.putExtra("title",myGoal.getName());
                toAddGoal.putExtra("content",myGoal.getPresentation());
                toAddGoal.putExtra("image",myGoal.getImageThumbnail());
                toAddGoal.putExtra("become", myGoal.getBecome());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                toAddGoal.putExtra("date", format.format(myGoal.getAchievementDate()));
                startActivity(toAddGoal);
                break;
            case R.id.goal_page_popup_delete_goal://删除目标及其日志
                goalPopup.dismiss();
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
                                finish();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int i = 0;
                                        List<BmobObject> diaries = new ArrayList<>();
                                        Diary diary;
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
                                                public void onSuccess() {}
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
                break;
            case R.id.goal_page_popup_order_goal://日志排序
                goalPopup.dismiss();
                if(order){
                    order = false;
                }else{
                    order = true;
                }
                queryDiary();
                break;
        }
    }
    @Override
    public void onNewIntent(Intent intent){
        if(intent.getStringExtra("goal") != null){
            goalId = intent.getStringExtra("goal");
        }
        if(intent.getBooleanExtra("refresh",false)){
            queryGoal(goalId);
            refreshMain = true;
        }
    }
    private void initializeViews(){
        listView = (ListView) findViewById(R.id.goal_page_list_view);
        loading = (RelativeLayout) findViewById(R.id.goal_page_loading);
        Button back = (Button) findViewById(R.id.goal_page_bar_back);
        Button statistic = (Button) findViewById(R.id.goal_page_bar_statistic);
        Button add = (Button) findViewById(R.id.goal_page_bar_add);
        Button more = (Button) findViewById(R.id.goal_page_bar_more);
        back.setOnClickListener(this);
        statistic.setOnClickListener(this);
        add.setOnClickListener(this);
        more.setOnClickListener(this);
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,MainActivity.class);
        if(refreshMain){
            intent.putExtra("refresh_goal",true);
        }
        startActivity(intent);
        finish();
    }
}

