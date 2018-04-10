package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chao.wei on 2018/3/29.
 */
public class ContourUtil {
    private static final String TAG = "ContourUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private ContourUtil() {
    }

    public static Mat contours(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.d(TAG, "mat == null || mat.empty()");
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

        // 在新的图像上绘制轮廓
        Mat contousMat = new Mat();
        contousMat.create(edgeMat.rows(), edgeMat.cols(), CvType.CV_8UC3);
        Scalar scalar = new Scalar(255, 255, 255, 255);
        for (int i = 0, size = contourList.size(); i < size; i++) {
            Imgproc.drawContours(contousMat, contourList, i, scalar);
        }
        return contousMat;
    }
}