package com.google.zxing.client.android.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.client.android.CameraActivity;
import com.google.zxing.client.android.camera.open.OpenCamera;
import com.google.zxing.client.android.camera.open.OpenCameraInterface;
import com.jsxfedu.sfyjs_android.util.BitmapUtil;
import com.jsxfedu.sfyjs_android.util.HardwareInfoUtil;

import java.io.IOException;

public final class CameraManager {
    private static final String TAG = "CameraManager";

    public static final int MIN_WIDTH = 240;

    private CameraActivity activity;
    private final CameraConfigurationManager configManager;
    private OpenCamera camera;
    private AutoFocusManager autoFocusManager;
    private Rect framingRectInPreview;
    private boolean initialized;
    private boolean previewing;
    private final PreviewCallback previewCallback;
    private float mPreviewPercentScreenX, mPreviewPercentScreenY;
    private int mDivide;

    public CameraManager(CameraActivity activity) {
        this.activity = activity;

        this.configManager = new CameraConfigurationManager(activity);
        previewCallback = new PreviewCallback(configManager);
    }

    public synchronized void openDriver(SurfaceHolder holder) throws IOException {
        OpenCamera theCamera = camera;
        if (theCamera == null) {
            theCamera = OpenCameraInterface.open(OpenCameraInterface.NO_REQUESTED_CAMERA);
            if (theCamera == null) {
                throw new IOException("Camera.open() failed to return object from driver");
            }
            camera = theCamera;
        }

        if (!initialized) {
            initialized = true;
            configManager.initFromCameraParameters(theCamera);
        }

        Camera cameraObject = theCamera.getCamera();
        Camera.Parameters parameters = cameraObject.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten();
        try {
            configManager.setDesiredCameraParameters(theCamera, false);
        } catch (RuntimeException re) {
            Log.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
            Log.i(TAG, "Resetting to saved camera params: " + parametersFlattened);
            if (parametersFlattened != null) {
                parameters = cameraObject.getParameters();
                parameters.unflatten(parametersFlattened);
                try {
                    cameraObject.setParameters(parameters);
                    configManager.setDesiredCameraParameters(theCamera, true);
                } catch (RuntimeException re2) {
                    Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
                }
            }
        }
        cameraObject.setPreviewDisplay(holder);
    }

    public synchronized boolean isOpen() {
        return camera != null;
    }

    public synchronized void closeDriver() {
        if (camera != null) {
            camera.getCamera().release();
            camera = null;
        }
        activity = null;
    }

    public Point getCameraPreviewResolution() {
        return configManager.getCameraPreviewResolution();
    }

    private BitmapUtil.AutoCropPercent getAutoCropPercent() {
        return configManager.getAutoCropPercent();
    }

    public synchronized void startPreview() {
        OpenCamera theCamera = camera;
        if (theCamera != null && !previewing) {
            theCamera.getCamera().startPreview();
            previewing = true;
            autoFocusManager = new AutoFocusManager(activity, theCamera.getCamera());
        }
    }

    public synchronized void stopPreview() {
        if (autoFocusManager != null) {
            autoFocusManager.stop();
            autoFocusManager = null;
        }
        if (camera != null && previewing) {
            camera.getCamera().stopPreview();
            previewCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    public void requestPreviewFrame(Handler handler, int message) {
        OpenCamera theCamera = camera;
        if (theCamera != null && previewing) {
            previewCallback.setHandler(handler, message);
            theCamera.getCamera().setOneShotPreviewCallback(previewCallback);
        }
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview(width, height);
        if (rect == null) {
            return null;
        }

        int divide = getDivide(rect, MIN_WIDTH);

        // 可能采样数据。
        byte[] dataCropped = new byte[data.length / divide];
        for (int sy = 0, ry = 0; sy < height; ry++, sy += divide) {
            for (int sx = 0, rx = 0; sx < width; rx++, sx += divide) {
                int dataIndex = sx + sy * width;
                int dataCroppedIndex = rx + ry * width;
                dataCropped[dataCroppedIndex] = data[dataIndex];
            }
        }

        // 生成解析二维码的数据源，数据dataCropped：宽width、高height、裁剪从(rect.left, rect.top)开始，裁减宽rect.width()、高rect.height()，不水平翻转。
        return new PlanarYUVLuminanceSource(dataCropped, width, height, rect.left / divide, rect.top / divide, rect.width() / divide, rect.height() / divide, false);
    }

    /**
     * 获取采样数据系数
     *
     * @param rect
     * @return
     */
    private int getDivide(Rect rect, int limit) {
        if (mDivide == 0) {
            int minDivide = Math.min(rect.width() / limit, rect.height() / limit);
            mDivide = Math.max(1, minDivide);
        }
        return mDivide;
    }

    /**
     * 获取扫描框矩形区域对应到相机预览矩形区域的部分
     *
     * @return
     */
    private synchronized Rect getFramingRectInPreview(int width, int height) {
        if (framingRectInPreview == null) {
            DisplayMetrics displayMetrics = HardwareInfoUtil.getRealDisplayMetrics(activity);
            int heightPixels = displayMetrics.heightPixels;
            int widthPixels = displayMetrics.widthPixels;

            int length = (int) (height * (1 - 2 * CameraActivity.RED_LINE_MARGIN));
            float scale = (widthPixels * 1.0f / heightPixels) / (width * 1.0f / height);
            int scaledWidth = (int) (length * scale);

            mPreviewPercentScreenY = height * 1.0f / heightPixels;
            mPreviewPercentScreenX = width * 1.0f / widthPixels * scale;

            Rect rect = new Rect();
            rect.left = (width - scaledWidth) >> 1;
            rect.right = (width + scaledWidth) >> 1;
            rect.top = (height - length) >> 1;
            rect.bottom = (height + length) >> 1;

            framingRectInPreview = rect;
            Log.d(TAG, "framingRectInPreview: " + framingRectInPreview);
        }
        return framingRectInPreview;
    }
}