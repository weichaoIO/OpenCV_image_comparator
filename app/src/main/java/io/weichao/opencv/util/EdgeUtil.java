package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by chao.wei on 2018/3/29.
 */
public class EdgeUtil {
    private static final String TAG = "EdgeUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private EdgeUtil() {
    }

    public static Mat edgeCanny(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.d(TAG, "mat == null || mat.empty()");
            return null;
        }

        Mat matCopy = mat.clone();

        // 将图像转换成灰度图像
        Mat grayMat = new Mat();
        Imgproc.cvtColor(matCopy, grayMat, Imgproc.COLOR_BGR2GRAY);

        Mat edgeMat = new Mat();
        Imgproc.Canny(grayMat, edgeMat, 10, 100);
        return edgeMat;
    }

    public static Mat edgeSobel(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.d(TAG, "mat == null || mat.empty()");
            return null;
        }

        Mat matCopy = mat.clone();

        // 将图像转换成灰度图像
        Mat grayMat = new Mat();
        Imgproc.cvtColor(matCopy, grayMat, Imgproc.COLOR_BGR2GRAY);

        // 计算水平方向的梯度
        Mat gradX = new Mat();
        Imgproc.Sobel(grayMat, gradX, CvType.CV_16S, 1, 0, 3, 1, 0);
        // 计算垂直方向的梯度
        Mat gradY = new Mat();
        Imgproc.Sobel(grayMat, gradY, CvType.CV_16S, 0, 1, 3, 1, 0);
        // 计算两个方向上的梯度绝对值
        Mat absGradX = new Mat();
        Mat absGradY = new Mat();
        Core.convertScaleAbs(gradX, absGradX);
        Core.convertScaleAbs(gradY, absGradY);

        // 计算结果梯度
        Mat sobelMat = new Mat();
        Core.addWeighted(absGradX, 0.5, absGradY, 0.5, 1, sobelMat);
        return sobelMat;
    }

    public static Mat edgeGaussian(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.d(TAG, "mat == null || mat.empty()");
            return null;
        }

        Mat matCopy = mat.clone();

        // 将图像转换成灰度图像
        Mat grayMat = new Mat();
        Imgproc.cvtColor(matCopy, grayMat, Imgproc.COLOR_BGR2GRAY);


        // 以两个不同模糊半径对图像进行模糊处理
        Mat blur1 = new Mat();
        Mat blur2 = new Mat();
        Imgproc.GaussianBlur(grayMat, blur1, new Size(15, 15), 5);
        Imgproc.GaussianBlur(grayMat, blur2, new Size(21, 21), 5);

        // 将两幅模糊后的图像相减
        Mat tmpMat = new Mat();
        Core.absdiff(blur1, blur2, tmpMat);

        // 二值化
        Core.multiply(tmpMat, new Scalar(100), tmpMat);
        return tmpMat;
    }
}