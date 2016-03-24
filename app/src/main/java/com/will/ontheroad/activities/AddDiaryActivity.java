package com.will.ontheroad.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.LocalThumbnailListener;
import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.bean.Diary;

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
    private Toolbar mToolbar;
    private String goalId;
    private Diary diary;
    private Intent receivedIntent;
    private boolean isEdit;
    private boolean uploadFullImageSuccess;
    private boolean uploadThumbnailSuccess;
    private RelativeLayout imageLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_diary_page);
        receivedIntent = getIntent();
        initializeViews();
    }
    private void initializeViews(){
        TextView title = (TextView) findViewById(R.id.universal_toolbar_text);
        title.setText("新的进展");
        isEdit = receivedIntent.getBooleanExtra("edit", false);
        goalId = receivedIntent.getStringExtra("goal");
        imageLayout = (RelativeLayout) findViewById(R.id.add_diary_page_image_layout);
        diary = new Diary();
        contentEdit = (EditText) findViewById(R.id.add_diary_page_edit);
        contentImage = (ImageView) findViewById(R.id.add_diary_page_image);
        if(isEdit){
            title.setText("编辑进展");
            contentEdit.setText(receivedIntent.getStringExtra("content"));
            String path = receivedIntent.getStringExtra("imagePath");
            if( path !=null  &&!receivedIntent.getStringExtra("imagePath").equals("")){
                Picasso.with(this).load(receivedIntent.getStringExtra("imagePath")).into(contentImage);
                imageLayout.setVisibility(View.VISIBLE);
            }
        }
        progressBar = (ProgressBar) findViewById(R.id.universal_toolbar_progressbar);
        mToolbar = (Toolbar) findViewById(R.id.universal_toolbar);
        ImageView deleteImage = (ImageView) findViewById(R.id.add_diary_page_delete_image);
        deleteImage.setOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button chooseImage = (Button) findViewById(R.id.add_diary_page_choose_image);
        ScrollView blank = (ScrollView) findViewById(R.id.scroll);
        chooseImage.setOnClickListener(this);
        blank.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
       switch(v.getId()){
           case R.id.scroll:
               showToast("clicked scroll view");
               contentEdit.requestFocus();
               InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
               manager.showSoftInput(contentEdit,0);
               break;
           case R.id.add_diary_page_choose_image:
               startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
               break;
           case R.id.add_diary_page_delete_image:
               imageLayout.setVisibility(View.GONE);
               diary.setImageThumbnail("");
               diary.setImage("");
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
            final String filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();
            contentImage.setImageURI(uri);
            imageLayout.setVisibility(View.VISIBLE);
            mToolbar.getMenu().getItem(0).setVisible(false);
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
                    mToolbar.getMenu().getItem(0).setVisible(true);
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
                                mToolbar.getMenu().getItem(0).setVisible(true);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.universal_toolbar_done:
                if(toggle){
                    delay(1000);
                String content = contentEdit.getText().toString();
                if((diary.getImage() == null ||diary.getImage().equals("")) && content.isEmpty()){
                    showToast("请输入内容");
                }else{
                    diary.setContent(content);
                    diary.setGoalId(goalId);
                    if(!isEdit){
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
