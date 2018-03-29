#include <jni.h>
#include <string>

#include <opencv2/imgproc/imgproc.hpp>  // Gaussian Blur
#include <opencv2/core/core.hpp>        // Basic OpenCV structures (cv::Mat, Scalar)
#include <opencv2/highgui/highgui.hpp>  // OpenCV window I/O

using namespace std;
using namespace cv;

extern "C"
JNIEXPORT jstring JNICALL
Java_io_weichao_opencv_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "初始化完毕";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_io_weichao_opencv_util_CompareUtil_nativeComparePSNR(JNIEnv *env, jobject /* this */, jlong addr1,
                                            jlong addr2) {
    Mat &I1 = *(Mat *) addr1;
    Mat &I2 = *(Mat *) addr2;

    cvtColor(I1, I1, CV_BGR2GRAY);
    cvtColor(I2, I2, CV_BGR2GRAY);

    Mat s1;
    absdiff(I1, I2, s1);       // |I1 - I2|
    s1.convertTo(s1, CV_32F);  // cannot make a square on 8 bits
    s1 = s1.mul(s1);           // |I1 - I2|^2

    Scalar s = sum(s1);        // sum elements per channel

    double sse = s.val[0] + s.val[1] + s.val[2]; // sum channels

    if (sse <= 1e-10) // for small values return zero
        return 0;
    else {
        double mse = sse / (double) (I1.channels() * I1.total());
        double psnr = 10.0 * log10((255 * 255) / mse);
        return psnr;
    }
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_io_weichao_opencv_util_CompareUtil_nativeCompareSSIM(JNIEnv *env, jobject /* this */, jlong addr1,
                                            jlong addr2) {
    const double C1 = 6.5025, C2 = 58.5225;
    /***************************** INITS **********************************/
    int d = CV_32F;

    Mat &i1 = *(Mat *) addr1;
    Mat &i2 = *(Mat *) addr2;

    cvtColor(i1, i1, CV_BGR2GRAY);
    cvtColor(i2, i2, CV_BGR2GRAY);

    Mat I1, I2;
    i1.convertTo(I1, d);            // cannot calculate on one byte large values
    i2.convertTo(I2, d);

    Mat I2_2 = I2.mul(I2);        // I2^2
    Mat I1_2 = I1.mul(I1);        // I1^2
    Mat I1_I2 = I1.mul(I2);        // I1 * I2

    /*************************** END INITS **********************************/

    Mat mu1, mu2;                   // PRELIMINARY COMPUTING
    GaussianBlur(I1, mu1, Size(11, 11), 1.5);
    GaussianBlur(I2, mu2, Size(11, 11), 1.5);

    Mat mu1_2 = mu1.mul(mu1);
    Mat mu2_2 = mu2.mul(mu2);
    Mat mu1_mu2 = mu1.mul(mu2);

    Mat sigma1_2, sigma2_2, sigma12;

    GaussianBlur(I1_2, sigma1_2, Size(11, 11), 1.5);
    sigma1_2 -= mu1_2;

    GaussianBlur(I2_2, sigma2_2, Size(11, 11), 1.5);
    sigma2_2 -= mu2_2;

    GaussianBlur(I1_I2, sigma12, Size(11, 11), 1.5);
    sigma12 -= mu1_mu2;

    ///////////////////////////////// FORMULA ////////////////////////////////
    Mat t1, t2, t3;

    t1 = 2 * mu1_mu2 + C1;
    t2 = 2 * sigma12 + C2;
    t3 = t1.mul(t2);                 // t3 = ((2*mu1_mu2 + C1).*(2*sigma12 + C2))

    t1 = mu1_2 + mu2_2 + C1;
    t2 = sigma1_2 + sigma2_2 + C2;
    t1 = t1.mul(t2);                 // t1 =((mu1_2 + mu2_2 + C1).*(sigma1_2 + sigma2_2 + C2))

    Mat ssim_map;
    divide(t3, t1, ssim_map);        // ssim_map =  t3./t1;

    Scalar mssim = mean(ssim_map);   // mssim = average of ssim map

    return mssim.val[0] + mssim.val[1] + mssim.val[2];
}

jlong calHammingDistance(Mat matSrc) {
    Mat matDst;
    resize(matSrc, matDst, Size(8, 8), 0, 0, INTER_CUBIC);
    cvtColor(matDst, matDst, CV_BGR2GRAY);

    int iAvg = 0;
    int arr[64];
    for (int i = 0; i < 8; i++) {
        uchar *data1 = matDst.ptr<uchar>(i);
        int tmp = i * 8;
        for (int j = 0; j < 8; j++) {
            int tmp1 = tmp + j;
            arr[tmp1] = data1[j] / 4 * 4;
            iAvg += arr[tmp1];
        }
    }
    iAvg /= 64;

    int p = 1;
    jlong value = 0;
    for (int i = 0; i < 64; i++) {
        p *= 2;
        if (arr[i] >= iAvg) {
            value += p;
        }
    }
    return value;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_io_weichao_opencv_util_CompareUtil_nativeComparePH(JNIEnv *env, jclass, jlong addr1, jlong addr2) {
    Mat &matSrc1 = *(Mat *) addr1;
    Mat &matSrc2 = *(Mat *) addr2;
    if (!matSrc1.data || !matSrc2.data) {
        return 0j;
    }

    jlong value1 = calHammingDistance(matSrc1);
    jlong value2 = calHammingDistance(matSrc2);
    return value1 - value2;
}