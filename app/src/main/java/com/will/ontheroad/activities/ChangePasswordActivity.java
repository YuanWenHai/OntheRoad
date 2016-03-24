package com.will.ontheroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.will.ontheroad.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Will on 2016/3/3.
 */
public class ChangePasswordActivity extends BaseActivity{
    private EditText originalPassword;
    private EditText newPassword1;
    private EditText newPassword2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_page);
        initializeViews();
    }
    private void initializeViews(){
        originalPassword = (EditText) findViewById(R.id.change_password_page_origin);
        newPassword1 = (EditText) findViewById(R.id.change_password_page_new1);
        newPassword2 = (EditText) findViewById(R.id.change_password_page_new2);
        TextView text = (TextView) findViewById(R.id.universal_toolbar_text);
        text.setText("修改密码");
        Toolbar mToolbar = (Toolbar) findViewById(R.id.universal_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setTitle("修改密码");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.add_page_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String origin = originalPassword.getText().toString();
        final String new1 = newPassword1.getText().toString();
        final String new2 = newPassword2.getText().toString();
        if (origin.isEmpty() | new1.isEmpty() | new2.isEmpty()) {
            showToast("请完善内容");
        } else if (!new1.equals(new2)) {
            showToast("新密码输入不一致 ");
        } else {
            BmobUser.updateCurrentUserPassword(this, origin, new2, new UpdateListener() {
                @Override
                public void onSuccess() {
                    showToast("修改成功");
                    startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                }

                @Override
                public void onFailure(int i, String s) {
                    if (i == 210) {
                        showToast("原始密码输入错误");
                    } else {
                        showToast("error code:" + i + "\n" + s);
                    }
                }
            });
        }
        return true;
    }

}
