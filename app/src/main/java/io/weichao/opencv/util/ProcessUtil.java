package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ProcessUtil {
    private static final String TAG = "ProcessUtil";

    //    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }
    private ProcessUtil() {
    }

    public static Mat gray(Mat mat) {
        if (mat == null) {
            Log.e(TAG, "mat == null");
            return null;
        }

        Mat matCopy = mat.clone();

        Mat grayMat = new Mat();
        Imgproc.cvtColor(matCopy, grayMat, Imgproc.COLOR_BGR2GRAY);
        return grayMat;
    }

    public static Mat threshold(Mat mat) {
        if (mat == null) {
            Log.e(TAG, "mat == null");
            return null;
        }

        Mat matCopy = mat.clone();

        Mat grayMat = new Mat();
        Imgproc.cvtColor(matCopy, grayMat, Imgproc.COLOR_BGR2GRAY);

        Mat thresholdMat = new Mat();
        Imgproc.threshold(grayMat, thresholdMat, 50, 255, Imgproc.THRESH_BINARY);
        return thresholdMat;
    }
}