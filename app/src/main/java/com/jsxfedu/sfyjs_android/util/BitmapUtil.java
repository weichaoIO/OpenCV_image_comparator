package com.jsxfedu.sfyjs_android.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by pi on 2017/7/5.
 */
public class BitmapUtil {
    private static final String TAG = "BitmapUtil";

    private BitmapUtil() {
    }

    /**
     * 图片裁剪并缩放到指定大小
     *
     * @param imagePath
     * @param requestWidth
     * @param requestHeight
     * @param cropEdgePercent
     * @param filter
     * @return
     */
    public static Bitmap createCropBitmap(String imagePath, int requestWidth, int requestHeight, float cropEdgePercent, boolean filter) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int cropWidthIndex = (int) (width * cropEdgePercent);
        int cropHeightIndex = (int) (height * cropEdgePercent);
        int cropWidth = width - (cropWidthIndex << 1);
        int cropHeight = height - (cropHeightIndex << 1);

        Matrix matrix = new Matrix();
        float scaleX = requestWidth * 1.0f / cropWidth;
        float scaleY = requestHeight * 1.0f / cropHeight;
        matrix.setScale(scaleX, scaleY);

        return Bitmap.createBitmap(bitmap, cropWidthIndex, cropHeightIndex, cropWidth, cropHeight, matrix, filter);
    }

    public static Bitmap createCropBitmap(String imagePath, float cropXPercent, float cropYPercent) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        return createCropBitmap(bitmap, cropXPercent, cropYPercent);
    }

    public static Bitmap createCropBitmap(Bitmap bitmap, float cropXPercent, float cropYPercent) {
        if (cropXPercent < 0) {
            cropXPercent = 0;
        }
        if (cropYPercent < 0) {
            cropYPercent = 0;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int cropWidthIndex = (int) (width * cropXPercent);
        int cropHeightIndex = (int) (height * cropYPercent);
        int cropWidth = width - (cropWidthIndex << 1);
        int cropHeight = height - (cropHeightIndex << 1);
        return Bitmap.createBitmap(bitmap, cropWidthIndex, cropHeightIndex, cropWidth, cropHeight);
    }

    public static Bitmap createScaledBitmap(String imagePath, int requestWidth, int requestHeight, boolean filter) {
        boolean b = FileUtil.isFileExist(imagePath);
        if (!b) {
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            return Bitmap.createScaledBitmap(bitmap, requestWidth, requestHeight, filter);
        }

        return null;
    }

    public static Bitmap createScaledBitmap(Bitmap bitmap, int requestWidth, int requestHeight, boolean filter) {
        if (bitmap != null) {
            return Bitmap.createScaledBitmap(bitmap, requestWidth, requestHeight, filter);
        }

        return null;
    }

    public static Bitmap decodeBitmap(String imagePath, int requestWidth, int requestHeight) {
        if (TextUtils.isEmpty(imagePath)) {
            return null;
        }

        Log.i(TAG, "requestWidth: " + requestWidth);
        Log.i(TAG, "requestHeight: " + requestHeight);
        if (requestWidth <= 0 || requestHeight <= 0) {
            return BitmapFactory.decodeFile(imagePath);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 不加载图片到内存，仅获得图片宽高。
        BitmapFactory.decodeFile(imagePath, options);
        Log.i(TAG, "original height: " + options.outHeight);
        Log.i(TAG, "original width: " + options.outWidth);
        if (options.outHeight == -1 || options.outWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(imagePath);
                int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL); // 获取图片的高度
                int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL); // 获取图片的宽度
                Log.i(TAG, "exif height: " + height);
                Log.i(TAG, "exif width: " + width);
                options.outWidth = width;
                options.outHeight = height;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        options.inSampleSize = calculateInSampleSize(options, requestWidth, requestHeight); // 计算获取新的采样率
        Log.i(TAG, "inSampleSize: " + options.inSampleSize);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        Log.i(TAG, "height: " + height);
        Log.i(TAG, "width: " + width);
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            long totalPixels = width * height / inSampleSize;
            long totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }

    public static void saveBitmap(String filePath, Bitmap bitmap) {
        FileUtil.doFileExist(filePath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 以1个像素对应4个字节，将bitmap转为byte数组。
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2byteArray(Bitmap bitmap) {
        int byteCount = bitmap.getByteCount();
        ByteBuffer byteBuffer = ByteBuffer.allocate(byteCount);
        bitmap.copyPixelsToBuffer(byteBuffer);
        return byteBuffer.array();
    }

    public static String encodeToString(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) {
            return null;
        }

        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        // 创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
        bitmap.compress(format, 100, baos);
        // 将字节数组输出流转化为字节数组byte[]
        byte[] data = baos.toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static Bitmap createSquareCropBitmap(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        return createSquareCropBitmap(bitmap);
    }

    public static Bitmap createSquareCropBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int cropWidthIndex = 0, cropHeightIndex = 0;
        int cropWidth = width;
        int cropHeight = height;
        if (width == height) {
            return bitmap;
        } else if (width > height) {
            cropWidthIndex = (width - height) >> 1;
            cropWidth = height;
        } else {
            cropHeightIndex = (height - width) >> 1;
            cropHeight = width;
        }

        return Bitmap.createBitmap(bitmap, cropWidthIndex, cropHeightIndex, cropWidth, cropHeight);
    }

    public static Bitmap createRatioCropBitmap(Bitmap bitmap, float ratio) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (height * 1.0f / width == ratio) {
            return bitmap;
        } else {
            int cropWidthIndex = 0, cropHeightIndex = 0;
            int cropWidth = (int) (height * 1.0f / ratio);
            int cropHeight = (int) (width * ratio);
            if (width > cropWidth) {
                cropWidthIndex = (width - cropWidth) >> 1;
                cropHeight = height;
            } else {
                cropHeightIndex = (height - cropHeight) >> 1;
                cropWidth = width;
            }
            return Bitmap.createBitmap(bitmap, cropWidthIndex, cropHeightIndex, cropWidth, cropHeight);
        }
    }

    public static AutoCropPercent getAutoCropPercent(Bitmap bitmap, float previewRatio) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float pictureRatio = height * 1.0f / width;
        return getAutoCropPercent(pictureRatio, previewRatio);
    }

    public static AutoCropPercent getAutoCropPercent(float pictureRatio, float previewRatio) {
        if (pictureRatio == previewRatio) {
            return new AutoCropPercent(0, 0);
        } else {
            float sqrt = (float) (Math.sqrt((1 + previewRatio) / (1 + pictureRatio)));
            float ap = (pictureRatio - previewRatio) / (2 * pictureRatio) - 1 + sqrt;
            float bp = 1.0f / sqrt - 1;
            return new AutoCropPercent(-bp, ap);
        }
    }

    public static class AutoCropPercent {
        private float xp;
        private float yp;

        public AutoCropPercent() {
        }

        public AutoCropPercent(float xp, float yp) {
            this.xp = xp;
            this.yp = yp;
        }

        public float getXp() {
            return xp;
        }

        public void setXp(float xp) {
            this.xp = xp;
        }

        public float getYp() {
            return yp;
        }

        public void setYp(float yp) {
            this.yp = yp;
        }
    }
}
