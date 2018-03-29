package io.weichao.opencv.util;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by chao.wei on 2018/3/29.
 */
public class CompareUtil {
    private static final String TAG = "CompareUtil";

    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv");
    }

    private CompareUtil() {
    }

    public static native double nativeComparePSNR(long mat1, long mat2);

    public static native double nativeCompareSSIM(long mat1, long mat2);

    public static native long nativeComparePH(long mat1, long mat2);

    public static double comparePSNR(Mat srcMat, Mat dstMat) {
        Mat mat1 = srcMat.clone();
        Mat mat2 = dstMat.clone();
        return nativeComparePSNR(mat1.getNativeObjAddr(), mat2.getNativeObjAddr());
    }

    public static double compareSSIM(Mat srcMat, Mat dstMat) {
        Mat mat1 = srcMat.clone();
        Mat mat2 = dstMat.clone();
        return nativeCompareSSIM(mat1.getNativeObjAddr(), mat2.getNativeObjAddr());
    }

    public static long comparePH(Mat srcMat, Mat dstMat) {
        Mat mat1 = srcMat.clone();
        Mat mat2 = dstMat.clone();
        return nativeComparePH(mat1.getNativeObjAddr(), mat2.getNativeObjAddr());
    }

    public static double compareHist(Mat srcMat, Mat dstMat) {
        Mat mat1 = srcMat.clone();
        Mat mat2 = dstMat.clone();
        Mat grayMat1 = new Mat();
        Mat grayMat2 = new Mat();
        Imgproc.cvtColor(mat1, grayMat1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2, grayMat2, Imgproc.COLOR_BGR2GRAY);
        Mat histConvertMat1 = new Mat();
        Mat histConvertMat2 = new Mat();
        grayMat1.convertTo(histConvertMat1, CvType.CV_32F);
        grayMat2.convertTo(histConvertMat2, CvType.CV_32F);
        return Imgproc.compareHist(histConvertMat1, histConvertMat2, Imgproc.CV_COMP_CORREL);
    }
}