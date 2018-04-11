package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chao.wei on 2018/4/2.
 */
public class PointUtil {
    private static final String TAG = "PointUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private PointUtil() {
    }

    public static List<Point> getContourPointList(LineUtil.ContourLine contourLine) {
        if (contourLine == null) {
            Log.e(TAG, "contourLine == null");
            return null;
        }

        Point crossPoint1 = getCrossPoint(contourLine.leftLinePoints[0], contourLine.leftLinePoints[1], contourLine.bottomLinePoints[0], contourLine.bottomLinePoints[1]);
        Point crossPoint2 = getCrossPoint(contourLine.leftLinePoints[0], contourLine.leftLinePoints[1], contourLine.topLinePoints[0], contourLine.topLinePoints[1]);
        Point crossPoint3 = getCrossPoint(contourLine.rightLinePoints[0], contourLine.rightLinePoints[1], contourLine.topLinePoints[0], contourLine.topLinePoints[1]);
        Point crossPoint4 = getCrossPoint(contourLine.rightLinePoints[0], contourLine.rightLinePoints[1], contourLine.bottomLinePoints[0], contourLine.bottomLinePoints[1]);
        if (crossPoint1 == null || crossPoint2 == null || crossPoint3 == null || crossPoint4 == null) {
            Log.e(TAG, "crossPoint1 == null || crossPoint2 == null || crossPoint3 == null || crossPoint4 == null");
            return null;
        }

        List<Point> pointList = new ArrayList<>();
        pointList.add(crossPoint1);
        pointList.add(crossPoint2);
        pointList.add(crossPoint3);
        pointList.add(crossPoint4);
        return pointList;
    }

    public static Mat contourPointList(Mat mat, List<Point> contourPointList) {
        if (mat == null || mat.empty() || contourPointList == null) {
            Log.e(TAG, "mat == null || mat.empty() || contourPointList == null");
            return null;
        }

        Mat matCopy = mat.clone();

        Scalar scalar = new Scalar(255, 0, 0, 255);
        for (Point point : contourPointList) {
            Imgproc.circle(matCopy, point, 10, scalar, -1);
        }
        return matCopy;
    }

    public static Point getCrossPoint(Point line1Point1, Point line1Point2, Point line2Point1, Point line2Point2) {
        if (line1Point1 == null || line1Point2 == null || line2Point1 == null || line2Point2 == null) {
            Log.e(TAG, "line1Point1 == null || line1Point2 == null || line2Point1 == null || line2Point2 == null)");
            return null;
        }

        int deltaX1 = (int) (line1Point1.x - line1Point2.x);
        int deltaY1 = (int) (line1Point1.y - line1Point2.y);
        int deltaX2 = (int) (line2Point1.x - line2Point2.x);
        int deltaY2 = (int) (line2Point1.y - line2Point2.y);
        if (deltaX1 * deltaY2 == deltaX2 * deltaY1) {
            return null;
        }

        if (line1Point1.x == line1Point2.x) {
            if (line2Point1.y == line2Point2.y) {
                return new Point(line1Point1.x, line2Point1.y);
            }

            float line2K = getK(line2Point1, line2Point2);
            float line2B = getB(line2Point1, line2Point2);
            float y = (float) (line2K * line1Point1.x + line2B);
            return new Point(line1Point1.x, y);
        } else if (line2Point1.x == line2Point2.x) {
            if (line1Point1.y == line1Point2.y) {
                return new Point(line2Point1.x, line1Point1.y);
            }

            float line1K = getK(line1Point1, line1Point2);
            float line1B = getB(line1Point1, line1Point2);
            float y = (float) (line1K * line2Point1.x + line1B);
            return new Point(line2Point1.x, y);
        }

        float line1K = getK(line1Point1, line1Point2);
        float line1B = getB(line1Point1, line1Point2);
        float line2K = getK(line2Point1, line2Point2);
        float line2B = getB(line2Point1, line2Point2);
        float x = (int) ((line2B - line1B) / (line1K - line2K));
        float y = line1K * x + line1B;
        return new Point(x, y);
    }

    private static float getK(Point point1, Point point2) {
        return (float) ((point2.y - point1.y) / (point2.x - point1.x));
    }

    private static float getB(Point point1, Point point2) {
        return (float) ((point1.y * point2.x - point2.y * point1.x) / (point2.x - point1.x));
    }
}