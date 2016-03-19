package com.will.ontheroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.adapter.BaseAdapterHelper;
import com.will.ontheroad.adapter.QuickAdapter;
import com.will.ontheroad.bean.Goal;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.popup.QuickPopup;

import junit.framework.Test;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Will on 2016/2/27.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{
    private QuickAdapter<Goal> goalAdapter;
    private ListView listView;
    private TextView userName;
    private ImageView userImage;
    private TextView date;
    private TextView leftHour;
    private TextView become;
    private LinearLayout userLayout;
    private Button addGoal;
    private Button note;
    private final int CACHE_FIRST = 0;
    private final int NETWORK_FIRST = 1;
    private MyUser user;
    private List<Goal> list;
    private ImageView profileImage;
    private TextView profileName;
    private String myGoalId;
    private QuickPopup markPopup;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.alpha_in,0);
        setContentView(R.layout.main_page);
        initializeViews();
        queryUser();
        queryTextContent();
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
                list = goals;
                goalAdapter.clear();
                if (list == null || list.size() == 0) {
                    return;
                }
                goalAdapter.addAll(list);
            }

            @Override
            public void onError(int i, String s) {
                showToast("错误码：" + i + "错误信息：" + s);
            }
        });
    }
    private void initializeViews(){
        listView = (ListView) findViewById(R.id.main_page_list);
        userName = (TextView) findViewById(R.id.main_page_toolbar_name);
        userImage = (ImageView) findViewById(R.id.main_page_toolbar_image);
        userLayout = (LinearLayout) findViewById(R.id.main_page_toolbar_user_layout);
        date = (TextView) findViewById(R.id.main_page_date);
        become  = (TextView) findViewById(R.id.main_page_become);
        leftHour = (TextView) findViewById(R.id.main_page_left_hours);
        profileImage = (ImageView) findViewById(R.id.profile_page_image);
        profileName = (TextView) findViewById(R.id.profile_page_name);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_page_drawer_layout);
        userLayout.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        profileImage.setOnClickListener(this);
        TextView profileChangePassword = (TextView) findViewById(R.id.profile_page_change_password);
        TextView profileLogout = (TextView) findViewById(R.id.profile_page_logout);
        TextView laboratory = (TextView) findViewById(R.id.profile_page_laboratory);
        TextView aboutMe = (TextView) findViewById(R.id.profile_page_about_me);
        profileChangePassword.setOnClickListener(this);
        profileLogout.setOnClickListener(this);
        laboratory.setOnClickListener(this);
        aboutMe.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
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
                userImage.setImageResource(R.drawable.sakura);
                profileImage.setImageResource(R.drawable.sakura);
            }
            String userNameStr = (String) BmobUser.getObjectByKey(this,"userName");
            if(userNameStr != null){
                userName.setText(userNameStr);
                profileName.setText(userNameStr);
            }else{
                userName.setText("请修改用户名");
                profileName.setText("请修改用户名");
            }
        }
    }
    private void queryTextContent(){
        String myGoalId = (String) BmobUser.getObjectByKey(this,"myGoalId");
        if(myGoalId == null){
            //
        }else{
            BmobQuery<Goal> query = new BmobQuery<>();
            query.getObject(this, myGoalId, new GetListener<Goal>() {
                @Override
                public void onSuccess(Goal goal) {
                    date.setText(getFormattedDateCorrectToDay(new Date(System.currentTimeMillis())));
                    become.setText(",距离成为"+goal.getBecome()+"还有");
                    leftHour.setText(countDateHour(goal.getAchievementDate()));
                    showToast(countDateHour(goal.getAchievementDate()));
                }
                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.main_page_toolbar_user_layout:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.main_page_add_goal:

                break;
            case R.id.main_page_note:
                //添加心情;
                startActivity(new Intent(this,Test.class));
                break;
            case R.id.profile_page_image:
                startActivity(new Intent(this, UserInformationActivity.class));
                break;
            case R.id.profile_page_change_password:
                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                break;
            case R.id.profile_page_laboratory:
                break;
            case R.id.profile_page_about_me:
                break;
            case R.id.profile_page_logout:
                BmobUser.logOut(this);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.popup_mark:
                markPopup.dismiss();
                user.setMyGoalId(myGoalId);
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        queryTextContent();
                    }
                    @Override
                    public void onFailure(int i, String s) {}
                });
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
        startActivity(intent);
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent,View view ,int position,long id){
        myGoalId = list.get(position).getObjectId();
        View v = View.inflate(this,R.layout.popup_mark,null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.popup_mark);
        layout.setOnClickListener(this);
        markPopup = new QuickPopup(v,dpToPx(this,150),dpToPx(this,50));
        markPopup.setAnimationStyle(R.style.alphaAnimation);
        int[] i = new int[2];
        view.getLocationOnScreen(i);
        markPopup.showAtLocation(listView, Gravity.TOP | Gravity.RIGHT, i[0], i[1]);
        return true;
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
                        helper.setImageResource(R.id.main_page_item_image, R.drawable.sakura);
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
                //startActivity(new Intent(this,TestActivity.class));
                ActivityCompat.startActivity(this, new Intent(this, TestActivity.class),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
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
        }else{
            super.onBackPressed();
        }
    }
}