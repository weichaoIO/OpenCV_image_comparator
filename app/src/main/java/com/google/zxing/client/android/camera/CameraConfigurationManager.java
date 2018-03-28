package com.google.zxing.client.android.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.zxing.client.android.CameraActivity;
import com.google.zxing.client.android.camera.open.OpenCamera;
import com.jsxfedu.sfyjs_android.util.BitmapUtil;
import com.jsxfedu.sfyjs_android.util.HardwareInfoUtil;

import java.util.HashMap;
import java.util.List;

final class CameraConfigurationManager {
    private static final String TAG = "CameraConfiguration";

    private final Context context;

    private Point cameraPreviewResolution;
    private Point cameraPictureResolution;
    private BitmapUtil.AutoCropPercent mAutoCropPercent = new BitmapUtil.AutoCropPercent(0.0f, 0.0f);

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    public void initFromCameraParameters(OpenCamera camera) {
        int cwRotationFromNaturalToCamera = camera.getOrientation();
        Log.i(TAG, "相机方向: " + cwRotationFromNaturalToCamera);

        DisplayMetrics displayMetrics = HardwareInfoUtil.getRealDisplayMetrics(context);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.i(TAG, "屏幕分辨率:（" + width + "," + height + ")");

        Point tempP;
        if (width < height) {
            tempP = new Point(height, width);
        } else {
            tempP = new Point(width, height);
        }
        cameraPreviewResolution = findBestPreviewSizeValue(camera.getCamera(), tempP);// 176x144 320x240 352x288 480x320 480x368 640x480 720x480 800x480 800x600 864x480 960x540 1280x720 1440x1080 1920x1080 1920x1088 1872x1120
        Log.i(TAG, "相机可用的显示效果最佳的预览分辨率: " + cameraPreviewResolution);// Point(800, 480)

        cameraPictureResolution = findBestPictureSizeValue(camera.getCamera(), cameraPreviewResolution.y * 1.0f / cameraPreviewResolution.x);// 320x240 640x480 1024x768 1280x720 1280x768 1280x960 1600x1200 1920x1088 2560x1440 2560x1920 2880x1728 3264x2448 3328x1872 2880x1728 3600x2160
        Log.i(TAG, "相机可用的匹配相机预览分辨率比例的拍照分辨率: " + cameraPictureResolution);// Point(2880, 1728)
    }

    /**
     * 获取最佳的相机预览分辨率
     *
     * @param camera
     * @param screenResolution
     * @return
     */
    private Point findBestPreviewSizeValue(Camera camera, Point screenResolution) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (defaultSize == null) {
                throw new IllegalStateException("Parameters contained no preview size!");
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        if (Log.isLoggable(TAG, Log.INFO)) {
            StringBuilder previewSizesString = new StringBuilder();
            for (Camera.Size size : rawSupportedSizes) {
                previewSizesString.append(size.width).append('x').append(size.height).append(' ');
            }
            Log.i(TAG, "Supported preview sizes: " + previewSizesString);
        }

        Camera.Size bestSize = camera.new Size(screenResolution.x, screenResolution.y);
        if (rawSupportedSizes.contains(bestSize)) {
            return new Point(bestSize.width, bestSize.height);
        }

        int maxHeight = 0;
        bestSize = null;
        float screenRatio = screenResolution.y * 1.0f / screenResolution.x;
        float bestRatio = Integer.MAX_VALUE;
        for (Camera.Size size : rawSupportedSizes) {
            float aspectRatio = size.height * 1.0f / size.width;
            float ratioSub = Math.abs(aspectRatio - screenRatio);
            if (ratioSub < bestRatio) {
                bestRatio = ratioSub;
                maxHeight = size.height;
                bestSize = size;
            } else if (ratioSub == bestRatio) {
                int sizeHeight2ScreenHeight = Math.abs(size.height - screenResolution.y);
                int maxHeight2ScreenHeight = Math.abs(maxHeight - screenResolution.y);
                if ((sizeHeight2ScreenHeight < maxHeight2ScreenHeight) || (sizeHeight2ScreenHeight == maxHeight2ScreenHeight && size.height > screenResolution.y)) {
                    maxHeight = size.height;
                    bestSize = size;
                }
            }
        }

        // If no exact match, use largest preview size. This was not a great idea on older devices because
        // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
        // the CPU is much more powerful.
        if (bestSize != null) {
            return new Point(bestSize.width, bestSize.height);
        }

        // If there is nothing at all suitable, return current preview size
        Camera.Size defaultPreview = parameters.getPreviewSize();
        if (defaultPreview == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
        return defaultSize;
    }

    /**
     * 获取最佳的相机拍照分辨率
     *
     * @param camera
     * @param ratio
     * @return
     */
    private Point findBestPictureSizeValue(Camera camera, float ratio) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPictureSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported picture sizes; using default");
            Camera.Size defaultSize = parameters.getPictureSize();
            if (defaultSize == null) {
                throw new IllegalStateException("Parameters contained no picture size!");
            }
            return new Point(defaultSize.width, defaultSize.height);
        }

        if (Log.isLoggable(TAG, Log.INFO)) {
            StringBuilder pictureSizesString = new StringBuilder();
            for (Camera.Size size : rawSupportedSizes) {
                pictureSizesString.append(size.width).append('x').append(size.height).append(' ');
            }
            Log.i(TAG, "Supported picture sizes: " + pictureSizesString);
        }

