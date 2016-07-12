package com.coolweather.app.util;

/**
 * Created by MyPC on 2016/7/12 0012.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
