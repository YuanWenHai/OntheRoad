package com.will.ontheroad.news;

import com.will.ontheroad.bean.NewsDetailBean;
import com.will.ontheroad.constant.Constant;
import com.will.ontheroad.utility.OkHttpUtil;

/**
 * Created by Will on 2016/4/15.
 */
public class NewsDetailHelper {
    public static void getDetail(final String id, final NewsDetailCallback callback){
        OkHttpUtil.get(getUrl(id), new OkHttpUtil.Callback() {
            @Override
            public void onResult(String result) {
                callback.detailCallback(NewsJsonUtils.readJsonNewsDetailBeans(result,id));
            }
        });
    }

    private static String getUrl(String id){
        return Constant.DETAIL_HOST+id+Constant.DETAIL_END;
    }

    interface NewsDetailCallback{
            void detailCallback(NewsDetailBean bean);
    }
}
