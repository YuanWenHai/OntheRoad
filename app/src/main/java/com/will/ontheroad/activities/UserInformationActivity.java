package com.will.ontheroad.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.will.ontheroad.R;
import com.will.ontheroad.bean.MyUser;
import com.will.ontheroad.utility.DownloadImageListener;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.ThumbnailUrlListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Will on 2016/3/3.
 */
public class UserInformationActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imageView;
    private EditText userNameEdit;
    private ProgressBar progressBar;
    private MyUser user;
    private boolean index;

    protected void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.user_information_page);
        initializeViews();
        initializeData();
        user = BmobUser.getCurrentUser(this, MyUser.class);
    }

    private void initializeViews() {
        imageView = (ImageView) findViewById(R.id.user_information_set_image);
        userNameEdit = (EditText) findViewById(R.id.user_information_name_edit);
        progressBar = (ProgressBar) findViewById(R.id.user_information_progress_bar);
        imageView.setOnClickListener(this);
        Button back = (Button) findViewById(R.id.bar_button_back);
        Button confirm = (Button) findViewById(R.id.bar_button_confirm);
        back.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_button_back:
                if (index) {
                    Intent intent = new Intent(UserInformationActivity.this, MainActivity.class);
                    intent.putExtra("refresh_user", true);
                    startActivity(intent);
                } else {
                    onBackPressed();
                }
                break;
            case R.id.bar_button_confirm:
                user.setUserName(userNameEdit.getText().toString());
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(UserInformationActivity.this, MainActivity.class);
                        intent.putExtra("refresh_user", true);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                    }
                });
                break;
            case R.id.user_information_set_image:
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
        }
    }

    private void initializeData() {
        BmobUser bmobUser = BmobUser.getCurrentUser(this, MyUser.class);
        String userName = (String) BmobUser.getObjectByKey(this, "userName");
        if (userName != null) {
            userNameEdit.setText(userName);
        } else {
            userNameEdit.setText(" ");
        }
        String thumbnail = (String) BmobUser.getObjectByKey(this, "userImageThumbnail");
        if (thumbnail != null) {
            downloadImage(this, thumbnail, new DownloadImageListener() {
                @Override
                public void onSuccess(String localImagePath) {
                    imageView.setImageDrawable(Drawable.createFromPath(localImagePath));
                }
            });
        } else {
            imageView.setImageResource(R.drawable.sakura);
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
            String filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();
            imageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            final BmobFile bmobFile = new BmobFile(new File(filePath));
            bmobFile.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    user.setUserImageName(bmobFile.getFileUrl(UserInformationActivity.this));
                    bmobFile.getThumbnailUrl(UserInformationActivity.this, dpToPx(UserInformationActivity.this, 100),
                            dpToPx(UserInformationActivity.this, 100), new ThumbnailUrlListener() {
                                @Override
                                public void onSuccess(String s) {
                                    user.setUserImageThumbnail(s);
                                    user.update(UserInformationActivity.this);
                                    downloadImage(UserInformationActivity.this, s, new DownloadImageListener() {
                                        @Override
                                        public void onSuccess(String localImagePath) {
                                            imageView.setImageDrawable(Drawable.createFromPath(localImagePath));
                                            progressBar.setVisibility(View.GONE);
                                            imageView.setVisibility(View.VISIBLE);
                                            showToast("已设置头像");
                                            index = true;
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                }
                            });
                }

                @Override
                public void onFailure(int i, String s) {
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (index) {
            Intent intent = new Intent(UserInformationActivity.this, MainActivity.class);
            intent.putExtra("refresh_user", true);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
}
