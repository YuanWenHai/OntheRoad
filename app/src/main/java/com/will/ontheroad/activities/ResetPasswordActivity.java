package com.will.ontheroad.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.will.ontheroad.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;

/**
 * Created by Will on 2016/2/26.
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private EditText email;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_page);
        initializeViews();
    }
    private void initializeViews(){
        email = (EditText) findViewById(R.id.forget_page_edit_email);
        Button reset = (Button) findViewById(R.id.forget_page_button_find);
        reset.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        String emailMessage = email.getText().toString();
        emailMessage = emailMessage.replaceAll(" ","");
        if(emailMessage.isEmpty()){
            showToast("请输入邮箱地址");
        }else{
            BmobUser user = new BmobUser();
            user.resetPasswordByEmail(this, emailMessage, new ResetPasswordByEmailListener() {
                @Override
                public void onSuccess() {
                    showToast("成功");
                }

                @Override
                public void onFailure(int i, String s) {
                    showToast("失败");
                }
            });
        }
    }

}
