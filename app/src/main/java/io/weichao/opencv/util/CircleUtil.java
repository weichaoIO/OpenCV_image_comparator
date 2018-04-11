package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by chao.wei on 2018/4/2.
 */
public class CircleUtil {
    private static final String TAG = "CircleUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private CircleUtil() {
    }

    public static Mat circleHough(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return null;
        }

        Mat matCopy = mat.clone();

        // 将图像转换成灰度图像
        Mat grayMat = new Mat();
        Imgproc.cvtColor(matCopy, grayMat, Imgproc.COLOR_BGR2GRAY);

        Mat blurMat = new Mat();
        Imgproc.GaussianBlur(grayMat, blurMat, new Size(5, 5), 0);

        // 选择一种边缘检测，检测边缘 我这里采用canny算子检测算法
        Mat edgeMat = new Mat();
        Imgproc.Canny(blurMat, edgeMat, 10, 100);

        Mat circleMat = new Mat();
        Imgproc.HoughCircles(edgeMat, circleMat, Imgproc.CV_HOUGH_GRADIENT, 1, edgeMat.rows() / 15);

        // 新建Mat结构图像用来把直线画在该Mat上
        Mat houghCircles = new Mat();
        houghCircles.create(matCopy.rows(), matCopy.cols(), CvType.CV_8UC1);
        Scalar scalar = new Scalar(255, 0, 0, 255);
        for (int i = 0, colSize = circleMat.cols(); i < colSize; i++) {
            double[] parameters = circleMat.get(0, i);
            if (parameters.length == 3) {
                double x = parameters[0];
                double y = parameters[1];
                int r = (int) parameters[2];
                Point center = new Point(x, y);
                Imgproc.circle(houghCircles, center, r, scalar, 2);
            }
        }
        return houghCircles;
    }
}