package com.google.zxing.client.android;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.client.android.camera.CameraManager;
import com.jsxfedu.sfyjs_android.base.BaseAppCompatActivity;
import com.jsxfedu.sfyjs_android.util.HardwareInfoUtil;

import java.io.IOException;

import io.weichao.opencv.R;

public final class CameraActivity extends BaseAppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "CameraActivity";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    public static final int PICTURE_WIDTH = 1080;
    public static final float RED_LINE_MARGIN = 1.0f / 6;

    private View mFragmentCamera;
    private SurfaceView mSurfaceView;

    private CameraManager mCameraManager;
    private CameraActivityHandler mCameraActivityHandler;

    private boolean mHasSurface;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_camera);

        RelativeLayout root = findViewById(R.id.root);

        mFragmentCamera = findViewById(R.id.fragment_camera);
        mSurfaceView = findViewById(R.id.surface_view);

        DisplayMetrics displayMetrics = HardwareInfoUtil.getRealDisplayMetrics(this);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        root.setLayoutParams(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFragmentCamera.getVisibility() == View.VISIBLE) {
            startPreview();
        }
    }

    @Override
    protected void onPause() {
        stopPreview();
        super.onPause();
    }

    private void startPreview() {
        mCameraManager = new CameraManager(this);
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
    }

    private void stopPreview() {
        if (mCameraActivityHandler != null) {
            mCameraActivityHandler.quitSynchronously();
            mCameraActivityHandler = null;
        }
        if (mCameraManager != null) {
            mCameraManager.closeDriver();
            mCameraManager = null;
        }
        if (!mHasSurface) {
            SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * 初始化 Camera，初始化　Handler。
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            mCameraManager.openDriver(surfaceHolder);
            mCameraActivityHandler = new CameraActivityHandler(this, mCameraManager);

            DisplayMetrics displayMetrics = HardwareInfoUtil.getRealDisplayMetrics(this);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            int redLineIvMarginTB = (int) (height * RED_LINE_MARGIN);
            int redLineIvHeight = height - (2 * redLineIvMarginTB);

            Point cameraPreviewResolution = mCameraManager.getCameraPreviewResolution();
            int redLineIvWidth = (int) (redLineIvHeight * ((cameraPreviewResolution.y * width * 1.0f) / (cameraPreviewResolution.x * height)));
            int redLineIvMarginLR = (width - redLineIvWidth) >> 1;

            ImageView redLineIv = findViewById(R.id.red_line_iv);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(redLineIvMarginLR, redLineIvMarginTB, redLineIvMarginLR, redLineIvMarginTB);
            redLineIv.setLayoutParams(layoutParams);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
        }
    }

    public void handleDecode(Integer i) {
        restartPreviewAfterDelay(1000);
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    public Handler getHandler() {
        return mCameraActivityHandler;
    }

    /**
     * 重新开始扫描。
     *
     * @param delayMS
     */
    public void restartPreviewAfterDelay(long delayMS) {
        if (mCameraActivityHandler != null) {
            mCameraActivityHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }
}