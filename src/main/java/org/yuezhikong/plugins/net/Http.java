package org.yuezhikong.plugins.net;

import cn.hutool.http.HttpUtil;
public class Http {
    public static String getTicker(String ticker) {
        String url = "http://qt.gtimg.cn/q=s_" + ticker;
        String result= HttpUtil.get(url);
        return result;
    }
}