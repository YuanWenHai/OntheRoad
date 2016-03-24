package com.will.ontheroad.activities;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
import com.will.ontheroad.R;
import com.will.ontheroad.adapter.BaseAdapterHelper;
import com.will.ontheroad.adapter.MyQuickAdapter;
import com.will.ontheroad.bean.Diary;
import com.will.ontheroad.bean.Goal;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.fragment.ImageFragment;
import com.will.ontheroad.popup.QuickPopup;
import com.will.ontheroad.utility.MyCache;

import java.io.IOException;
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
    private MyQuickAdapter<Diary> adapter;
    private ListView listView;
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
    private List<Diary> list;
    private int position;
    private QuickPopup editPopup;
    private boolean refreshMain;
    private boolean order = true;
    private Toolbar mToolbar;
    private boolean ended;
    private ImageFragment fragment;
    private AVLoadingIndicatorView loading;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_page);
        user = BmobUser.getCurrentUser(this, MyUser.class);
        receivedIntent = getIntent();
        queryGoal();
        initializeViews();
        initializeAdapter();
        //blur(R.drawable.sakura, test);
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
        if(!ended) {
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
                    adapter.addAll(list);
                    loading.setVisibility(View.GONE);
                }

                @Override
                public void onError(int i, String s) {
                    showToast(s);
                }
            });
        }
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
            public void onSuccess(List<Diary> list) {
                GoalActivity.this.list = list;
                adapter.clear();
                adapter.addAll(list);
                listView.setAdapter(scaleAdapter);
                loadingPage.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                if (order && list.size() > 0) {
                    //获取到diary列表时，将最近的diary更新时间交给所属goal
                    Goal goal = new Goal();
                    goal.setUpdateDate(list.get(0).getCreatedAt());
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
    private void initializeAdapter(){
        adapter = new MyQuickAdapter<Diary>(this,R.layout.goal_page_list_item) {
            @Override
            protected void onFirstItem(final ImageView image, TextView name, TextView presentation,LinearLayout bg) {
                image.setOnClickListener(GoalActivity.this);
                if(goalImage != null && !goalImage.isEmpty()){
                    blur(goalImage, bg);
                }
                name.setText(goalName);
                presentation.setText(goalPresentation);
                if(goalImage != null){
                    Picasso.with(GoalActivity.this).load(goalImage).into(image);
                }
            }
            @Override//user信息获取待优化，无需多次获取
            protected void convert(final BaseAdapterHelper helper, Diary item) {
                //Log.e("position",""+helper.getPosition());
                if(helper.getPosition()==list.size()-1 && !ended ){
                    loading.setVisibility(View.VISIBLE);
                    loadMore();
                }
                helper.setText(R.id.goal_page_list_item_name, user.getUserName())
                        .setText(R.id.goal_page_list_item_time, item.getCreatedAt());
                helper.getView(R.id.goal_page_list_item_more).setOnClickListener(GoalActivity.this);
                helper.getView(R.id.goal_page_list_item_more).setTag(helper.getPosition());
                if(!item.getContent().isEmpty()){
                    helper.setText(R.id.goal_page_list_item_content,item.getContent());
                    helper.getView(R.id.goal_page_list_item_content).setVisibility(View.VISIBLE);
                }else{
                    helper.getView(R.id.goal_page_list_item_content).setVisibility(View.GONE);
                }
                if(user.getUserImageThumbnail() != null){
                    helper.setImageUrl(R.id.goal_page_list_item_image,user.getUserImageThumbnail());
                }
                if(item.getImageThumbnal() != null && !item.getImageThumbnal().isEmpty()){
                    helper.setImageUrl(R.id.goal_page_content_image, item.getImageThumbnal());
                    helper.getView(R.id.goal_page_content_image).setVisibility(View.VISIBLE);
                    helper.getView(R.id.goal_page_content_image).setOnClickListener(GoalActivity.this);
                    helper.getView(R.id.goal_page_content_image).setTag(helper.getPosition());
                }else{
                    helper.getView(R.id.goal_page_content_image).setVisibility(View.GONE);
                }
            }
        };
        scaleAdapter = new ScaleInAnimationAdapter(adapter);
        scaleAdapter.setAbsListView(listView);
        //alphaAdapter = new AlphaInAnimationAdapter(adapter);
        //alphaAdapter.setAbsListView(listView);
        //listView.setAdapter(adapter);
        queryDiary();
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.goal_page_list_item_more://日志栏-更多
                View view = View.inflate(this,R.layout.popup_edit_diary,null);
                TextView edit = (TextView) view.findViewById(R.id.goal_page_popup_edit);
                TextView delete = (TextView) view.findViewById(R.id.goal_page_popup_delete);
                edit.setOnClickListener(this);
                delete.setOnClickListener(this);
                editPopup = new QuickPopup(view,dpToPx(this,200),dpToPx(this,50));
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                editPopup.setAnimationStyle(R.style.scaleAnimation);
                editPopup.showAtLocation(listView, Gravity.NO_GRAVITY, location[0] - dpToPx(this, 200), location[1]);
                position = (int) v.getTag();
                break;
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
                        adapter.clear();
                        adapter.addAll(list);
                        showToast("已删除");
                        //queryDiary();
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        showToast(s);
                    }
                });
                break;
            case R.id.goal_page_image_view:
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
                    blur(goalImage, mToolbar);
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
        loading = (AVLoadingIndicatorView) findViewById(R.id.loading);
        listView = (ListView) findViewById(R.id.goal_page_list_view);
        loadingPage = (RelativeLayout) findViewById(R.id.loading_page);
        mToolbar = (Toolbar) findViewById(R.id.goal_page_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("abc");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        blur(goalImage, mToolbar);
        mToolbar.setAlpha(0.95f);
    }
    @Override
    public void onBackPressed(){
        if(fragment!= null && fragment.isVisible()){
            getFragmentManager().beginTransaction().remove(fragment).commit();
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
    private void blur(final String url,final View view) {
        if(url != null){
        Drawable drawable = MyCache.getInstance().getLru().get(url);
        if(drawable == null){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bg = null;
                try {
                    bg = Picasso.with(GoalActivity.this).load(url).get();
                }catch(IOException i){
                    i.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postScale(0.1f,0.1f);
                final Bitmap resizeBitmap = Bitmap.createBitmap(bg,0,0,bg.getWidth(),bg.getHeight(),matrix,true);
                RenderScript rs = RenderScript.create(GoalActivity.this);
                Allocation overlayAlloc = Allocation.createFromBitmap(rs,resizeBitmap);
                ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
                blur.setInput(overlayAlloc);
                blur.setRadius(25f);
                blur.forEach(overlayAlloc);
                overlayAlloc.copyTo(resizeBitmap);
                final Drawable drawable = new BitmapDrawable(getResources(), resizeBitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackground(drawable);
                    }
                });
                MyCache.getInstance().getLru().put(url,drawable);
                rs.destroy();
            }
        }).start();
        }else{
            view.setBackground(drawable);
        }
    }else{
            view.setBackgroundColor(Color.rgb(17,17,17));
        }
    }
}

