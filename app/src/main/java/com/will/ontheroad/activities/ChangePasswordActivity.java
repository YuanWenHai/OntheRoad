package com.will.ontheroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.will.ontheroad.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Will on 2016/3/3.
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener{
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
        Button back = (Button) findViewById(R.id.bar_button_back);
        Button confirm = (Button) findViewById(R.id.bar_button_confirm);
        back.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.bar_button_back:
                onBackPressed();
                break;
            case R.id.bar_button_confirm:
                final String origin = originalPassword.getText().toString();
                final String new1 = newPassword1.getText().toString();
                final String new2 = newPassword2.getText().toString();
                if(origin.isEmpty() |new1.isEmpty()|new2.isEmpty()){
                    showToast("请完善内容");
                }else if (!new1.equals(new2)){
                    showToast("新密码输入不一致 ");
                }else{
                    BmobUser.updateCurrentUserPassword(this, origin, new2, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                         showToast("修改成功");
                            startActivity(new Intent(ChangePasswordActivity.this,MainActivity.class));
                        }
                        @Override
                        public void onFailure(int i, String s) {
                            showToast("s");
                        }
                    });
                }
        }
    }
}
