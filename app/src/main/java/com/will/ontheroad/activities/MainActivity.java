package com.will.ontheroad.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.will.ontheroad.R;
import com.will.ontheroad.adapter.BaseAdapterHelper;
import com.will.ontheroad.adapter.QuickAdapter;
import com.will.ontheroad.bean.Goal;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.popup.QuickPopup;
import com.will.ontheroad.utility.DownloadImageListener;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Will on 2016/2/27.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
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
    private QuickPopup profilePopup;
    private ImageView profileImage;
    private TextView profileName;
    //TextView profileSetImage;
    //TextView profileSetName;
    //TextView profileChangePassword;
    //TextView profileLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
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
        userName = (TextView) findViewById(R.id.main_page_user_name);
        userImage = (ImageView) findViewById(R.id.main_page_user_image);
        userLayout = (LinearLayout) findViewById(R.id.main_page_user_layout);
        date = (TextView) findViewById(R.id.main_page_date);
        become  = (TextView) findViewById(R.id.main_page_become);
        leftHour = (TextView) findViewById(R.id.main_page_left_hours);
        addGoal = (Button) findViewById(R.id.main_page_add_goal);
        note = (Button) findViewById(R.id.main_page_note);
        userLayout.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        addGoal.setOnClickListener(this);
        note.setOnClickListener(this);
    }
    private void queryUser(){
        user = BmobUser.getCurrentUser(this,MyUser.class);
        if(user == null){
            //注册页面
        }else{
            if(user.getUserImageThumbnail() != null){
                try{
                    downloadImage(this, user.getUserImageThumbnail(), new DownloadImageListener() {
                        @Override
                        public void onSuccess(String localImagePath) {
                            userImage.setImageDrawable(Drawable.createFromPath(localImagePath));
                        }
                    });
                }catch (Exception e){}
            }else{
                userImage.setImageResource(R.drawable.sakura);
            }
            String userNameStr = (String) BmobUser.getObjectByKey(this,"userName");
            if(userNameStr != null){
                userName.setText(userNameStr);
            }else{
                userName.setText("修改用户名");
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
                    date.setText("今天是" + getFormattedDateCorrectToDay(new Date(System.currentTimeMillis())));
                    become.setText(goal.getBecome());
                    leftHour.setText(countDateHour(goal.getAchievementDate()));
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
            case R.id.main_page_user_layout:
                showProfilePopup();
                break;
            case R.id.main_page_add_goal:
                startActivity(new Intent(this,AddGoalActivity.class));
                break;
            case R.id.main_page_note:
                //添加心情;
                break;
            case R.id.profile_user_information:
                startActivity(new Intent(this,UserInformationActivity.class));
                profilePopup.dismiss();
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent,View view ,int position,long id){
        //进入相应的目标主界面
    }
    private void initializeAdapter() {
        if (goalAdapter == null) {
            goalAdapter = new QuickAdapter<Goal>(this, R.layout.main_page_list_item) {
                @Override
                protected void convert(final BaseAdapterHelper helper, final Goal goal) {
                    helper.setText(R.id.main_page_item_goal, goal.getName())
                            .setText(R.id.main_page_item_begin_date, getFormattedDateCorrectToDay(goal.getCreateDate()))
                            .setText(R.id.main_page_item_last_update_date, getFormattedDateCorrectToDay(goal.getUpdateDate()));
                    if(goal.getImageThumbnail() !=null){
                    downloadImage(MainActivity.this, goal.getImageThumbnail(), new DownloadImageListener() {
                        @Override
                        public void onSuccess(final String imagePath) {
                            helper.setImageDrawable(R.id.main_page_item_image, Drawable.createFromPath(imagePath));
                        }
                    });
                    }else{
                        helper.setImageResource(R.id.main_page_item_image, R.drawable.sakura);
                    }
                }
            };
            listView.setAdapter(goalAdapter);
        }
        queryGoal(NETWORK_FIRST);
    }
    private void showProfilePopup(){
        View view = View.inflate(this,R.layout.profile_page,null);
        profileImage = (ImageView) view.findViewById(R.id.profile_page_image);
        downloadImage(this, user.getUserImageThumbnail(), new DownloadImageListener() {
            @Override
            public void onSuccess(String localImagePath) {
                profileImage.setImageDrawable(Drawable.createFromPath(localImagePath));
            }
        });
        profileName = (TextView) view.findViewById(R.id.profile_page_name);
        String userName = (String)BmobUser.getObjectByKey(this,"userName");
        if(userName != null){
            profileName.setText(userName);
        }else{
            profileName.setText("设置用户名");
        }
        RelativeLayout profileUserInformation = (RelativeLayout) view.findViewById(R.id.profile_user_information);
        profileUserInformation.setOnClickListener(this);
        TextView profileChangePassword = (TextView) view.findViewById(R.id.profile_page_change_password);
        TextView profileLogout = (TextView) view.findViewById(R.id.profile_page_logout);
        TextView profileClearCache = (TextView) findViewById(R.id.profile_page_clear_cache);
        profileChangePassword.setOnClickListener(this);
        profileLogout.setOnClickListener(this);
        profileClearCache.setOnClickListener(this);
        profilePopup = new QuickPopup(view,mScreenWidth*3/5,mScreenHeight-getStateBarHeight());
        profilePopup.setAnimationStyle(R.style.profileAnimation);
        profilePopup.showAtLocation(userLayout, Gravity.START | Gravity.TOP, 0, getStateBarHeight());
    }
    @Override
    protected void onNewIntent(Intent intent){
        if(intent.getBooleanExtra("refresh_goal",false)){
            queryGoal(NETWORK_FIRST);
        }else if(intent.getBooleanExtra("refresh_user",false)){
            queryUser();
        }
    }

}