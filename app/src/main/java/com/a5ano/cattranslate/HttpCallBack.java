package com.a5ano.cattranslate;

/**
 * Created by Administrator on 2016/6/6 0006.
 */

public interface HttpCallBack {
    void onSuccess(String result);
    void onFailure(String exception);
}
