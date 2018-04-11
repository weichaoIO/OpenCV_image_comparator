package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by chao.wei on 2018/3/29.
 */
public class CornerUtil {
    private static final String TAG = "CornerUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private CornerUtil() {
    }

    public static Mat cornerHarris(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return null;
        }

        Mat matCopy = mat.clone();

        // 将图像转化成灰度图像
        Mat grayMat = new Mat();
        Imgproc.cvtColor(matCopy, grayMat, Imgproc.COLOR_BGR2GRAY);

        // 找出角点
        Mat cornerMat = new Mat();
        Imgproc.cornerHarris(grayMat, cornerMat, 2, 3, 0.01);

        // 归一化Harris角点的输出
        Mat normCornerMat = new Mat();
        Core.normalize(cornerMat, normCornerMat, 0.00001, 255, Core.NORM_MINMAX);

        // 在新的图像上绘制角点
        Scalar scalar = new Scalar(255, 0, 0, 255);
        for (int i = 0, colSize = normCornerMat.cols(); i < colSize; i++) {
            for (int j = 0, rowSize = normCornerMat.rows(); j < rowSize; j++) {
                double[] value = normCornerMat.get(j, i);
                if (value[0] > 150) {
                    Imgproc.circle(matCopy, new Point(i, j), 10, scalar, -1);
                }
            }
        }

        return matCopy;
    }
}