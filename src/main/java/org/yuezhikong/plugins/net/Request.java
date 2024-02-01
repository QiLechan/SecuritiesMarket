package org.yuezhikong.plugins.net;

import cn.hutool.http.HttpUtil;
public class Request {
    /**
     * 获取股票信息
     * @param ticker 股票代码
     * @return 返回股票信息，若股票代码不存在则返回"none_match"
     */
    public static String getTicker(String ticker) {
        String url = "http://qt.gtimg.cn/q=s_" + ticker;
        String result= HttpUtil.get(url);
        if (result.contains("none_match")){
            return "none_match";
        }
        return result;
    }
}