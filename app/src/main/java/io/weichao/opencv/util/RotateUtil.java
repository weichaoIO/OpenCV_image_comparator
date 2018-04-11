package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chao.wei on 2018/4/2.
 */
public class RotateUtil {
    private static final String TAG = "RotateUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private RotateUtil() {
    }

    public static Mat rotate(Mat mat) {
        RotatedRect rotatedRect = getRotatedRect(mat);
        if (mat == null || mat.empty() || rotatedRect == null) {
            Log.e(TAG, "mat == null || mat.empty() || rotatedRect == null");
            return null;
        }

        Mat matCopy = mat.clone();

        Mat rotationMatrix2DMat = Imgproc.getRotationMatrix2D(new Point(matCopy.rows() / 2, matCopy.cols() / 2), rotatedRect.angle, 1);
        Mat regionMat = new Mat(matCopy.rows(), matCopy.cols(), CvType.CV_8UC3, new Scalar(0, 0, 0));
        Imgproc.warpAffine(matCopy, regionMat, rotationMatrix2DMat, new Size(matCopy.rows(), matCopy.cols()));
        return regionMat;
    }

    private static RotatedRect getRotatedRect(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return null;
        }

        Mat matCopy = mat.clone();

        // 将图像转换成灰度图像
        Mat grayMat = new Mat();
        Imgproc.cvtColor(matCopy, grayMat, Imgproc.COLOR_BGR2GRAY);

        // 获取边缘
        Mat edgeMat = new Mat();
        Imgproc.Canny(grayMat, edgeMat, 10, 100);

        // 找出最外轮廓
        List<MatOfPoint> contourList = new ArrayList<>();
        Mat hierarchyMat = new Mat();
        Imgproc.findContours(edgeMat, contourList, hierarchyMat, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return Imgproc.minAreaRect(new MatOfPoint2f(contourList.get(0).toArray()));
    }
}