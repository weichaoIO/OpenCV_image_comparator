package io.weichao.opencv.util;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chao.wei on 2018/3/29.
 */
public class ExtractUtil {
    private static final String TAG = "ExtractUtil";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private ExtractUtil() {
    }

    public static Mat warpPerspective(Mat mat, List<Point> contourPointList) {
        if (mat == null || mat.empty() || contourPointList == null || contourPointList.size() < 4) {
            Log.d(TAG, "mat == null || mat.empty() || contourPointList == null || contourPointList.size() < 4");
            return null;
        }

        Mat startM = Converters.vector_Point2f_to_Mat(contourPointList);
        Mat matCopy = mat.clone();

        int resultWidth = matCopy.width();
        int resultHeight = matCopy.height();
        List<Point> dest = new ArrayList<>();
        dest.add(new Point(0, resultHeight));
        dest.add(new Point(0, 0));
        dest.add(new Point(resultWidth, 0));
        dest.add(new Point(resultWidth, resultHeight));
        Mat endM = Converters.vector_Point2f_to_Mat(dest);
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);
        Imgproc.warpPerspective(matCopy, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight), Imgproc.INTER_CUBIC);
        return outputMat;
    }
}