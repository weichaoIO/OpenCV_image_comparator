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
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;

import io.weichao.opencv.R;
import io.weichao.opencv.util.CompareUtil;

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
        int i = -1;
        if (source != null) {
            Bitmap bitmap = createBitmap(source);
            Mat mat1 = new Mat();
            Mat mat2 = new Mat();
            Utils.bitmapToMat(mBitmap1, mat1);
            Utils.bitmapToMat(bitmap, mat2);
            i = compare(mat1, mat2);
        }

        Handler handler = activity.getHandler();
        if (i != -1) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, i);
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
     * @param dstMat
     */
    public int compare(Mat srcMat, Mat dstMat) {
        if (srcMat.empty() || dstMat.empty()) {
            return -1;
        }

        long ph = CompareUtil.comparePH(srcMat, dstMat);
        Log.e(TAG, "【感知哈希】相似度：" + ph);

        double psnr = CompareUtil.comparePSNR(srcMat, dstMat);
        Log.e(TAG, "【峰值信噪比】相似度：" + psnr);

        double ssim = CompareUtil.compareSSIM(srcMat, dstMat);
        Log.e(TAG, "【结构相似性】相似度：" + ssim);

        double hist = CompareUtil.compareHist(srcMat, dstMat);
        Log.e(TAG, "【直方图】相似度：" + hist);

        return 0;
    }
}