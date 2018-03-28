package io.weichao.opencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Used to load the 'opencv' library on application startup.
    static {
        OpenCVLoader.initDebug();
        System.loadLibrary("opencv");
    }

    public static final String TAG = "OpenCv_compare";
    private Bitmap mBitmap1, mBitmap2;
    private ImageView mIv_ImageView1, mIv_ImageView2;
    private Button mBtn_compare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        Toast.makeText(this, stringFromJNI(), Toast.LENGTH_SHORT).show();
    }

    public void init() {
        mIv_ImageView1 = findViewById(R.id.iv_img1);
        mIv_ImageView2 = findViewById(R.id.iv_img2);
        mBtn_compare = findViewById(R.id.btn_compare);
        mBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
        mIv_ImageView1.setImageBitmap(mBitmap1);
        mIv_ImageView2.setImageBitmap(mBitmap2);
        mBtn_compare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Mat mat11 = new Mat();
        Mat mat22 = new Mat();
        Utils.bitmapToMat(mBitmap1, mat1);
        Utils.bitmapToMat(mBitmap2, mat2);
        Imgproc.cvtColor(mat1, mat11, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2, mat22, Imgproc.COLOR_BGR2GRAY);
        comPareHist(mat11, mat22);
    }

    /**
     * 比较来个矩阵的相似度
     *
     * @param srcMat
     * @param dstMat
     */
    public void comPareHist(Mat srcMat, Mat dstMat) {
        if (srcMat.empty() || dstMat.empty()) {
            return;
        }

        double psnr = getPSNR(srcMat.getNativeObjAddr(), dstMat.getNativeObjAddr());
        Log.e(TAG, "【峰值信噪比】相似度：" + psnr);

        double ssim = getSSIM(srcMat.getNativeObjAddr(), dstMat.getNativeObjAddr());
        Log.e(TAG, "【结构相似性】相似度：" + ssim);

        srcMat.convertTo(srcMat, CvType.CV_32F);
        dstMat.convertTo(dstMat, CvType.CV_32F);
        float hist = (float) Imgproc.compareHist(srcMat, dstMat, Imgproc.CV_COMP_CORREL);
        Log.e(TAG, "【直方图】相似度：" + hist);
    }

    public native String stringFromJNI();

    public native double getPSNR(long mat1, long mat2);

    public native double getSSIM(long mat1, long mat2);
}