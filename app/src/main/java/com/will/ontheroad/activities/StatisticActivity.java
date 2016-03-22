package com.will.ontheroad.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.will.ontheroad.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Will on 2016/3/21.
 */
public class StatisticActivity extends BaseActivity {
    private PieChartView pieChart;
    private int spentDayNum;
    private int leftDayNum;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_page);
        initializeViews();
    }
    private void initializeViews(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.universal_toolbar_translucent);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(!isOvertime()) {
        TextView currentDate = (TextView) findViewById(R.id.statistic_page_now_date);
        TextView become = (TextView) findViewById(R.id.statistic_page_become);
        TextView leftDay = (TextView) findViewById(R.id.statistic_page_left_day);
        TextView createdAt = (TextView) findViewById(R.id.statistic_page_started_at);
        TextView spent = (TextView) findViewById(R.id.statistic_page_spent_day);
        pieChart = (PieChartView) findViewById(R.id.statistic_page_pie_chart);
        pieChart.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, SliceValue sliceValue) {
                if (((int) sliceValue.getValue()) == leftDayNum) {
                    showToast("目标实现还有" + (int) sliceValue.getValue() + "天");
                } else {
                    showToast("已经过去" + (int) sliceValue.getValue() + "天");
                }
            }

            @Override
            public void onValueDeselected() {

            }
        });
            currentDate.setText(getCurrentDate());
            spent.setText(getSpentDayNum());
            become.setText("距离成为"+getIntent().getStringExtra("become")+",还有");
            leftDay.setText(getLeftDayNum());
            createdAt.setText(getCreatedAt());
            setPieChart();
        }else{
            showToast("execute");
            findViewById(R.id.success_page).setVisibility(View.VISIBLE);
            findViewById(R.id.statistic_page).setVisibility(View.GONE);
        }
    }
    private String getCurrentDate(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(date);
    }
    private String getLeftDayNum(){
        Date currentDate = new Date(System.currentTimeMillis());
        Date achievementDate  = null;
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            achievementDate = format.parse(getIntent().getStringExtra("achievement_date"));
        }catch (ParseException p){
            p.printStackTrace();
        }
        GregorianCalendar c1 = new GregorianCalendar();
        GregorianCalendar c2 = new GregorianCalendar();
        c1.setTime(currentDate);
        c2.setTime(achievementDate);
        float dayNum = (c2.getTimeInMillis()-c1.getTimeInMillis())/(1000*60*60*24);
        leftDayNum = (int)(dayNum+0.5f);
        return String.valueOf(leftDayNum);
    }
    private String getSpentDayNum(){
        Date currentDate = new Date(System.currentTimeMillis());
        Date createdAt = null;
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            createdAt = format.parse(getIntent().getStringExtra("created_at"));
        }catch (ParseException p ){
            p.printStackTrace();
        }
        GregorianCalendar c1 = new GregorianCalendar();
        GregorianCalendar c2 = new GregorianCalendar();
        c1.setTime(currentDate);
        c2.setTime(createdAt);
        float dayNum = (c1.getTimeInMillis()-c2.getTimeInMillis())/(1000*60*60*24);
        spentDayNum = (int) (dayNum+0.5f);
        return String.valueOf(spentDayNum);
    }
    private String getCreatedAt(){
        Date createdAt = null;
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            createdAt = format.parse(getIntent().getStringExtra("created_at"));
        }catch (ParseException p ){
            p.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return "始于"+format.format(createdAt);
    }
    private boolean isOvertime(){
        Date currentDate = new Date(System.currentTimeMillis());
        Date achievementDate  = null;
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            achievementDate = format.parse(getIntent().getStringExtra("achievement_date"));
        }catch (ParseException p){
            p.printStackTrace();
        }
        GregorianCalendar c1 = new GregorianCalendar();
        GregorianCalendar c2 = new GregorianCalendar();
        c1.setTime(currentDate);
        c2.setTime(achievementDate);
        return (c2.getTimeInMillis()-c1.getTimeInMillis()) < 0;
    }
    private void setPieChart(){
        PieChartData data = new PieChartData();
        List<SliceValue> list = new ArrayList<>();
        list.add(new SliceValue(leftDayNum,Color.argb(180,102,102,153)));
        list.add(new SliceValue(spentDayNum,Color.argb(180,200,200,200)));
        data.setValues(list);
        data.setHasLabels(true);
        data.setHasLabelsOnlyForSelected(false);
        data.setValueLabelBackgroundEnabled(false);
        data.setSlicesSpacing(1);
        data.setValueLabelTextSize(20);
        pieChart.setPieChartData(data);
    }
}
