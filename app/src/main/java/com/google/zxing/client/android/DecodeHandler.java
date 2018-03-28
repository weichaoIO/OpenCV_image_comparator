package com.google.zxing.client.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.PlanarYUVLuminanceSource;
import com.jsxfedu.sfyjs_android.util.BitmapUtil;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;

import io.weichao.opencv.R;

final class DecodeHandler extends Handler {
    private static final String TAG = "DecodeHandler";

    private final CameraActivity activity;
    private boolean running = true;
    private Bitmap mBitmap1;

    DecodeHandler(CameraActivity activity) {
        this.activity = activity;

        mBitmap1 = BitmapFactory.decodeResource(activity.getResources(), R.drawable.test);
    }

    @Override
    public void handleMessage(Message message) {
        if (!running) {
            return;
        }
        switch (message.what) {
            case R.id.decode:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                running = false;
                Looper.myLooper().quit();
                break;
        }
    }

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        PlanarYUVLuminanceSource source = activity.getCameraManager().buildLuminanceSource(data, width, height);
        float f = -1;
        if (source != null) {
            Bitmap bitmap = createBitmap(source);
            Mat mat1 = new Mat();
            Mat mat2 = new Mat();
            Mat mat11 = new Mat();
            Mat mat22 = new Mat();
            Utils.bitmapToMat(mBitmap1, mat1);
            Utils.bitmapToMat(bitmap, mat2);
            Imgproc.cvtColor(mat1, mat11, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(mat2, mat22, Imgproc.COLOR_BGR2GRAY);
            f = comPareHist(mat11, mat22);
        }

        Handler handler = activity.getHandler();
        if (f != -1) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, f);
//                Bundle bundle = new Bundle();
//                bundleThumbnail(source, bundle);
//                message.setData(bundle);
                message.sendToTarget();
            }
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }
    }

    private Bitmap createBitmap(PlanarYUVLuminanceSource source) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        return BitmapUtil.createScaledBitmap(bitmap, mBitmap1.getWidth(), mBitmap1.getHeight(), true);
    }

    /**
     * 比较来个矩阵的相似度
     *
     * @param srcMat
     * @param desMat
     */
    public float comPareHist(Mat srcMat, Mat desMat) {
        srcMat.convertTo(srcMat, CvType.CV_32F);
        desMat.convertTo(desMat, CvType.CV_32F);
        return (float) Imgproc.compareHist(srcMat, desMat, Imgproc.CV_COMP_CORREL);
    }
}