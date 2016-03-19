package com.will.ontheroad.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.LocalThumbnailListener;
import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.bean.Goal;
import com.will.ontheroad.bean.MyUser;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.qqtheme.framework.picker.DatePicker;

/**
 * Created by Will on 2016/2/28.
 */
public class AddGoalActivity extends BaseActivity implements View.OnClickListener {
    private EditText titleEdit;
    private EditText contentEdit;
    private TextView dateText;
    private EditText becomeEdit;
    private ImageView imageView;
    private String filePath;
    private Goal myGoal;
    private ProgressBar progressBar;
    private Button  confirm;
    private String preImageUrl;
    private String preThumbnailUrl;
    private Intent receivedIntent;
    private String pickedDate;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_goal_page);
        myGoal = new Goal();
        receivedIntent = getIntent();
        initializeViews();
    }

    private void initializeViews() {
        mToolbar = (Toolbar) findViewById(R.id.universal_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleEdit = (EditText) findViewById(R.id.add_goal_page_edit_title);
        contentEdit = (EditText) findViewById(R.id.add_goal_page_edit_content);
        dateText = (TextView) findViewById(R.id.add_goal_page_date_text);
        becomeEdit = (EditText) findViewById(R.id.add_goal_page_edit_become);
        imageView = (ImageView) findViewById(R.id.add_goal_page_image);
        if(receivedIntent.getBooleanExtra("edit",false)){
            pickedDate = receivedIntent.getStringExtra("date");
            titleEdit.setText(receivedIntent.getStringExtra("title"));
            dateText.setText(pickedDate);
            becomeEdit.setText(receivedIntent.getStringExtra("become"));
            if(receivedIntent.getStringExtra("content") != null){
                contentEdit.setText(receivedIntent.getStringExtra("content"));
            }
            if(receivedIntent.getStringExtra("image") != null){
                Picasso.with(this).load(receivedIntent.getStringExtra("image")).into(imageView);
                /*downloadImage(this, receivedIntent.getStringExtra("image"), new DownloadImageListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        imageView.setImageDrawable(drawable);
                    }
                });*/
            }
        }
        progressBar = (ProgressBar) findViewById(R.id.universal_toolbar_progressbar);
        //confirm = (Button) findViewById(R.id.add_goal_page_button_confirm);
        //Button back = (Button) findViewById(R.id.add_goal_page_button_back);
        dateText.setOnClickListener(this);
        //confirm.setOnClickListener(this);
        //back.setOnClickListener(this);
        imageView.setOnClickListener(this);
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_goal_page_image:
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
                break;
            case R.id.add_goal_page_date_text:
                if(!receivedIntent.getBooleanExtra("edit",false)){
                    DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH_DAY);
                    picker.setRange(2016, 2099);
                    picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                        @Override
                        public void onDatePicked(String year, String month, String day) {
                            pickedDate = year + "-" + month + "-" + day;
                            dateText.setText(pickedDate);
                        }
                    });
                    picker.show();
                }else{
                    showToast("这个无法修改啦");
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();
            //imageView.setImageURI(uri);
            mToolbar.getMenu().getItem(0).setVisible(false);
            progressBar.setVisibility(View.VISIBLE);
            new BmobProFile().getLocalThumbnail(filePath, 5, 400, 400, new LocalThumbnailListener() {
                @Override
                public void onSuccess(String s) {
                    imageView.setImageDrawable(Drawable.createFromPath(s));;
                    final BmobFile bmobFile = new BmobFile(new File(s));
                    bmobFile.uploadblock(AddGoalActivity.this, new UploadFileListener() {
                        @Override
                        public void onSuccess() {
                            myGoal.setImageThumbnail(bmobFile.getFileUrl(AddGoalActivity.this));
                        }
                        @Override
                        public void onFailure(int i, String s) {
                            showToast(s);
                        }
                    });
                }
                @Override
                public void onError(int i, String s) {
                    showToast(s);
                }
            });
            final BmobFile file = new BmobFile(new File(filePath));
            file.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    myGoal.setImageFileName(file.getFileUrl(AddGoalActivity.this));
                    mToolbar.getMenu().getItem(0).setVisible(true);
                    progressBar.setVisibility(View.GONE);
                    showToast("upload success.Url is " + file.getFileUrl(AddGoalActivity.this));
                }
                @Override
                public void onFailure(int i, String s) {
                    showToast(s);
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.universal_toolbar_done:
                String title = titleEdit.getText().toString();
                String content = contentEdit.getText().toString();
                //String date = dateEdit.getText().toString();
                String become =becomeEdit.getText().toString();
                if(title.isEmpty()|| pickedDate == null|| become.isEmpty()){
                    showToast("请完善内容");
                }else{
                    myGoal.setAchievementDate(pickedDate);
                    if(!content.isEmpty()){
                        myGoal.setPresentation(content);
                    }
                    myGoal.setName(title);
                    myGoal.setBecome(become);
                    //myGoal.setCreateDate(new Date(System.currentTimeMillis()));
                    //myGoal.setUpdateDate(new Date(System.currentTimeMillis()));
                    myGoal.setUser(BmobUser.getCurrentUser(this, MyUser.class));
                    if(!receivedIntent.getBooleanExtra("edit",false)){
                        myGoal.save(this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                //跳转到此目标页面
                                Intent intent = new Intent(AddGoalActivity.this,MainActivity.class);
                                intent.putExtra("refresh_goal",true);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                showToast("错误码:" + i + "错误信息" + s);
                            }
                        });
                    }else{
                        myGoal.update(AddGoalActivity.this, receivedIntent.getStringExtra("objectId"), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                Intent intent = new Intent(AddGoalActivity.this,GoalActivity.class);
                                intent.putExtra("refresh",true);
                                startActivity(intent);
                            }
                            @Override
                            public void onFailure(int i, String s) {
                                showToast(s);
                            }
                        });
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
