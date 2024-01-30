package org.yuezhikong.plugins.net;

import cn.hutool.http.HttpUtil;
public class Post {
    public static String getToken(String ticker) {
        String url = "https://qt.gtimg.cn/q=s_" + ticker;
        String result= HttpUtil.post(url, "");
        return result;
    }
}