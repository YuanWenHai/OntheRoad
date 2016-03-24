package com.will.ontheroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.will.ontheroad.R;
import com.will.ontheroad.bean.MyUser;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Will on 2016/2/26.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private EditText account;
    private EditText password;
    private EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        initializeViews();

    }
    private void initializeViews(){
        Button commit;
        account = (EditText) findViewById(R.id.register_page_edit_account);
        password = (EditText) findViewById(R.id.register_page_edit_password);
        email = (EditText) findViewById(R.id.register_page_edit_email);
        commit = (Button) findViewById(R.id.register_page_commit_button);
        commit.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        String accountMessage = account.getText().toString();
        String passwordMessage = password.getText().toString();
        String emailMessage = email.getText().toString();
         if(accountMessage.contains(" ") ||emailMessage.contains(" ")){
            accountMessage = accountMessage.replaceAll(" ","");
             account.setText(accountMessage);
             emailMessage = emailMessage.replaceAll(" ","");
             email.setText(emailMessage);
        }
        if(emailMessage.length()<1){
             showToast("Email不能为空");
         }
        else if(accountMessage.length()<6 || passwordMessage.length()<6){
            showToast("账号/密码过短,不能少于6位");
        }
        else{
            MyUser user = new MyUser();
            user.setUsername(accountMessage);
            user.setPassword(passwordMessage);
            user.setEmail(emailMessage);
            user.signUp(this,new SaveListener(){
                @Override
            public void onSuccess(){
                    showToast("注册成功");
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                }
                @Override
            public void onFailure(int code,String message){
                    if(code == 202){
                        showToast("此账号已存在");
                    }else{
                        showToast("注册失败!错误码"+code+"错误信息"+message);
                    }
                }
            });
        }
    }
}