        int pictureFitPreviewWidth = (int) (CameraActivity.PICTURE_WIDTH / ratio);
        Camera.Size bestSize = camera.new Size(pictureFitPreviewWidth, CameraActivity.PICTURE_WIDTH);
        if (rawSupportedSizes.contains(bestSize)) {
            return new Point(bestSize.width, bestSize.height);
        }

        int minLength = Integer.MAX_VALUE;
        int maxY = 0;
        bestSize = null;
        boolean hasLegalBestSize = false;
        HashMap<Float, BitmapUtil.AutoCropPercent> autoCropPercentMap = new HashMap<>();
        StringBuilder pictureFitPreviewSizesString = new StringBuilder();
        for (Camera.Size size : rawSupportedSizes) {
            float aspectRatio = size.height * 1.0f / size.width;

            BitmapUtil.AutoCropPercent autoCropPercent;
            if (autoCropPercentMap.containsKey(aspectRatio)) {
                autoCropPercent = autoCropPercentMap.get(aspectRatio);
            } else {
                autoCropPercent = BitmapUtil.getAutoCropPercent(aspectRatio, ratio);
                autoCropPercentMap.put(aspectRatio, autoCropPercent);
            }

            float yMargin = CameraActivity.RED_LINE_MARGIN + autoCropPercent.getYp();
            int y = (int) (size.height * (1 - 2 * yMargin) + 0.5f);

            if (hasLegalBestSize && y < CameraActivity.PICTURE_WIDTH) {
                continue;
            }

            if (!hasLegalBestSize && y >= CameraActivity.PICTURE_WIDTH) {
                hasLegalBestSize = true;
                pictureFitPreviewSizesString.append(size.width).append('x').append(size.height).append(' ');
                minLength = size.height * size.width;
                bestSize = size;
                mAutoCropPercent = autoCropPercent;
                continue;
            }

            if (hasLegalBestSize) {
                pictureFitPreviewSizesString.append(size.width).append('x').append(size.height).append(' ');
            }

            if (hasLegalBestSize) {
                int length = size.height * size.width;
                if (length < minLength) {
                    minLength = length;
                    bestSize = size;
                    mAutoCropPercent = autoCropPercent;
                }
            } else {
                if (y > maxY) {
                    maxY = y;
                    bestSize = size;
                    mAutoCropPercent = autoCropPercent;
                }
            }
        }
        Log.i(TAG, "Supported picture fit preview sizes: " + pictureFitPreviewSizesString);

        // If no exact match, use largest picture size. This was not a great idea on older devices because
        // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
        // the CPU is much more powerful.
        if (bestSize != null) {
            return new Point(bestSize.width, bestSize.height);
        }

        // If there is nothing at all suitable, return current picture size
        Camera.Size defaultPicture = parameters.getPictureSize();
        if (defaultPicture == null) {
            throw new IllegalStateException("Parameters contained no picture size!");
        }

        Point defaultSize = new Point(defaultPicture.width, defaultPicture.height);
        Log.i(TAG, "No suitable picture sizes, using default: " + defaultSize);
        return defaultSize;
    }

    /**
     * 设置相机参数
     *
     * @param camera
     * @param safeMode
     */
    public void setDesiredCameraParameters(OpenCamera camera, boolean safeMode) {
        Camera theCamera = camera.getCamera();
        Camera.Parameters parameters = theCamera.getParameters();

        if (parameters == null) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }

        Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

        if (safeMode) {
            Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
        }

        CameraConfigurationUtils.setFocus(parameters, true, true, safeMode);

        if (!safeMode) {
            CameraConfigurationUtils.setBarcodeSceneMode(parameters);
            CameraConfigurationUtils.setVideoStabilization(parameters);
            CameraConfigurationUtils.setFocusArea(parameters);
            CameraConfigurationUtils.setMetering(parameters);
        }

        parameters.setPreviewSize(cameraPreviewResolution.x, cameraPreviewResolution.y);

        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);
        parameters.setPictureSize(cameraPictureResolution.x, cameraPictureResolution.y);

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes != null && supportedFlashModes.size() > 0) {
            if (Log.isLoggable(TAG, Log.INFO)) {
                StringBuilder builder = new StringBuilder();
                for (String mode : supportedFlashModes) {
                    builder.append(mode).append(' ');
                }
                Log.i(TAG, "支持的闪光模式: " + builder);
            }
            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            }
        } else {
            Log.i(TAG, "不支持任何闪光模式");
        }

        theCamera.setParameters(parameters);

        Camera.Parameters afterParameters = theCamera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize != null && (cameraPreviewResolution.x != afterSize.width || cameraPreviewResolution.y != afterSize.height)) {
            Log.w(TAG, "Camera said it supported preview size " + cameraPreviewResolution.x + 'x' + cameraPreviewResolution.y +
                    ", but after setting it, preview size is " + afterSize.width + 'x' + afterSize.height);
            cameraPreviewResolution.x = afterSize.width;
            cameraPreviewResolution.y = afterSize.height;
        }
    }

    /**
     * 预览分辨率
     *
     * @return
     */
    public Point getCameraPreviewResolution() {
        return cameraPreviewResolution;
    }

    public BitmapUtil.AutoCropPercent getAutoCropPercent() {
        String model = android.os.Build.MODEL;
        Log.d(TAG, model);
        if ("Y80".equals(model)) {// 长虹 Y80 同一长、宽比例的分辨率表示的不是同一区域
            float margin = 0.065f;// 预览 800x480，拍照 2880x1728
            return new BitmapUtil.AutoCropPercent(mAutoCropPercent.getXp() + margin, mAutoCropPercent.getYp() + margin);
        } else {
            return mAutoCropPercent;
        }
    }
}