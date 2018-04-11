package io.weichao.opencv.util;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by chao.wei on 2018/3/30.
 */
public class BitmapUtil {
    private static final String TAG = "BitmapUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private BitmapUtil() {
    }

    public static void matToBitmap(Mat mat, Bitmap bitmap) {
        if (mat == null || mat.empty() || bitmap == null) {
            Log.e(TAG, "mat == null || mat.empty() || bitmap == null");
            return;
        }

        // 将mat转换回位图
        Utils.matToBitmap(mat, bitmap);
    }

    public static void bitmapToMat(Bitmap bitmap, int requestWidth, int requestHeight, Mat mat) {
        if (mat == null || bitmap == null) {
            Log.e(TAG, "mat == null || bitmap == null");
            return;
        }

        Bitmap scaledBitmap = com.jsxfedu.sfyjs_android.util.BitmapUtil.createScaledBitmap(bitmap, requestWidth, requestHeight, false);
        Utils.bitmapToMat(scaledBitmap, mat);
    }
}