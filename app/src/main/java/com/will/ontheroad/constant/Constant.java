package com.will.ontheroad.constant;

/**
 * Created by Will on 2016/4/14.
 */
public class Constant {
    //新闻
    public static final int TYPE_HEADLINE  = 0;
    public static final int TYPE_LOCAL = 1;
    public static final String HOST = "http://c.m.163.com/";
    public static final String END = ".html";
    public static final String DETAIL_END = "/full.html";
    //http://c.m.163.com/nc/article/headline/T1348647909107/0-20.html
    public static final String HEAD_LINE = "http://c.m.163.com/nc/article/headline/T1348647909107/";
    public static final String LOCAL = "http://c.m.163.com/nc/article/local/5aSq5Y6f/";
    public static final String DETAIL_HOST= "http://c.m.163.com/nc/article/";
    public static final String HEAD_LINE_CODE = "T1348647909107";
    public static final String LOCAL_CODE = "太原";
    public static final String NEWS_COUNT = "20";
    //图片
    //最新图片
    public static final String NEW_IMAGE = "http://c.m.163.com/photo/api/list/0096/4GJ60096.json";
    /**
     * //加载list，比如http://c.m.163.com/photo/api/morelist/0096/4GJ60096/91830.json,
     * 链接尾部数字为项目编号，如91830，则意味加载编号小于91830之前的10条项目。
     */
    public static final String MORE_IMAGE = "http://c.m.163.com/photo/api/morelist/0096/4GJ60096/";
    /**
     * 加载单个项目详情,比如http://c.m.163.com/photo/api/set/0096/91830.json
     */
    public static final String IMAGE_DETAIL = "http://c.m.163.com/photo/api/set/0096/";
    public static final String IMAGE_END = ".json";
 }
