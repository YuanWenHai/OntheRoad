package com.will.ontheroad.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.LocalThumbnailListener;
import com.will.ontheroad.R;
import com.will.ontheroad.bean.Diary;
import com.will.ontheroad.utility.DownloadImageListener;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Will on 2016/3/6.
 */
public class AddDiaryActivity extends BaseActivity implements View.OnClickListener{
    private EditText contentEdit;
    private ImageView contentImage;
    private ProgressBar progressBar;
    private Button confirm;
    private String goalId;
    private Diary diary;
    private Intent receivedIntent;
    private boolean isEdit;
    private boolean uploadFullImageSuccess;
    private boolean uploadThumbnailSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_diary_page);
        receivedIntent = getIntent();
        initializeViews();
    }
    private void initializeViews(){
        isEdit = receivedIntent.getBooleanExtra("edit", false);
        goalId = receivedIntent.getStringExtra("goal");
        diary = new Diary();
        contentEdit = (EditText) findViewById(R.id.add_diary_page_edit);
        contentImage = (ImageView) findViewById(R.id.add_diary_page_image);
        if(isEdit){
            contentEdit.setText(receivedIntent.getStringExtra("content"));
            downloadImage(this, receivedIntent.getStringExtra("imagePath"), new DownloadImageListener() {
                @Override
                public void onSuccess(Drawable drawable) {
                    contentImage.setImageDrawable(drawable);
                    contentImage.setVisibility(View.VISIBLE);
                }
            });
        }
        progressBar = (ProgressBar) findViewById(R.id.confirm_back_title_bar_progress);
        Button back = (Button) findViewById(R.id.bar_button_back);
        confirm = (Button) findViewById(R.id.bar_button_confirm);
        Button chooseImage = (Button) findViewById(R.id.add_diary_page_choose_image);
        ScrollView blank = (ScrollView) findViewById(R.id.scroll);
        back.setOnClickListener(this);
        confirm.setOnClickListener(this);
        chooseImage.setOnClickListener(this);
        blank.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
       switch(v.getId()){
           case R.id.scroll:
               contentEdit.requestFocus();
               InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
               manager.showSoftInput(contentEdit,0);
               break;
           case R.id.bar_button_back:
               onBackPressed();
               break;
           case R.id.add_diary_page_choose_image:
               startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
               break;
           case R.id.bar_button_confirm:
               String content = contentEdit.getText().toString();
               if(diary.getImage() == null && content.isEmpty()){
                   showToast("请输入内容");
               }else{
                   diary.setContent(content);
                   diary.setGoalId(goalId);
                  if(!isEdit){
                      /*Goal goal = new Goal();
                      goal.setUpdateDate(new Date(System.currentTimeMillis()));
                      goal.update(this, receivedIntent.getStringExtra("objectId"), new UpdateListener() {
                          @Override
                          public void onSuccess(){}
                          @Override
                          public void onFailure(int i, String s) {
                              showToast(s);
                          }
                      });*/
                      diary.save(this, new SaveListener() {
                          @Override
                          public void onSuccess() {
                              Intent intent = new Intent(AddDiaryActivity.this, GoalActivity.class);
                              intent.putExtra("refresh", true);
                              startActivity(intent);
                              finish();
                          }

                          @Override
                          public void onFailure(int i, String s) {
                              showToast(s);
                          }
                      });
                  }else{
                      diary.update(this, receivedIntent.getStringExtra("objectId"), new UpdateListener() {
                          @Override
                          public void onSuccess() {
                              Intent intent = new Intent(AddDiaryActivity.this,GoalActivity.class);
                              intent.putExtra("refresh",true);
                              startActivity(intent);
                              finish();
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
            final String filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();
            contentImage.setImageURI(uri);
            contentImage.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            //上传全尺寸图片文件
            final BmobFile file = new BmobFile(new File(filePath));
            file.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    diary.setImage(file.getFileUrl(AddDiaryActivity.this));
                    uploadFullImageSuccess = true;
                    if(uploadThumbnailSuccess){
                    progressBar.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onFailure(int i, String s) {
                    showToast(s);
                }
            });
            //本地裁剪并上传略缩图
            new BmobProFile().getLocalThumbnail(filePath, 5,1000,1000, new LocalThumbnailListener() {
                @Override
                public void onSuccess(String s) {
                    final BmobFile bmobFile = new BmobFile(new File(s));
                    bmobFile.uploadblock(AddDiaryActivity.this, new UploadFileListener() {
                        @Override
                        public void onSuccess() {
                            diary.setImageThumbnail(bmobFile.getFileUrl(AddDiaryActivity.this));
                            uploadThumbnailSuccess = true;
                            if(uploadFullImageSuccess){
                                progressBar.setVisibility(View.GONE);
                                confirm.setVisibility(View.VISIBLE);
                            }
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
        }
    }
    protected void onNewIntent(Intent intent){
        receivedIntent = intent;
        initializeViews();
    }
}
