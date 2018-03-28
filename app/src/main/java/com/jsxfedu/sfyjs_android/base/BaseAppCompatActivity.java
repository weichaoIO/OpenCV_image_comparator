package com.jsxfedu.sfyjs_android.base;

import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by pi on 2017/6/2.
 */
abstract public class BaseAppCompatActivity extends AppCompatActivity implements BaseActivityView {
    private static final String TAG = "BaseAppCompatActivity";

    private Toast mToast;

    @Override
    public void showToast(String content) {
        showToast(content, true, Toast.LENGTH_SHORT);
    }

    @Override
    public void showToast(final String content, final boolean isNessesary, final int length) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (content == null) {
                    return;
                }

                if (isNessesary) {
                    if (mToast == null) {
                        mToast = Toast.makeText(BaseAppCompatActivity.this, content, Toast.LENGTH_SHORT);
                    }
                    mToast.setText(content);
                    mToast.setDuration(length);
                    mToast.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = getWebView();
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    BaseAppCompatActivity.super.onBackPressed();
                }
            }
        });
    }

    public WebView getWebView() {
        return null;
    }
}