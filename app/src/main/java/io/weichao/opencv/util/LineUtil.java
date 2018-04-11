package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by chao.wei on 2018/4/2.
 */
public class LineUtil {
    private static final String TAG = "LineUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private LineUtil() {
    }

    public static ContourLine getContourLineHough(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.d(TAG, "mat == null || mat.empty()");
            return null;
        }

        Mat matCopy = mat.clone();

        ContourLine contourLine = new ContourLine();
        contourLine.topLinePoints = getTopLinePoints(matCopy);
        if (contourLine.topLinePoints == null) {
            return null;
        }

        contourLine.bottomLinePoints = getBottomLinePoints(matCopy);
        if (contourLine.bottomLinePoints == null) {
            return null;
        }

        contourLine.leftLinePoints = getLeftLinePoints(matCopy);
        if (contourLine.leftLinePoints == null) {
            return null;
        }

        contourLine.rightLinePoints = getRightLinePoints(matCopy);
        if (contourLine.rightLinePoints == null) {
            return null;
        }

        return contourLine;
    }

    public static Mat contourLineHough(Mat mat, ContourLine contourLine) {
        if (mat == null || mat.empty() || contourLine == null) {
            Log.d(TAG, "mat == null || mat.empty() || contourLine == null");
            return null;
        }

        Mat matCopy = mat.clone();

        // 新建Mat结构图像用来把直线画在该Mat上
        Mat houghLines = new Mat();
        houghLines.create(matCopy.rows(), matCopy.cols(), CvType.CV_8UC1);
        Scalar scalar = new Scalar(255, 255, 255, 255);
        Imgproc.line(houghLines, contourLine.topLinePoints[0], contourLine.topLinePoints[1], scalar, 2);
        Imgproc.line(houghLines, contourLine.bottomLinePoints[0], contourLine.bottomLinePoints[1], scalar, 2);
        Imgproc.line(houghLines, contourLine.leftLinePoints[0], contourLine.leftLinePoints[1], scalar, 2);
        Imgproc.line(houghLines, contourLine.rightLinePoints[0], contourLine.rightLinePoints[1], scalar, 2);
        return houghLines;
    }

    private static Point[] getTopLinePoints(Mat mat) {
        Mat matCopy = mat.clone();

        Mat topMat = matCopy.submat(0, matCopy.rows() >> 1, 0, matCopy.cols());
        Mat edgeTopMat = EdgeUtil.edgeGaussian(topMat);
        Mat lineMat = new Mat();
        Imgproc.HoughLines(edgeTopMat, lineMat, 1, Math.PI / 180, matCopy.cols() >> 1);
        double data[] = lineMat.get(0, 0);
        if (data == null) {
            return null;
        }

        double rho = data[0];
        double theta = data[1];
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        double x = cosTheta * rho;
        double y = sinTheta * rho;
        Point[] points = new Point[2];
        points[0] = new Point(x + matCopy.cols() * (-sinTheta), y + matCopy.rows() * cosTheta);
        points[1] = new Point(x - matCopy.cols() * (-sinTheta), y - matCopy.rows() * cosTheta);
        return points;
    }

    private static Point[] getBottomLinePoints(Mat mat) {
        Mat matCopy = mat.clone();

        Mat bottomMat = matCopy.submat(matCopy.rows() >> 1, matCopy.rows(), 0, matCopy.cols());
        Mat edgeBottomMat = EdgeUtil.edgeGaussian(bottomMat);
        Mat lineMat = new Mat();
        Imgproc.HoughLines(edgeBottomMat, lineMat, 1, Math.PI / 180, matCopy.cols() >> 1);
        double data[] = lineMat.get(0, 0);
        if (data == null) {
            return null;
        }

        double rho = data[0];
        double theta = data[1];
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        double x = cosTheta * rho;
        double y = sinTheta * rho + (matCopy.rows() >> 1);
        Point[] points = new Point[2];
        points[0] = new Point(x + matCopy.cols() * (-sinTheta), y + matCopy.rows() * cosTheta);
        points[1] = new Point(x - matCopy.cols() * (-sinTheta), y - matCopy.rows() * cosTheta);
        return points;
    }

    private static Point[] getLeftLinePoints(Mat mat) {
        Mat matCopy = mat.clone();

        Mat leftMat = matCopy.submat(0, matCopy.rows(), 0, matCopy.cols() >> 1);
        Mat edgeLeftMat = EdgeUtil.edgeGaussian(leftMat);
        Mat lineMat = new Mat();
        Imgproc.HoughLines(edgeLeftMat, lineMat, 1, Math.PI / 180, matCopy.rows() >> 1);
        double data[] = lineMat.get(0, 0);
        if (data == null) {
            return null;
        }

        double rho = data[0];
        double theta = data[1];
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        double x = cosTheta * rho;
        double y = sinTheta * rho;
        Point[] points = new Point[2];
        points[0] = new Point(x + matCopy.cols() * (-sinTheta), y + matCopy.rows() * cosTheta);
        points[1] = new Point(x - matCopy.cols() * (-sinTheta), y - matCopy.rows() * cosTheta);
        return points;
    }

    private static Point[] getRightLinePoints(Mat mat) {
        Mat matCopy = mat.clone();

        Mat rightMat = matCopy.submat(0, matCopy.rows(), matCopy.cols() >> 1, matCopy.cols());
        Mat edgeRightMat = EdgeUtil.edgeGaussian(rightMat);
        Mat lineMat = new Mat();
        Imgproc.HoughLines(edgeRightMat, lineMat, 1, Math.PI / 180, matCopy.rows() >> 1);
        double data[] = lineMat.get(0, 0);
        if (data == null) {
            return null;
        }

        double rho = data[0];
        double theta = data[1];
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        double x = cosTheta * rho + (matCopy.cols() >> 1);
        double y = sinTheta * rho;
        Point[] points = new Point[2];
        points[0] = new Point(x + matCopy.cols() * (-sinTheta), y + matCopy.rows() * cosTheta);
        points[1] = new Point(x - matCopy.cols() * (-sinTheta), y - matCopy.rows() * cosTheta);
        return points;
    }

    public static class ContourLine {
        public Point[] topLinePoints;
        public Point[] bottomLinePoints;
        public Point[] leftLinePoints;
        public Point[] rightLinePoints;
    }
}