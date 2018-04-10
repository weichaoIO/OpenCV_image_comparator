package io.weichao.opencv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.zxing.client.android.CameraActivity;

import org.opencv.android.OpenCVLoader;

/**
 * Created by chao.wei on 2018/3/28.
 */
public class FirstActivity extends AppCompatActivity {
        static {
            OpenCVLoader.initDebug();
            System.loadLibrary("opencv");
        }

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,  // 读取存储
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 写入存储
            Manifest.permission.CAMERA                  // 拍照
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        requestPermissions();
    }

    public void change2Main(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void change2Camera(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED) {
                // Android 6.0 申请权限
                requestPermissions(PERMISSIONS, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                    || grantResults[1] != PackageManager.PERMISSION_GRANTED
                    || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                FirstActivity.this.finish();
            }
        }
    }
}