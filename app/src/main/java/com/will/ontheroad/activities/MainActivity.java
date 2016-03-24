package com.will.ontheroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.adapter.BaseAdapterHelper;
import com.will.ontheroad.adapter.QuickAdapter;
import com.will.ontheroad.bean.Goal;
import com.will.ontheroad.bean.MyUser;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Will on 2016/2/27.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener
       {
    private QuickAdapter<Goal> goalAdapter;
    private ListView listView;
    private TextView userName;
    private ImageView userImage;
    private LinearLayout userLayout;
    private RelativeLayout noContent;
    private final int CACHE_FIRST = 0;
    private final int NETWORK_FIRST = 1;
    private MyUser user;
    private List<Goal> list;
    private ImageView profileImage;
    private TextView profileName;
    private DrawerLayout drawerLayout;
    private RelativeLayout loadingPage;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.alpha_in, 0);
        setContentView(R.layout.main_page);
        initializeViews();
        queryUser();
        initializeAdapter();
    }
    //请求并显示目标list
    private void queryGoal(int mode){
        BmobQuery<Goal> query = new BmobQuery<>();
        if(mode == CACHE_FIRST){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }
        query.addWhereEqualTo("user", user);
        query.order("-updatedAt");
        query.findObjects(this, new FindListener<Goal>() {
            @Override
            public void onSuccess(List<Goal> goals) {
                loadingPage.setVisibility(View.GONE);
                if(goals.size() == 0){
                    noContent.setVisibility(View.VISIBLE);
                }else{
                    noContent.setVisibility(View.GONE);
                }
                list = goals;
                goalAdapter.clear();
                if (list == null || list.size() == 0) {
                    return;
                }
                goalAdapter.addAll(list);
            }
            @Override
            public void onError(int i, String s) {
            }
        });
    }
    private void initializeViews(){
        loadingPage = (RelativeLayout) findViewById(R.id.main_page_loading);
        noContent = (RelativeLayout) findViewById(R.id.main_page_no_content);
        listView = (ListView) findViewById(R.id.main_page_list);
        userName = (TextView) findViewById(R.id.main_page_toolbar_name);
        userImage = (ImageView) findViewById(R.id.main_page_toolbar_image);
        userLayout = (LinearLayout) findViewById(R.id.main_page_toolbar_user_layout);
        profileImage = (ImageView) findViewById(R.id.profile_page_image);
        profileName = (TextView) findViewById(R.id.profile_page_name);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_page_drawer_layout);
        userLayout.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        profileImage.setOnClickListener(this);
        TextView profileChangePassword = (TextView) findViewById(R.id.profile_page_change_password);
        TextView profileLogout = (TextView) findViewById(R.id.profile_page_logout);
        TextView laboratory = (TextView) findViewById(R.id.profile_page_laboratory);
        TextView aboutMe = (TextView) findViewById(R.id.profile_page_about_me);
        TextView feedback = (TextView) findViewById(R.id.profile_page_feedback);
        profileChangePassword.setOnClickListener(this);
        profileLogout.setOnClickListener(this);
        laboratory.setOnClickListener(this);
        aboutMe.setOnClickListener(this);
        feedback.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        toolbar.setTitle("abc");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void queryUser(){
        user = BmobUser.getCurrentUser(this,MyUser.class);
        if(user == null){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }else{
            if(user.getUserImageThumbnail() != null){
                try{
                    Picasso.with(this).load(user.getUserImageThumbnail()).into(userImage);
                    Picasso.with(this).load(user.getUserImageThumbnail()).into(profileImage);
                }catch (Exception e){}
            }else{
                userImage.setImageResource(R.drawable.anonymous);
                profileImage.setImageResource(R.drawable.anonymous);
            }
            String userNameStr = (String) BmobUser.getObjectByKey(this,"userName");
            if(userNameStr != null){
                userName.setText(userNameStr);
                profileName.setText(userNameStr);
            }else{
                userName.setText("请设置用户名");
                profileName.setText("请设置用户名");
            }
        }
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.main_page_toolbar_user_layout:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.profile_page_image:
                startActivity(new Intent(this, UserInformationActivity.class));
                break;
            case R.id.profile_page_change_password:
                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                break;
            case R.id.profile_page_laboratory:
                showToast("开发中```");
                break;
            case R.id.profile_page_about_me:
                startActivity(new Intent(this,AboutMe.class));
                break;
            case R.id.profile_page_logout:
                BmobUser.logOut(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.profile_page_feedback:
                startActivity(new Intent(this,FeedbackActivity.class));
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent,View view ,int position,long id){
        Intent intent  = new Intent(this,GoalActivity.class);
        intent.putExtra("goal",list.get(position).getObjectId());
        intent.putExtra("name",list.get(position).getName());
        intent.putExtra("image", list.get(position).getImageThumbnail());
        intent.putExtra("presentation", list.get(position).getPresentation());
        intent.putExtra("become", list.get(position).getBecome());
        intent.putExtra("date", list.get(position).getAchievementDate());
        intent.putExtra("created_at",list.get(position).getCreatedAt());
        intent.putExtra("full_image",list.get(position).getImageFileName());
        startActivity(intent);
    }
    private void initializeAdapter() {
        if (goalAdapter == null) {
            goalAdapter = new QuickAdapter<Goal>(this, R.layout.main_page_list_item) {
                @Override
                protected void convert(final BaseAdapterHelper helper, final Goal goal) {
                    helper.setText(R.id.main_page_item_goal, goal.getName());
                    if(goal.getPresentation() != null){
                        helper.setText(R.id.main_page_item_presentation,goal.getPresentation());
                        helper.getView(R.id.main_page_item_presentation).setVisibility(View.VISIBLE);
                    }else{
                        helper.getView(R.id.main_page_item_presentation).setVisibility(View.GONE);
                    }
                    if(goal.getUpdateDate()!=null) {
                        helper.setText(R.id.main_page_item_last_update_date, "上次更新：" + goal.getUpdateDate());
                    }else{
                        helper.setText(R.id.main_page_item_last_update_date,"尚未更新");
                    }
                    if(goal.getImageThumbnail() !=null){
                        helper.setImageUrl(R.id.main_page_item_image,goal.getImageThumbnail());
                    }else{
                        helper.setImageResource(R.id.main_page_item_image, R.drawable.noimgavailable);
                    }
                }
            };
            listView.setAdapter(goalAdapter);
        }
        queryGoal(CACHE_FIRST);
    }

    @Override
    protected void onNewIntent(Intent intent){
        if(intent.getBooleanExtra("refresh_goal",false)){
            queryGoal(NETWORK_FIRST);
        }else if(intent.getBooleanExtra("refresh_user",false)){
            queryUser();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.main_page_toolbar_note:
                return true;
            case R.id.main_page_toolbar_add:
                startActivity(new Intent(this,AddGoalActivity.class));
                return true;
            default:
               return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawer(Gravity.LEFT);
        }else if (toggle){
            showToast("再按一次退出");
            delay(3000);
        }else{
            super.onBackPressed();
        }
    }
}