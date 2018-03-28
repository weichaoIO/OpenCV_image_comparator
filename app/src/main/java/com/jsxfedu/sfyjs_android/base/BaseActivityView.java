package com.jsxfedu.sfyjs_android.base;

public interface BaseActivityView {
    void showToast(String content);

    void showToast(String content, boolean isNessesary, int length);

    void finish();
}
