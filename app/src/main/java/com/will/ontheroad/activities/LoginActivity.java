package com.will.ontheroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.will.ontheroad.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Will on 2016/2/26.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private EditText account;
    private EditText password;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        initializeViews();
    }
    private void initializeViews(){
        account = (EditText) findViewById(R.id.login_page_edit_account);
        password = (EditText) findViewById(R.id.login_page_edit_password);
        TextView register = (TextView) findViewById(R.id.login_page_text_register);
        TextView forget = (TextView) findViewById(R.id.login_page_text_forget);
        Button confirm = (Button) findViewById(R.id.login_page_confirm_button);
        register.setOnClickListener(this);
        forget.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.login_page_text_register:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;
            case R.id.login_page_text_forget:
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
                break;
            case R.id.login_page_confirm_button:
                String accountMessage = account.getText().toString();
                String passwordMessage = password.getText().toString();
                accountMessage = accountMessage.replaceAll(" ","");
                if(accountMessage.isEmpty() || passwordMessage.isEmpty()){
                    showToast("请输入账号或密码");
                }else{
                    BmobUser user = new BmobUser();
                    user.setUsername(accountMessage);
                    user.setPassword(passwordMessage);
                    user.login(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            showToast("成功");
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        }
                        @Override
                        public void onFailure(int i, String s) {
                            if (i == 101) {
                                showToast("账号和密码不匹配,请重试");
                            } else {
                                showToast("登陆失败--" + i + "错误信息--" + s);
                            }
                        }
                    });
                }
                break;
        }
    }
}
