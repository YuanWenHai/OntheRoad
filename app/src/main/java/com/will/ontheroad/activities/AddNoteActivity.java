package com.will.ontheroad.activities;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.LocalThumbnailListener;
import com.squareup.picasso.Picasso;
import com.will.ontheroad.R;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.bean.Note;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Will on 2016/3/31.
 */
public class AddNoteActivity extends BaseActivity implements View.OnClickListener{
    private EditText editContent;
    private ImageView imageContent;
    private ProgressBar progressBar;
    private RelativeLayout layout;
    private Toolbar toolbar;
    private Note note = new Note();
    private boolean uploadFullImageSuccess;
    private boolean uploadThumbnailSuccess;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_diary_page);
        initializeViews();
    }
    private void initializeViews(){
        editContent = (EditText) findViewById(R.id.add_diary_page_edit);
        imageContent = (ImageView) findViewById(R.id.add_diary_page_image);
        layout = (RelativeLayout) findViewById(R.id.add_diary_page_image_layout);
        String content = getIntent().getStringExtra("content");
        String image = getIntent().getStringExtra("image");
        id = getIntent().getStringExtra("id");
        if(content != null){
            editContent.setText(content);
        }
        if (image!=null && !image.isEmpty()){
            Picasso.with(this).load(image).into(imageContent);
            imageContent.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
        }
        Button pickImage = (Button) findViewById(R.id.add_diary_page_choose_image);
        ImageView deleteImage = (ImageView) findViewById(R.id.add_diary_page_delete_image);
        pickImage.setOnClickListener(this);
        deleteImage.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.universal_toolbar);
        TextView title = (TextView) findViewById(R.id.universal_toolbar_text);
        if(content != null || image != null ){
            title.setText("编辑心情");
        }else{
            title.setText("添加心情");
        }
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.universal_toolbar_progressbar);
    }
    @Override
    public void onClick(View v ){
        switch(v.getId()){
            case R.id.add_diary_page_delete_image:
                imageContent.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                note.setImage("");
                break;
            case R.id.add_diary_page_choose_image:
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
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
            imageContent.setImageURI(uri);
            layout.setVisibility(View.VISIBLE);
            toolbar.getMenu().getItem(0).setVisible(false);
            progressBar.setVisibility(View.VISIBLE);
            //上传全尺寸图片文件
            final BmobFile file = new BmobFile(new File(filePath));
            file.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    note.setImage(file.getFileUrl(AddNoteActivity.this));
                    uploadFullImageSuccess = true;
                    if(uploadThumbnailSuccess){
                        progressBar.setVisibility(View.GONE);
                        toolbar.getMenu().getItem(0).setVisible(true);
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
                    bmobFile.uploadblock(AddNoteActivity.this, new UploadFileListener() {
                        @Override
                        public void onSuccess() {
                            note.setImageThumbnail(bmobFile.getFileUrl(AddNoteActivity.this));
                            uploadThumbnailSuccess = true;
                            if(uploadFullImageSuccess){
                                progressBar.setVisibility(View.GONE);
                                toolbar.getMenu().getItem(0).setVisible(true);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_page_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
       String content = editContent.getText().toString();
        if((note.getImage() == null || note.getImage().isEmpty())&& content.isEmpty()){
            showToast("请完善内容");
        }else{
            note.setContent(content);
            note.setUser(BmobUser.getCurrentUser(this, MyUser.class));
            final Intent intent = new Intent(this,NoteActivity.class);
            if(id != null){
                note.update(this, id, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
            }else{
                note.save(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
            }
        }
        return true;
    }
}
