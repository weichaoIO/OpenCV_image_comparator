package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chao.wei on 2018/3/29.
 */
public class CompareUtil {
    private static final String TAG = "CompareUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private CompareUtil() {
    }

    private static native double nativeComparePSNR(long mat1, long mat2);

    private static native double nativeCompareSSIM(long mat1, long mat2);

    private static native long nativeComparePH(long mat1, long mat2);

    public static double comparePSNR(Mat mat1, Mat mat2) {
        if (mat1.empty() || mat2.empty()) {
            Log.d(TAG, "mat1.empty() || mat2.empty()");
            return -1;
        }

        Mat mat1Copy = mat1.clone();
        Mat mat2Copy = mat2.clone();
        return nativeComparePSNR(mat1Copy.getNativeObjAddr(), mat2Copy.getNativeObjAddr());
    }

    public static double compareSSIM(Mat mat1, Mat mat2) {
        if (mat1.empty() || mat2.empty()) {
            Log.d(TAG, "mat1.empty() || mat2.empty()");
            return -1;
        }

        Mat mat1Copy = mat1.clone();
        Mat mat2Copy = mat2.clone();
        return nativeCompareSSIM(mat1Copy.getNativeObjAddr(), mat2Copy.getNativeObjAddr());
    }

    public static long comparePH(Mat mat1, Mat mat2) {
        if (mat1.empty() || mat2.empty()) {
            Log.d(TAG, "mat1.empty() || mat2.empty()");
            return -1;
        }

        Mat mat1Copy = mat1.clone();
        Mat mat2Copy = mat2.clone();
        return nativeComparePH(mat1Copy.getNativeObjAddr(), mat2Copy.getNativeObjAddr());
    }

    public static double compareHist(Mat mat1, Mat mat2) {
        if (mat1.empty() || mat2.empty()) {
            Log.d(TAG, "mat1.empty() || mat2.empty()");
            return -1;
        }

        Mat mat1Copy = mat1.clone();
        Mat mat2Copy = mat2.clone();
        Mat grayMat1 = new Mat();
        Mat grayMat2 = new Mat();
        Imgproc.cvtColor(mat1Copy, grayMat1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2Copy, grayMat2, Imgproc.COLOR_BGR2GRAY);
        Mat histConvertMat1 = new Mat();
        Mat histConvertMat2 = new Mat();
        grayMat1.convertTo(histConvertMat1, CvType.CV_32F);
        grayMat2.convertTo(histConvertMat2, CvType.CV_32F);
        return Imgproc.compareHist(histConvertMat1, histConvertMat2, Imgproc.CV_COMP_CORREL);
    }

    public static Mat matches(Mat mat1, Mat mat2) {
        if (mat1.empty() || mat2.empty()) {
            Log.d(TAG, "mat1.empty() || mat2.empty()");
            return null;
        }

        Mat mat1Copy = mat1.clone();
        Mat mat2Copy = mat2.clone();

        // 转灰度图
        Mat grayMat1 = new Mat();
        Mat grayMat2 = new Mat();
        Imgproc.cvtColor(mat1Copy, grayMat1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2Copy, grayMat2, Imgproc.COLOR_BGR2GRAY);

        // 二值化处理
        Mat thresholdMat1 = new Mat();
        Mat thresholdMat2 = new Mat();
        Imgproc.threshold(grayMat1, thresholdMat1, 50, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(grayMat2, thresholdMat2, 50, 255, Imgproc.THRESH_BINARY);

        /* 获取matches */
        MatOfKeyPoint matOfKeyPoint1 = new MatOfKeyPoint();
        MatOfKeyPoint matOfKeyPoint2 = new MatOfKeyPoint();
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.ORB);
        featureDetector.detect(thresholdMat1, matOfKeyPoint1);
        featureDetector.detect(thresholdMat2, matOfKeyPoint2);

        Mat descriptorMat1 = new Mat();
        Mat descriptorMat2 = new Mat();
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        descriptorExtractor.compute(thresholdMat1, matOfKeyPoint1, descriptorMat1);
        descriptorExtractor.compute(thresholdMat2, matOfKeyPoint2, descriptorMat2);

        MatOfDMatch matches = new MatOfDMatch();
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        descriptorMatcher.match(descriptorMat1, descriptorMat2, matches);
        /* 获取matches */

        // 将matches转成API需要的格式
        List<DMatch> matchesList = matches.toList();
        LinkedList<DMatch> listOfGoodMatches = new LinkedList<>();
        for (int i = 0, rows = matchesList.size(); i < rows; i++) {
            listOfGoodMatches.add(matchesList.get(i));
        }
        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(listOfGoodMatches);

        // 将图片格式转成API需要的格式
        Mat rgbMat1 = new Mat();
        Mat rgbMat2 = new Mat();
        Imgproc.cvtColor(mat1Copy, rgbMat1, Imgproc.COLOR_RGBA2RGB, 1);
        Imgproc.cvtColor(mat2Copy, rgbMat2, Imgproc.COLOR_RGBA2RGB, 1);

        // 联合两张图片，拼成一张
        Mat outputMat = new Mat();
        List<Mat> combineMatList = Arrays.asList(mat1Copy, mat2Copy);
        Core.hconcat(combineMatList, outputMat);

        // 将匹配线绘制到图像上
//        Features2d.drawMatches(thresholdMat1, matOfKeyPoint1, thresholdMat2, matOfKeyPoint2, goodMatches, outputMat);
        Features2d.drawMatches(rgbMat1, matOfKeyPoint1, rgbMat2, matOfKeyPoint2, goodMatches, outputMat);
        return outputMat;
    }
}