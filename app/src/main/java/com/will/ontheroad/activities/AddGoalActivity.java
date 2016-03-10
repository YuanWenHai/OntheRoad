package com.will.ontheroad.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.will.ontheroad.R;
import com.will.ontheroad.bean.Goal;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.utility.DownloadImageListener;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.ThumbnailUrlListener;
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
    private Boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_goal_page);
        myGoal = new Goal();
        receivedIntent = getIntent();
        initializeViews();
    }

    private void initializeViews() {
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
                downloadImage(this, receivedIntent.getStringExtra("image"), new DownloadImageListener() {
                    @Override
                    public void onSuccess(String localImagePath) {
                        imageView.setImageDrawable(Drawable.createFromPath(localImagePath));
                    }
                });
            }
        }
        progressBar = (ProgressBar) findViewById(R.id.confirm_back_title_bar_progress);
        confirm = (Button) findViewById(R.id.add_goal_page_button_confirm);
        Button back = (Button) findViewById(R.id.add_goal_page_button_back);
        dateText.setOnClickListener(this);
        confirm.setOnClickListener(this);
        back.setOnClickListener(this);
        imageView.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_goal_page_button_back:
                onBackPressed();
                break;
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
            case R.id.add_goal_page_button_confirm:
                String title = titleEdit.getText().toString();
                String content = contentEdit.getText().toString();
                //String date = dateEdit.getText().toString();
                String become =becomeEdit.getText().toString();
                if(title.isEmpty()|| pickedDate == null|| become.isEmpty()){
                    showToast("请完善内容");
                }else{
                    try{
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        Date uploadDate = format.parse(pickedDate);
                        myGoal.setAchievementDate(uploadDate);
                    }catch (ParseException p){
                        p.printStackTrace();
                        showToast("请输入有效的日期,例如20160805");
                        return;
                    }
                    if(!content.isEmpty()){
                        myGoal.setPresentation(content);
                    }
                    myGoal.setName(title);
                    myGoal.setBecome(become);
                    myGoal.setCreateDate(new Date(System.currentTimeMillis()));
                    myGoal.setUpdateDate(new Date(System.currentTimeMillis()));
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
            Log.e("filePath", filePath);
            imageView.setImageURI(uri);
            confirm.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            final BmobFile file = new BmobFile(new File(filePath));
            file.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    myGoal.setImageFileName(file.getFileUrl(AddGoalActivity.this));
                    file.getThumbnailUrl(AddGoalActivity.this, dpToPx(AddGoalActivity.this, 100),
                            dpToPx(AddGoalActivity.this, 100), new ThumbnailUrlListener() {
                                @Override
                                public void onSuccess(String s) {
                                    myGoal.setImageThumbnail(s);
                                }
                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                    confirm.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    showToast("upload success.Url is "+file.getFileUrl(AddGoalActivity.this));
                }
                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }
}
