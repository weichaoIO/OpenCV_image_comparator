package io.weichao.opencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.List;

import io.weichao.opencv.util.BitmapUtil;
import io.weichao.opencv.util.CircleUtil;
import io.weichao.opencv.util.CompareUtil;
import io.weichao.opencv.util.ContourUtil;
import io.weichao.opencv.util.CornerUtil;
import io.weichao.opencv.util.EdgeUtil;
import io.weichao.opencv.util.ExtractUtil;
import io.weichao.opencv.util.LineUtil;
import io.weichao.opencv.util.PointUtil;
import io.weichao.opencv.util.ProcessUtil;
import io.weichao.opencv.util.RotateUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";

//    static {
//        OpenCVLoader.initDebug();
//        System.loadLibrary("opencv");
//    }

    private static final int REQUEST_WIDTH = 960;// 该值能压缩到多低取决于边框线有多粗
    private static final int REQUEST_HEIGHT = REQUEST_WIDTH;

    private Bitmap mBitmap1, mBitmap2;
    private TextView mTv;
    private ImageView mIv1, mIv2, mMatchesIv;
    private Button mCompareBtn, mCornerHarrisBtn, mEdgeCannyBtn, mEdgeSobelBtn, mEdgeGaussianBtn, mContoursBtn, mLineHoughBtn, mCircleHoughBtn, mRotateBtn, mCrossPointBtn, mGrayBtn, mThresholdBtn, mEqualizeHistBtn, mExtractBtn;
    private int mItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        Toast.makeText(this, stringFromJNI(), Toast.LENGTH_SHORT).show();
    }

    public void init() {
        mTv = findViewById(R.id.tv);
        mIv1 = findViewById(R.id.iv1);
        mIv2 = findViewById(R.id.iv2);
        mMatchesIv = findViewById(R.id.iv_matches);
        mCompareBtn = findViewById(R.id.btn_compare);
        mCornerHarrisBtn = findViewById(R.id.btn_corner_harris);
        mEdgeCannyBtn = findViewById(R.id.btn_edge_canny);
        mEdgeSobelBtn = findViewById(R.id.btn_edge_sobel);
        mEdgeGaussianBtn = findViewById(R.id.btn_edge_gaussian);
        mContoursBtn = findViewById(R.id.btn_contours);
        mLineHoughBtn = findViewById(R.id.btn_line_hough);
        mCircleHoughBtn = findViewById(R.id.btn_circle_hough);
        mRotateBtn = findViewById(R.id.btn_rotate);
        mCrossPointBtn = findViewById(R.id.btn_cross_point);
        mGrayBtn = findViewById(R.id.btn_gray);
        mThresholdBtn = findViewById(R.id.btn_threshold);
        mEqualizeHistBtn = findViewById(R.id.btn_equalize_hist);
        mExtractBtn = findViewById(R.id.btn_extract);
        mIv1.setOnClickListener(this);
        mIv2.setOnClickListener(this);
        mMatchesIv.setOnClickListener(this);
        mCompareBtn.setOnClickListener(this);
        mCornerHarrisBtn.setOnClickListener(this);
        mEdgeCannyBtn.setOnClickListener(this);
        mEdgeSobelBtn.setOnClickListener(this);
        mEdgeGaussianBtn.setOnClickListener(this);
        mContoursBtn.setOnClickListener(this);
        mLineHoughBtn.setOnClickListener(this);
        mCircleHoughBtn.setOnClickListener(this);
        mRotateBtn.setOnClickListener(this);
        mCrossPointBtn.setOnClickListener(this);
        mGrayBtn.setOnClickListener(this);
        mThresholdBtn.setOnClickListener(this);
        mExtractBtn.setOnClickListener(this);
        mEqualizeHistBtn.setOnClickListener(this);

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mBitmap1 = com.jsxfedu.sfyjs_android.util.BitmapUtil.createScaledBitmap(bitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, false);
        mIv1.setImageBitmap(mBitmap1);

        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mBitmap2 = com.jsxfedu.sfyjs_android.util.BitmapUtil.createScaledBitmap(bitmap2, REQUEST_WIDTH, REQUEST_HEIGHT, false);
        mIv2.setImageBitmap(mBitmap2);
    }

    @Override
    public void onClick(View v) {
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        if (v == mIv1) {
            changeItem(++mItem);
        } else if (v == mIv2) {
            resetIv2();
        } else {
            long timeStart = System.currentTimeMillis();
            switch (v.getId()) {
                case R.id.btn_compare:
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
                    BitmapUtil.bitmapToMat(bitmap, REQUEST_WIDTH, REQUEST_HEIGHT, mat1);
                    BitmapUtil.bitmapToMat(mBitmap2, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    compare(mat1, mat2);
                    break;
                case R.id.btn_corner_harris:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    cornerHarris(mat2);
                    break;
                case R.id.btn_edge_canny:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    edgeCanny(mat2);
                    break;
                case R.id.btn_edge_sobel:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    edgeSobel(mat2);
                    break;
                case R.id.btn_edge_gaussian:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    edgeGaussian(mat2);
                    break;
                case R.id.btn_contours:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    contours(mat2);
                    break;
                case R.id.btn_line_hough:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    lineHough(mat2);
                    break;
                case R.id.btn_circle_hough:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    circleHough(mat2);
                    break;
                case R.id.btn_rotate:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    rotate(mat2);
                    break;
                case R.id.btn_cross_point:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    crossPoint(mat2);
                    break;
                case R.id.btn_gray:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    gray(mat2);
                    break;
                case R.id.btn_threshold:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    threshold(mat2);
                    break;
                case R.id.btn_equalize_hist:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    equalizeHist(mat2);
                    break;
                case R.id.btn_extract:
                    BitmapUtil.bitmapToMat(mBitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, mat2);
                    extract(mat2);
                    break;
                case R.id.iv_matches:
                    mMatchesIv.setVisibility(View.GONE);
                    break;
            }
            long timeEnd = System.currentTimeMillis();
            Log.e(TAG, "耗时: " + (timeEnd - timeStart) + " ms");
        }
    }

    private void changeItem(int i) {
        Bitmap bitmap1 = null;
        switch (i % 7) {
            case 0:
                bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test);
                break;
            case 1:
                bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test1);
                break;
            case 2:
                bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
                break;
            case 3:
                bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test3);
                break;
            case 4:
                bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test4);
                break;
            case 5:
                bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test5);
                break;
            case 6:
                bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test6);
                break;
        }
        mBitmap1 = com.jsxfedu.sfyjs_android.util.BitmapUtil.createScaledBitmap(bitmap1, REQUEST_WIDTH, REQUEST_HEIGHT, false);
        mIv1.setImageBitmap(mBitmap1);
        resetIv2();
    }

    private void resetIv2() {
        mBitmap2 = mBitmap1.copy(mBitmap1.getConfig(), true);
        mIv2.setImageBitmap(mBitmap2);
    }

    /**
     * 比较两个矩阵的相似度
     *
     * @param mat1
     * @param mat2
     */
    public void compare(Mat mat1, Mat mat2) {
        if (mat1 == null || mat1.empty() || mat2 == null || mat2.empty()) {
            Log.e(TAG, "mat1 == null || mat1.empty() || mat2 == null || mat2.empty()");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        long ph = CompareUtil.comparePH(mat1, mat2);
        Log.e(TAG, "【感知哈希】相似度：" + ph);
        stringBuilder.append("【感知哈希】相似度：" + ph + "\n");

        double psnr = CompareUtil.comparePSNR(mat1, mat2);
        Log.e(TAG, "【峰值信噪比】相似度：" + psnr);
        stringBuilder.append("【峰值信噪比】相似度：" + psnr + "\n");

        double ssim = CompareUtil.compareSSIM(mat1, mat2);
        Log.e(TAG, "【结构相似性】相似度：" + ssim);
        stringBuilder.append("【结构相似性】相似度：" + ssim + "\n");

        double hist = CompareUtil.compareHist(mat1, mat2);
        Log.e(TAG, "【直方图】相似度：" + hist);
        stringBuilder.append("【直方图】相似度：" + hist + "\n");

        mTv.setText(stringBuilder.toString());

        Mat matchesMat = CompareUtil.matches(mat1, mat2);
        Bitmap matchesBitmap = Bitmap.createBitmap(matchesMat.cols(), matchesMat.rows(), Bitmap.Config.ARGB_8888);
        BitmapUtil.matToBitmap(matchesMat, matchesBitmap);
        mMatchesIv.setImageBitmap(matchesBitmap);
        mMatchesIv.setVisibility(View.VISIBLE);
    }

    private void cornerHarris(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat cornerMat = CornerUtil.cornerHarris(mat);
        BitmapUtil.matToBitmap(cornerMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void edgeCanny(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat edgeMat = EdgeUtil.edgeCanny(mat);
        BitmapUtil.matToBitmap(edgeMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void edgeSobel(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat edgeMat = EdgeUtil.edgeSobel(mat);
        BitmapUtil.matToBitmap(edgeMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void edgeGaussian(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat edgeMat = EdgeUtil.edgeGaussian(mat);
        BitmapUtil.matToBitmap(edgeMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void contours(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat contoursMat = ContourUtil.contours(mat);
        BitmapUtil.matToBitmap(contoursMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void lineHough(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        LineUtil.ContourLine contourLine = LineUtil.getContourLineHough(mat);
        Mat lineMat = LineUtil.contourLineHough(mat, contourLine);
        BitmapUtil.matToBitmap(lineMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void circleHough(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat circleMat = CircleUtil.circleHough(mat);
        BitmapUtil.matToBitmap(circleMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void rotate(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat rotateMat = RotateUtil.rotate(mat);
        BitmapUtil.matToBitmap(rotateMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void crossPoint(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        LineUtil.ContourLine contourLine = LineUtil.getContourLineHough(mat);
        List<Point> contourPointList = PointUtil.getContourPointList(contourLine);
        Mat pointMat = PointUtil.contourPointList(mat, contourPointList);
        BitmapUtil.matToBitmap(pointMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void gray(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat grayMat = ProcessUtil.gray(mat);
        BitmapUtil.matToBitmap(grayMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void threshold(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat thresholdMat = ProcessUtil.threshold(mat);
        BitmapUtil.matToBitmap(thresholdMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void equalizeHist(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        Mat equalizeHistMat = ProcessUtil.equalizeHist(mat);
        BitmapUtil.matToBitmap(equalizeHistMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    private void extract(Mat mat) {
        if (mat == null || mat.empty()) {
            Log.e(TAG, "mat == null || mat.empty()");
            return;
        }

        LineUtil.ContourLine contourLine = LineUtil.getContourLineHough(mat);
        List<Point> contourPointList = PointUtil.getContourPointList(contourLine);
        Mat thresholdMat = ProcessUtil.threshold(mat);
        Mat extractMat = ExtractUtil.warpPerspective(thresholdMat, contourPointList);
        BitmapUtil.matToBitmap(extractMat, mBitmap2);
        mIv2.setImageBitmap(mBitmap2);
    }

    public native String stringFromJNI();
}