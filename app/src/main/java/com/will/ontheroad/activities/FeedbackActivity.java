package com.will.ontheroad.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.will.ontheroad.R;
import com.will.ontheroad.bean.Feedback;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Will on 2016/3/22.
 */
public class FeedbackActivity extends BaseActivity {
    private EditText edit;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_page);
        TextView title = (TextView) findViewById(R.id.universal_toolbar_text);
        title.setText("提交反馈");
        edit = (EditText) findViewById(R.id.feedback_page_edit);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.universal_toolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
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
        getMenuInflater().inflate(R.menu.add_page_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String content = edit.getText().toString();
        if(content.isEmpty()){
            showToast("好歹说几句啊");
        }else{
            Feedback feedback = new Feedback();
            feedback.setAndroidVersion(Build.VERSION.RELEASE);
            feedback.setModel(Build.MODEL);
            feedback.setSdkVersion(String.valueOf(Build.VERSION.SDK_INT));
            feedback.setContent(content);
            feedback.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    showToast("提交成功");
                    startActivity(new Intent(FeedbackActivity.this,MainActivity.class));
                    finish();
                }
                @Override
                public void onFailure(int i, String s) {
                    showToast(s);
                }
            });
        }
        return true;
    }
}
