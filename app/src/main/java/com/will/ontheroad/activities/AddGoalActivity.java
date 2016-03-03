package com.will.ontheroad.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.will.ontheroad.R;
import com.will.ontheroad.bean.Goal;
import com.will.ontheroad.bean.MyUser;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.ThumbnailUrlListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Will on 2016/2/28.
 */
public class AddGoalActivity extends BaseActivity implements View.OnClickListener {
    private EditText titleEdit;
    private EditText contentEdit;
    private EditText dateEdit;
    private EditText becomeEdit;
    private ImageView imageView;
    private String filePath;
    private Goal myGoal;
    private ProgressBar progressBar;
    private Button  confirm;
    private String preImageUrl;
    private String preThumbnailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_goal_page);
        initializeViews();
        myGoal = new Goal();
    }

    private void initializeViews() {
        titleEdit = (EditText) findViewById(R.id.add_goal_page_edit_title);
        contentEdit = (EditText) findViewById(R.id.add_goal_page_edit_content);
        dateEdit = (EditText) findViewById(R.id.add_goal_page_edit_target_date);
        becomeEdit = (EditText) findViewById(R.id.add_goal_page_edit_become);
        imageView = (ImageView) findViewById(R.id.add_goal_page_image);
        progressBar = (ProgressBar) findViewById(R.id.confirm_back_title_bar_progress);
        confirm = (Button) findViewById(R.id.add_goal_page_button_confirm);
        Button back = (Button) findViewById(R.id.add_goal_page_button_back);
        confirm.setOnClickListener(this);
        back.setOnClickListener(this);
        imageView.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_goal_page_button_back:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.add_goal_page_image:
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
                break;
            case R.id.add_goal_page_button_confirm:
                String title = titleEdit.getText().toString();
                String content = contentEdit.getText().toString();
                String date = dateEdit.getText().toString();
                String become =becomeEdit.getText().toString();
                if(title.isEmpty()|| date.isEmpty()|| become.isEmpty()){
                    showToast("请完善内容");
                }else{
                    try{
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
                        Date uploadDate = format.parse(date);
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
                    myGoal.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            //跳转到此目标页面
                            Intent intent = new Intent(AddGoalActivity.this,MainActivity.class);
                            intent.putExtra("refresh_goal",true);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            showToast("错误码:" + i + "错误信息" + s);
                        }
                    });
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
